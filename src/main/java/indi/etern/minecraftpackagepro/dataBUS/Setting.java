package indi.etern.minecraftpackagepro.dataBUS;

import indi.etern.minecraftpackagepro.component.main.WorkBenchLauncher;

import java.io.*;
import java.util.HashMap;

public class Setting {
    //TODO 没写完
    static HashMap<Object,Object> settingMap=new HashMap<>();
    private Setting() throws IOException {
        final String CONFIG_PATH = "options.config";
        File config = new File(CONFIG_PATH);
        if (!config.exists()) {
            config.createNewFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_PATH))) {
                oos.writeObject(settingMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("["+ WorkBenchLauncher.getTimeSinceStart()+ "s][info][setting] read options from: \"" + config.getAbsolutePath() + "\"");
        if (config.exists()&&config.canRead()&& config.canWrite()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CONFIG_PATH))) {
                settingMap = (HashMap<Object, Object>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_PATH))) {
                    oos.writeObject(settingMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } else {
//            throw new FileNotFoundException(CONFIG_PATH);
        }
    }
    //单例类实现————————————————————————————————————————————————————————
    private static class Holder{
        private static final Setting setting;
        
        static {
            try {
                setting = new Setting();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static Setting getInstance(){
        return Holder.setting;
    }
    //—————————————————————————————————————————————————————————————————
    public Object get(Object key){
        return settingMap.get(key);
    }
    public void flush(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("options.config"))) {
            oos.writeObject(settingMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getInt(Object key) throws ClassCastException{
        return (int)settingMap.get(key);
    }
    public double getDouble(Object key) throws ClassCastException{
        return (double)settingMap.get(key);
    }
    public float getFloat(Object key) throws ClassCastException{
        return (float)settingMap.get(key);
    }
    public boolean getBoolean(Object key) throws ClassCastException{
        return (boolean)settingMap.get(key);
    }
    public String getString(Object key) throws ClassCastException{
        return (String)settingMap.get(key);
    }
    public long getLong(Object key) throws ClassCastException{
        return (long)settingMap.get(key);
    }
    public byte getByte(Object key) throws ClassCastException{
        return (byte)settingMap.get(key);
    }
    public short getShort(Object key) throws ClassCastException{
        return (short)settingMap.get(key);
    }
    public char getChar(Object key) throws ClassCastException{
        return (char)settingMap.get(key);
    }
    public void put(Object key,Object value){settingMap.put(key, value);}
    
}
