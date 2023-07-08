package indi.etern.minecraftpackagepro.io.indexScanner;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import indi.etern.minecraftpackagepro.io.packTree.FileTree.tsFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelReader {
    static int t = 0;
    static double[] position = new double[6];
    private Cube_old cubeOld = new Cube_old();
    List<Cube_old> cubeOlds = new ArrayList<>();
    String lastName;
    File packPath;
    public ModelReader(File packPath) {
        this.packPath = packPath;
    }
    
    private void switchName(String name, String information) {
        switch (name) {
            case "__comment":
                break;
            case "elements":
                break;
            case "from":
                cubeOlds.add(cubeOld);
                cubeOld = new Cube_old();
                break;
            case "to":
                try {
                    cubeOld.setFrom(position[0], position[1], position[2]);
                } catch (IndexOutOfBoundsException ignored) {
                }
                break;
            case "faces":
                try {
                    cubeOld.setTo(position[3], position[4], position[5]);
                } catch (IndexOutOfBoundsException ignored) {
                }
                break;
            case "texture":
                break;
            case "parent":
                cubeOld.setParent(packPath , information);
                break;
            case "up", "top":
                cubeOld.setFaceTexture(Cube_old.Face.up , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "down", "bottom":
                cubeOld.setFaceTexture(Cube_old.Face.down , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "north":
                cubeOld.setFaceTexture(Cube_old.Face.north , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "south":
                cubeOld.setFaceTexture(Cube_old.Face.south , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "east", "right":
                cubeOld.setFaceTexture(Cube_old.Face.east , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "west", "left":
                cubeOld.setFaceTexture(Cube_old.Face.west , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "all":
                cubeOld.setFaceTexture(Cube_old.Face.all , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "side":
                cubeOld.setFaceTexture(Cube_old.Face.west , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                cubeOld.setFaceTexture(Cube_old.Face.east , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
            case "end":
                cubeOld.setFaceTexture(Cube_old.Face.north , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                cubeOld.setFaceTexture(Cube_old.Face.south , new tsFile(packPath + "\\minecraft\\textures\\" + information + ".png"));
                break;
        }
    }
    
    public List<Cube_old> getCubesFrom(String path) throws IOException {
        FileInputStream fin = new FileInputStream(path);
        InputStreamReader reader = new InputStreamReader(fin);
        handleJsonObject(new JsonReader(reader));
        reader.close();
        fin.close();
        return cubeOlds;
    }
    
    private void handleJsonObject(JsonReader reader) throws IOException {
        reader.beginObject();
        int a = 0;
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            a++;
            if (token.equals(JsonToken.BEGIN_ARRAY)) {
                handleJsonArray(reader);
            } else if (token.equals(JsonToken.NAME)) {
                lastName = reader.nextName();
            } else if (token.equals(JsonToken.STRING)) {
                String information = reader.nextString();
                if (lastName != null) {
                    switchName(lastName, information);
                }
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleJsonObject(reader);
            } else {
                reader.skipValue();
                //break
            }
        }
    }
    
    private void handleJsonArray(JsonReader reader) throws IOException {
        List<Double> numbers = new ArrayList<>();
        reader.beginArray();
        while (true) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                eqaqlsArrayName(lastName, numbers);
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleJsonObject(reader);
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else if (token.equals(JsonToken.NUMBER)) {
                double number = reader.nextDouble();
                numbers.add(number);
//                System.out.println(number);
            } else if (token.equals(JsonToken.NAME)) {
                lastName = reader.nextName();
                if (lastName.equals("north")||
                        lastName.equals("south")||
                        lastName.equals("west")||
                        lastName.equals("east")||
                        lastName.equals("up")||
                        lastName.equals("down")||
                        lastName.equals("all")){
                    faceName = lastName;
                }
            } else {
                reader.skipValue();
            }
        }
        
    }
    String faceName;
    Double[] tempUV = new Double[4];
    Map<String, Double[]> uvMap = new HashMap<>();
    private void eqaqlsArrayName(String name, List<Double> numbers) {
        switch (name) {
            case "elements":
            case "from":
//                if (position.size() == 0 )break;
                position[0] = numbers.get(0);
                position[1] = numbers.get(1);
                position[2] = numbers.get(2);
                break;
            case "to":
//                if (position.size() == 0 )break;
                position[3] = numbers.get(0);
                position[4] = numbers.get(1);
                position[5] = numbers.get(2);
                break;
            case "uv":
                for (int i = 0; i < 4; i++) {
                    tempUV[i] = numbers.get(i);
                }
                uvMap.put(faceName, tempUV);
                break;
        }
    }
    
}
