package indi.etern.minecraftpackagepro.dataBUS.model;

public class Model {
    public Cube[] elements;
    String parent;
    public Textures textures;
    @Override
    public String toString() {
        return "Model{" + "\n      " +
                "elements=" + elements + "\n      " +
                ", parent='" + parent + '\'' + "\n     " +
                ", textures=" + textures + "\n" +
                '}' + "\n";
    }
}
