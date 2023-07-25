package indi.etern.minecraftpackagepro.dataBUS.model;

import indi.etern.minecraftpackagepro.component.main.WorkBenchLauncher;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.List;

public class Cube {
    public String __comment;
    public double[] from;
    public double[] to;
    public Faces faces = new Faces();
    private transient Model belongModel;
    private transient List<Group> cube;
    
    void setBelongModel(Model belongModel) {
        this.belongModel = belongModel;
    }
    
    public List<Face> getFaces() {
        return new ArrayList<>(){{add(faces.down);add(faces.up);add(faces.north);add(faces.south);add(faces.west);add(faces.east);}};
    }
    
    public List<Group> toGroups(){
        cube = new ArrayList<>();
        double width = to[0] - from[0];
        double height = to[1] - from[1];
        double depth = to[2] - from[2];
        double x = from[0];
        double y = from[1];
        double z = from[2];
        Box faceDown = new Box(width,0,depth);
        faceDown.setTranslateX(x + width / 2);
        faceDown.setTranslateY(y);
        faceDown.setTranslateZ(z + depth / 2);
        addMaterialTo(faceDown,faces.down);
        
        Box faceUp = new Box(width,0,depth);
        faceUp.setTranslateX(x + width / 2);
        faceUp.setTranslateY(y + height);
        faceUp.setTranslateZ(z + depth / 2);
        addMaterialTo(faceUp,faces.up);
        
        Box faceNorth = new Box(width,height,0);
        faceNorth.setTranslateX(x + width / 2);
        faceNorth.setTranslateY(y + height / 2);
        faceNorth.setTranslateZ(z);
        addMaterialTo(faceNorth,faces.north);
        
        Box faceSouth = new Box(width,height,0);
        faceSouth.setTranslateX(x + width / 2);
        faceSouth.setTranslateY(y + height / 2);
        faceSouth.setTranslateZ(z + depth);
        addMaterialTo(faceSouth,faces.south);
        
        Box faceWest = new Box(0,height,depth);
        faceWest.setTranslateX(x);
        faceWest.setTranslateY(y + height / 2);
        faceWest.setTranslateZ(z + depth / 2);
        addMaterialTo(faceWest,faces.west);
        
        Box faceEast = new Box(0,height,depth);
        faceEast.setTranslateX(x + width);
        faceEast.setTranslateY(y + height / 2);
        faceEast.setTranslateZ(z + depth / 2);
        addMaterialTo(faceEast,faces.east);
        
        return cube;
    }
    private void addMaterialTo(Box FaceBox,Face face){
        final PhongMaterial material = new PhongMaterial();
        try {
            String blockTextureURI;
            String blockTextureName = belongModel.textures.get(face.texture.substring(1));
            if (face.texture.startsWith("#")) {
                while (blockTextureName.startsWith("#")){
                    blockTextureName = belongModel.textures.get(blockTextureName.substring(1));
                }
            }
            blockTextureURI = WorkBenchLauncher.fileTree.getIndexScanner().getBlockTextureURIOfInsideName(blockTextureName);
            
            material.setDiffuseMap(zoomRealSize(25,new Image(blockTextureURI)));
            FaceBox.setMaterial(material);
            cube.add(new Group(FaceBox));
        } catch (Exception ignored) {}
    }
    private Image zoomRealSize(int zoomN,Image image){
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage((int) (zoomN*image.getWidth()), (int) (zoomN*image.getHeight()));
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < image.getHeight()*zoomN; y++) {
            for (int x = 0; x < image.getWidth()*zoomN; x++) {
                int argb = pixelReader.getArgb(x / zoomN, y / zoomN);
                pixelWriter.setArgb(x, y, argb);
            }
        }
        return writableImage;
    }
}
