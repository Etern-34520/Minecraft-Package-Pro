package indi.etern.minecraftpackagepro.dataBUS;

import java.util.HashMap;

public class Setting {
    //TODO 没写完
    static HashMap<Object,Object> settingMap=new HashMap<>();
    private Setting(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            //TODO 序列化到文件
        });
    }
    //单例类实现————————————————————————————————————————————————————————
    private static class Holder{
        static Setting setting=new Setting();
    }
    public Setting getInstance(){
        return Holder.setting;
    }
    //—————————————————————————————————————————————————————————————————
    public static Object get(Object key){
        return settingMap.get(key);
    }
    
}
