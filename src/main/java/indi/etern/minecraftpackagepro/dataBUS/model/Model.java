package indi.etern.minecraftpackagepro.dataBUS.model;

import indi.etern.minecraftpackagepro.component.main.WorkBenchLauncher;
import javafx.scene.Group;

import java.util.List;
import java.util.Map;

public class Model {
    public Cube[] elements;
    String parent;
    private transient Model parentModel = null;
    public Map<String,String> textures;
    @Override
    public String toString() {
        return "Model{" + "\n      " +
                "elements=" + elements + "\n      " +
                ", parent='" + parent + '\'' + "\n     " +
                ", textures=" + textures + "\n" +
                '}' + "\n";
    }
    
    public Group toGroup(){
        Group group = new Group();
        Map<String, String> oldParentTextures = null;
        if (elements == null) {
            try{
                if (parentModel == null)
                    parentModel = WorkBenchLauncher.fileTree.getIndexScanner().getModelOfName(parent.replace("block/",""));
            } catch (NullPointerException ignored){}
            oldParentTextures = parentModel.textures;//back up
            if (parentModel.textures!=null){
                for (String key : textures.keySet()) {
                    parentModel.textures.put(key, textures.get(key));
                }
            } else {
                parentModel.textures = textures;
            }
            group = parentModel.toGroup();
        } else {
            for (Cube cube : elements) {
                cube.setBelongModel(this);
                final List<Group> cubeGroup = cube.toGroups();
                group.getChildren().addAll(cubeGroup);
            }
        }
        if (elements == null) {
            textures = oldParentTextures;
        }
        return group;
    }
    
}
