package indi.etern.minecraftpackagepro.component.view3D;

import indi.etern.minecraftpackagepro.dataBUS.model.Model;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.concurrent.atomic.AtomicBoolean;

public class ModelView extends Pane {
    PerspectiveCamera camera = new PerspectiveCamera();
//    PointLight light = new PointLight(Color.WHITE);
//    private final Sphere cameraSphere = new Sphere(50);
    
    Group root = new Group();
    Group groupX = new Group();
    Group groupY = new Group();
    Group currentModelGroup;
    
    Rotate rotationX, rotationY;
    public ModelView() {
        camera.setFieldOfView(78);
        camera.setFarClip(10000);
        camera.setVerticalFieldOfView(false);
        camera.setRotate(0);
        camera.setRotationAxis(Rotate.Y_AXIS);
        
        AmbientLight ambientLight = new AmbientLight();
        groupY.getChildren().add(ambientLight);
        groupY.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
        
        /*this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            backgroundTranslate.setX(getWidth()/2);
            backgroundTranslate.setZ(getHeight()/2);
        });*/
        
        Box x = createBox(0,0,0,100,1,1,Color.RED);
        Box y = createBox(0,0,0,1,100,1,Color.GREEN);
        Box z = createBox(0,0,0,1,1,100,Color.BLUE);
        
        groupY.getChildren().addAll(x,y,z);
        
        SubScene subScene = new SubScene(root, 600, 400, true, SceneAntialiasing.BALANCED);
//        JMetro jMetro = new JMetro();
//        jMetro.setScene(subScene.getScene());
        this.getChildren().add(subScene);
        Platform.runLater(() -> {
            subScene.setCamera(camera);
        });
        subScene.widthProperty().bind(this.widthProperty());
        subScene.heightProperty().bind(this.heightProperty());
        
        //only test
        /*Box box = createBox(0,0,0,50,50,50,Color.RED);
//        box.setRotate(45);
//        box.setRotationAxis(new Point3D(1,1,0));
        final PhongMaterial material = new PhongMaterial();
        Image image = new Image("file:F:\\Minecraft\\resourcePacks\\minecraftDefaultPack_1.8.9\\minecraft\\textures\\blocks\\bedrock.png");
        image = zoomRealSize(30,image);
        material.setDiffuseMap(image);
        material.setSpecularPower(0);
        box.setMaterial(material);*/
        
//        box.setMaterial(new PhongMaterial(Color.BLUE));
//        groupY.getChildren().add(box);
        groupX.getChildren().add(groupY);
        root.getChildren().add(groupX);
        
        rotationX = new Rotate(0, Rotate.X_AXIS);
        rotationY = new Rotate(0, Rotate.Y_AXIS);
        Rotate rotationZ = new Rotate(180, Rotate.Z_AXIS);
        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            try {
                root.getTransforms().remove(0);
            } catch (IndexOutOfBoundsException ignored){}
            root.getTransforms().add(0,new Translate(subScene.getWidth()/2, subScene.getHeight()/2));
        });
        groupX.getTransforms().add(rotationX);
        groupY.getTransforms().add(rotationY);
        groupY.getTransforms().add(rotationZ);
        
        AtomicBoolean move = new AtomicBoolean(false);
        this.setOnMousePressed(event -> {
            /*if (event.getButton() == MouseButton.MIDDLE) {
                // Set the pivot point of the rotation to the current mouse position
                rotationX.setPivotX(event.getSceneX());
                rotationX.setPivotY(event.getSceneY());
                rotationY.setPivotX(event.getSceneX());
                rotationY.setPivotY(event.getSceneY());
            } else {*/
                anchorX = event.getSceneX();
                anchorY = event.getSceneY();
                anchorAngleX = rotationX.getAngle();
                anchorAngleY = rotationY.getAngle();
                move.set(event.getButton() == MouseButton.SECONDARY);
//            }
        });
        
        this.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.SECONDARY && move.get()) {
                camera.setTranslateX(camera.getTranslateX() + (anchorX - event.getSceneX())*(500-camera.getTranslateZ())/500);
                camera.setTranslateY(camera.getTranslateY() + (anchorY - event.getSceneY())*(500-camera.getTranslateZ())/500);
                anchorX = event.getSceneX();
                anchorY = event.getSceneY();
            } else if (event.getButton() != MouseButton.MIDDLE) {
                rotationX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
                if (rotationX.getAngle() > 90) rotationX.setAngle(90);
                else if (rotationX.getAngle() < -90) rotationX.setAngle(-90);
                rotationY.setAngle(anchorAngleY + anchorX - event.getSceneX());
            }
        });
        
        this.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? -50 : 50;
            final double translateZ = camera.getTranslateZ() + zoomFactor;
//            if (translateZ < -1000) camera.setTranslateZ(-1000);
//            else if (translateZ > 250) camera.setTranslateZ(250);
//            else camera.setTranslateZ(translateZ);
//            System.out.println(camera.getTranslateZ());
            camera.setTranslateZ(translateZ);
        });
    }
    
    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;
    
    private static Box createBox(double x, double y, double z, double width, double height, double depth, Color c) {
        Box cube = new Box(width, height, depth);
//        cube.setTranslateX(x - width / 2);
//        cube.setTranslateY(y - height / 2);
//        cube.setTranslateZ(z - depth / 2);
        cube.setMaterial(new PhongMaterial(c));
        return cube;
    }
    
    public void setModel(Model model) {
        try {
            groupY.getChildren().remove(currentModelGroup);
        } catch (NullPointerException ignored){}
        currentModelGroup = model.toGroup();
        groupY.getChildren().add(currentModelGroup);
    }
}
