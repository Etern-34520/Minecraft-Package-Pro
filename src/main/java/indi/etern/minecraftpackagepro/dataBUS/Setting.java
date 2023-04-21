package indi.etern.minecraftpackagepro.dataBUS;

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
        System.out.println(config.getAbsolutePath());
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
    public void put(Object key,Object value){settingMap.put(key, value);}
    
}
