package indi.etern.minecraftpackagepro.io.indexScanner;

import com.google.gson.Gson;
import indi.etern.minecraftpackagepro.dataBUS.model.Cube;
import indi.etern.minecraftpackagepro.dataBUS.model.Face;
import indi.etern.minecraftpackagepro.dataBUS.model.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class IndexScanner {
    private final Map<String, List<Model>> blockTexture2ModelsMap = new HashMap<>();
    private final Map<String, Item> itemTexture2ModelMap = new HashMap<>();
    
    public List<Model> getModelsOf(File texture){
        final String[] splitTemp = texture.getAbsolutePath().split("textures");
        final String insideName = splitTemp[1].substring(1, splitTemp[1].length()-4).replace('\\','/');//remove ".png" & replace '\' to '/'
        return blockTexture2ModelsMap.get(insideName);
    }
    public IndexScanner(File packPath) {
        if (new File(packPath,"assets").exists()){
            packPath = new File(packPath,"assets");
        }
        File blockModelFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\models\\block");
        File itemModelFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\models\\item");
        File blockTextureFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\textures\\block");
        File itemTextureFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\textures\\item");
        if (!blockModelFolder.exists()) {
            throw new RuntimeException("block model folder not found");
        }
        for (File blockModelFile : Objects.requireNonNull(blockModelFolder.listFiles())) {
            try {
                Model model = new Gson().fromJson(new FileReader(blockModelFile), Model.class);
                List<Model> models;
                if (model.elements != null)
                    for (Cube cube : model.elements) {
                        List<Face> faceList = cube.getFaces();
                            for (Face face:faceList) {
                                if (face != null && face.texture != null) {
                                    models = blockTexture2ModelsMap.get(face.texture);
                                    if (models == null) {
                                        models = new ArrayList<>();
                                        models.add(model);
                                        blockTexture2ModelsMap.put(face.texture,models);
                                    } else if (!models.contains(model)) {
                                        models.add(model);
                                    }
                                }
                            }
                    }
                if (model.textures!=null){
                    List<String> textures = new ArrayList<>(){{
                        add(model.textures.texture);
                        add(model.textures.particle);
                        add(model.textures.all);
                    }};
                    for (String texture:textures){
                        if (texture!=null){
                            models = blockTexture2ModelsMap.get(texture);
                            if (models == null) {
                                models = new ArrayList<>();
                                models.add(model);
                                blockTexture2ModelsMap.put(texture,models);
                            } else if (!models.contains(model)) {
                                models.add(model);
                            }
                        }
                    }
                }
                
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }
}
