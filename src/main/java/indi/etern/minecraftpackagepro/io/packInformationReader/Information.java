package indi.etern.minecraftpackagepro.io.packInformationReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Information {
    public String getDescription() {
        return description;
    }
    
    private String description;
    private short packFormat;
    public Information(String packPath) throws IOException {
        if (!packPath.endsWith("\\")) packPath+="\\";
        File packMeta = new File(packPath+"pack.mcmeta");
        FileInputStream fileInputStream = new FileInputStream(packMeta);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        JsonReader jsonReader = new JsonReader(inputStreamReader);
        Gson gson = new Gson();
        Map<?,?> map = (Map<?, ?>) ((Map<?, ?>) gson.fromJson(jsonReader, HashMap.class)).get("pack");
        description =(String) map.get("description");
        packFormat = ((Double) map.get("pack_format")).shortValue();
    }
    
    public short getPackFormat() {
        return packFormat;
    }
    
    public static void main(String[] args) {
        try {
            Information information = new Information("D:\\minecraftDefaultPack_1.12.2");
            System.out.println(information.getDescription());
            System.out.println(information.getPackFormat());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
