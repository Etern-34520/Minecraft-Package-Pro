package indi.etern.minecraftpackagepro.io.indexScanner;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class IndexScanner {
    private final Map<String, List<Cube>> blockTexture2ModelsMap = new HashMap<>();
    private final Map<String, Item> itemTexture2ModelMap = new HashMap<>();
    
    public List<Cube> getCubesOf(File texture){
        return blockTexture2ModelsMap.get(texture.getName());
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
                ModelReader modelReader = new ModelReader(packPath);
                Cube cube = modelReader.Read(blockModelFile.getAbsolutePath());
                cube.setName(blockModelFile.getName().substring(0, blockModelFile.getName().length() - 5));
                for (Cube.Face face : Cube.Face.values()) {
                    List<Cube> cubes = blockTexture2ModelsMap.get(cube.getTextureOf(face));
                    if (cubes == null) {
                        cubes = new ArrayList<>();
                        cubes.add(cube);
                        if (cube.getTextureOf(face)!=null&&cube.getTextureOf(face).startsWith("#")){
                        
                        }
                        if (cube.getTextureOf(face)!=null) blockTexture2ModelsMap.put(cube.getTextureOf(face), cubes);
                    } else if (!cubes.contains(cube)) {
                        cubes.add(cube);
                    }
                }
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
        }
    }
    
    
}
