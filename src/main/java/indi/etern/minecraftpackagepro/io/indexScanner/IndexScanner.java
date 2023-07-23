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
    private final Gson gson = new Gson();
    private final File blockModelFolder;
    private final File itemModelFolder;
    private final File blockTextureFolder;
    private final File itemTextureFolder;
    
    public List<Model> getModelsOf(File texture){
        final String[] splitTemp = texture.getAbsolutePath().split("textures");
        try {
            final String insideName = splitTemp[1].substring(1, splitTemp[1].length()-4).replace('\\','/');//remove ".png" & replace '\' to '/'
            return blockTexture2ModelsMap.get(insideName);
        } catch (IndexOutOfBoundsException ignored){
            //not a used texture
            return null;
        }
    }
    public Model getModelOfName(String name){
        try {
            return gson.fromJson(new FileReader(new File(blockModelFolder,name+".json")), Model.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public IndexScanner(File packPath) {
        if (new File(packPath,"assets").exists()){
            packPath = new File(packPath,"assets");
        }
        blockModelFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\models\\block");
        itemModelFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\models\\item");
        blockTextureFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\textures\\blocks");
        itemTextureFolder = new File(packPath.getAbsolutePath() + "\\minecraft\\textures\\items");
        if (!blockModelFolder.exists()) {
            throw new RuntimeException("block model folder not found");
        }
        for (File blockModelFile : Objects.requireNonNull(blockModelFolder.listFiles())) {
            try {
                Model model = gson.fromJson(new FileReader(blockModelFile), Model.class);
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
                    for (String texture:model.textures.values()){
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
    
    public String getBlockTextureURIOfInsideName(String texture) {
        return "file:" + blockTextureFolder.getAbsolutePath()+ "\\" + texture.replace("blocks/","") + ".png";
    }
}
