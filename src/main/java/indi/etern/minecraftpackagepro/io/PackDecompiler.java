package indi.etern.minecraftpackagepro.io;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("removal")
public class PackDecompiler {
    private File minecraftJar;
    private HashMap<String, String> fileMap = new HashMap<>();
    private String putPath;
    private int progress = 0;
    private int indexLength = 0;
    private int readLength = 0;
    private File minecraftPath;
    private String version;
    public int over1 = 0;
    public boolean over2 = false;
    private String mainVersion;
    public boolean reachException = false;
    private final List<Thread> threads = new ArrayList<>();
    private boolean JarDirect;
    
    public List<Thread> getThreads() {
        return threads;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public boolean isReachException() {
        return reachException;
    }

    private void reachException() {
        reachException = true;
    }
    
    
    public PackDecompiler(File minecraftPath, String version, String putPath) {
        try {
            String regVersion = "(\\d)+\\.+(\\d)+(\\.+(\\d))?";//x.x.x
            if (version.matches(regVersion)) {
                System.out.println("Version pass:" + "\"" + version + "\"");
                if (minecraftPath.isDirectory()) {
                    this.minecraftPath = minecraftPath;
                    this.version = version;
                    this.putPath = putPath;
                    System.out.println("Path pass:" + "\"" + minecraftPath.getAbsolutePath() + "\"");
                    String[] jsonVersion = version.split("\\.");
                    mainVersion = jsonVersion[0] + "." + jsonVersion[1];
                }
            }
        } catch (Exception e) {
            reachException();
            e.printStackTrace();
        }
    }
    
    boolean libraries = true;
    boolean jar = true;
    
    public void librariesAble(boolean set) {
        libraries = set;
    }
    
    public void jarAble(boolean set) {
        jar = set;
    }

    public void cancel() {
        progress = 0;
        List<Thread> threads = this.getThreads();
        for (Thread thread : threads) {
            thread.stop();
        }
        System.out.println("canceled");
    }
    public void pause() {
        List<Thread> threads = this.getThreads();
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                synchronized (this) {
//                    thread.suspend();
                    try {
                        thread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void reStart() {
        List<Thread> threads = this.getThreads();
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                synchronized (this) {
//                    thread.resume();
                    thread.notify();
                }
            }
        }
    }

    public void start() {
        if (jar){
            JarThread jarThread = new JarThread();
            threads.add(jarThread);
            jarThread.start();
        }
        if (libraries){
            HashThread hashThread = new HashThread();
            threads.add(hashThread);
            hashThread.start();
        }
        if ((!libraries)&&jar){
            over2 = true;
        }
        if ((!jar) && libraries) {
            progress = 100;
            over1 = 5;
        }
    }
    
    public boolean isOver() {
        return (over1 == 5) && over2;
    }
    
    public void zipUncompress(String inputFile, String destDirPath) throws Exception {
        File srcFile = new File(inputFile);
        if (!srcFile.exists()) {
            throw new Exception(srcFile.getPath() + ":文件不存在");
        }
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(srcFile);
        } catch (Exception e) {
            reachException();
            e.printStackTrace();
            return;
        }
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.isDirectory()) {
                System.out.println(entry);
            }
            if (entry.getName().startsWith("assets")) {
                if (!entry.isDirectory()) {
                    String path = entry.getName().replace("assets/", "");
                    path = path.replace("/", "\\");
                    File targetFile = new File(destDirPath + "\\" + path);
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    if (!targetFile.exists()) {
                        targetFile.createNewFile();
                        InputStream is = zipFile.getInputStream(entry);
                        FileOutputStream fos = new FileOutputStream(targetFile);
                        int len;
                        byte[] buf = new byte[4096];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.close();
                        is.close();
                    }
                }
            }
        }
        over1++;
    }
    
    private class HashThread extends Thread {
        @Override
        public void run() {
            try {
                fileMap = (HashMap<String, String>) new ResourceIndex(new File(minecraftPath.getAbsolutePath() + "\\assets"), mainVersion).getResourceMap();//Use Minecraft's code from Mojang to prevent bugs :(
                indexLength = fileMap.size();
                File objects = new File(minecraftPath.getAbsolutePath() + "\\assets\\objects");
                System.out.println("Read information done");

                indexLength = fileMap.size();
                System.out.println("size:" + indexLength);
                filesDirs(objects);
                over2 = true;
                System.out.println("Hash over");
            } catch (Exception e) {
                reachException();
                e.printStackTrace();
            }
        }
    }

    public void filesDirs(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                assert files != null;
                for (File flies2 : files) {
                    filesDirs(flies2);
                }
            } else {
                //System.out.println("???????"+file);
                try {
                    //System.out.println(fileMap.get(file.getName()));
                    if (fileMap.get(file.getName()) != null) {
                        readLength++;
                        progress = readLength * 100 / indexLength;

                        File put = new File(putPath + "\\" + fileMap.get(file.getName()).replace(':', '\\'));
                        put.getParentFile().mkdirs();
                        FileUtils.copyFile(file, put);
                        //copyFile(file,put);
                    }
                } catch (IOException e) {
                    reachException();
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("反混淆assets完成");
        }
    }
    
    private class JarThread extends Thread {
        @Override
        public void run() {
            System.out.println("Unzip Minecraft jar");
            try {
                for (int i = 0; i < 5; i++) {
                    new Thread(() -> {
                        try {
                            if (JarDirect) {
                                zipUncompress(minecraftJar.getPath(), putPath);
                            } else {
                                zipUncompress(minecraftPath.getAbsolutePath() + "\\versions\\" + version + "\\" + version + ".jar", putPath);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            } catch (Exception e) {
                reachException();
                e.printStackTrace();
            }
        }
    }
    
}