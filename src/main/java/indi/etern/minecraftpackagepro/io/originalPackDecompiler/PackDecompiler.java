package indi.etern.minecraftpackagepro.io.originalPackDecompiler;

import indi.etern.minecraftpackagepro.component.tasks.TaskPane;
import indi.etern.minecraftpackagepro.component.tools.decompiler.ProgressPane;
import indi.etern.minecraftpackagepro.io.fromMCP.ResourceIndex;
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

@SuppressWarnings({"removal", "ResultOfMethodCallIgnored"})
public class PackDecompiler {
    private File minecraftJar;
    private HashMap<String, String> fileMap = new HashMap<>();
    private String putPath;
    private int progress = 0;
    private int indexLength = 0;
    private int readLength = 0;
    private File minecraftPath;
    private String version;
    private byte overUncompress = 0;
    private boolean over1 = false;
    private boolean over2 = false;
    private boolean stop = false;
    private String mainVersion;
    private boolean reachException = false;
    private final List<Thread> threads = new ArrayList<>();
    private boolean JarDirect;
    private ProgressPane progressPane;
    private TaskPane taskPane;
    private final List<Exception> exceptions = new ArrayList<>();
    
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
    
    
    public PackDecompiler(File minecraftPath,File minecraftJar, String version, String putPath) {
        try {
            this.minecraftPath = minecraftPath;
            this.version = version;
            this.putPath = putPath;
            this.minecraftJar = minecraftJar;
            JarDirect = true;
            System.out.println("Path pass:" + "\"" + minecraftPath.getAbsolutePath() + "\"");
            String[] jsonVersion = version.split("\\.");
            mainVersion = jsonVersion[0] + "." + jsonVersion[1];
        } catch (Exception e) {
            reachException();
            e.printStackTrace();
            exceptions.add(e);
        }
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
            exceptions.add(e);
        }
    }
    
    private boolean libraries = true;
    private boolean jar = true;
    
    public void librariesAble(boolean set) {
        libraries = set;
    }
    
    public void jarAble(boolean set) {
        jar = set;
    }

    public void cancel() {
        progress = 0;
        stop=true;
        System.out.println("canceled");
    }
    public void pause() {
        List<Thread> threads = this.getThreads();
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                synchronized (this) {
                    thread.suspend();
                }
            }
        }
    }
    public void reStart() {
        List<Thread> threads = this.getThreads();
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                synchronized (this) {
                    thread.resume();
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
//            overUncompress = 5;
            over1 = true;
        }
    }
    
    public boolean isOver() {
//        return ((overUncompress==5) && over2);
        return over1 && over2;
    }
    
    public List<Exception> getExceptions() {
        return exceptions;
    }
    
    public void zipUncompress(String inputFile, String destDirPath) throws Exception {
        File srcFile = new File(inputFile);
        if (!srcFile.exists()) {
            throw new IOException(srcFile.getPath() + ":文件不存在");
        }
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(srcFile);
        } catch (Exception e) {
            reachException();
            e.printStackTrace();
            exceptions.add(e);
            return;
        }
        Enumeration<?> entries = zipFile.entries();
        while (!stop&&entries.hasMoreElements()) {
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
//        overUncompress++;
        over1 = true;
    }
    
    public void setProgressPane(ProgressPane progressPane) {
        this.progressPane = progressPane;
    }
    public void setTaskPane(TaskPane taskPane) {
        this.taskPane = taskPane;
    }

    public boolean isStop() {
        return stop;
    }
    
    public String getVersion() {
        return version;
    }
    
    //Change to nio
    private class HashThread extends Thread {
        @Override
        public void run() {
            try {
                fileMap = (HashMap<String, String>) new ResourceIndex(
                        new File(minecraftPath.getAbsolutePath() + "\\assets")
                        , mainVersion
                ).getResourceMap();//Use Minecraft's code from Mojang to prevent bugs :(
                indexLength = fileMap.size();
                File objects = new File(minecraftPath.getAbsolutePath() + "\\assets\\objects");
//                progressPane.tip("Hash读取完毕，开始反混淆");
                taskPane.setTip("Hash读取完毕", -1);
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                    progressPane.tip("");
                    taskPane.setTip("反混淆中", -1);
                }).start();
                System.out.println(version+":getCubesFrom information done");
                indexLength = fileMap.size();
                System.out.println(version+"MapSize:" + indexLength);
                filesDirs(objects);
                over2 = true;
                System.out.println(version+":Hash over");
            } catch (Exception e) {
                reachException();
                e.printStackTrace();
                exceptions.add(e);
            }
        }
    }

    public void filesDirs(File file) {
        if (!stop&&file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                assert files != null;
                for (File flies2 : files) {
                    filesDirs(flies2);
                }
            } else {
                try {
                    //System.out.println(fileMap.get(file.getName()));
                    if (fileMap.get(file.getName()) != null) {
                        readLength++;
                        progress = readLength * 100 / indexLength;

                        File put = new File(putPath + "\\" + fileMap.get(file.getName()).replace(':', '\\'));
                        put.getParentFile().mkdirs();
                        FileUtils.copyFile(file, put);
                    }
                } catch (IOException e) {
                    reachException();
                    e.printStackTrace();
                    exceptions.add(e);
                }
            }
        } else if(!stop) {
            System.out.println("反混淆assets完成");
        }
    }
    
    private class JarThread extends Thread {
        @Override
        public void run() {
            System.out.println("Unzip Minecraft jar");
            try {
//                for (int i = 0; i < 5; i++) {
                    new Thread(() -> {
                        try {
                            if (JarDirect) {
                                zipUncompress(minecraftJar.getPath(), putPath);
                            } else {
                                zipUncompress(minecraftPath.getAbsolutePath() + "\\versions\\" + version + "\\" + version + ".jar", putPath);
                            }
                        } catch (Exception e) {
                            reachException();
                            e.printStackTrace();
                            exceptions.add(e);
                        }
                    }).start();
//                }
            } catch (Exception e) {
                reachException();
                e.printStackTrace();
                exceptions.add(e);
            }
        }
    }
    
}