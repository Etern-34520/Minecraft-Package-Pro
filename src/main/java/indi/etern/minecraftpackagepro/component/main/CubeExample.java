package indi.etern.minecraftpackagepro.component.main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CubeExample extends Application {
    private static final double WALK_SIN_STRETCH = .3;
    private static final double WALK_SIN_SHIFT = 1.2;
    private static final double TURN_VELOCITY = 45;
    private static final double MOVE_VELOCITY = 200;
    private static final double WALK_CYCLE = 5;
    private static final double WALK_LOWER = 20;
    private static final double WALK_UPPER = 0;
    
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    private static int collidingState(double beforeMin, double beforeMax, double objMin, double objMax,
                                      double move) {
        double min = beforeMin + move;
        double max = beforeMax + move;
        
        if (max > objMin && objMax > min) //Intersecting
        {
            double maxOutside = max - objMax;
            double minOutside = objMin - min;
            
            if (move == 0 || maxOutside == minOutside || maxOutside <= 0 && minOutside <= 0)
                return 1; //Receding
            else
                return maxOutside < minOutside ^ move < 0 ? 2 /*Advancing*/ : 1 /*Receding*/;
        } else
            return 0; //No intersection
    }
    
    private static Box createBox(double x, double y, double z, double width, double height, double depth, Color c) {
        Box cube = new Box(width, height, depth);
        cube.setTranslateX(x + width / 2);
        cube.setTranslateY(y + height / 2);
        cube.setTranslateZ(z + depth / 2);
        cube.setMaterial(new PhongMaterial(c));
        return cube;
    }
    
    private static Group createOpenCube(int width, int height, int depth, int thickness) {
        Group cube = new Group();
        cube.getChildren().addAll(createBox(0, 0, 0, thickness, height, depth, Color.ALICEBLUE),
                createBox(0, 0, 0, width, thickness, depth, Color.GREEN),
                createBox(0, 0, 0, width, height, thickness, Color.ALICEBLUE),
                createBox(width, 0, 0, thickness, height, depth, Color.ALICEBLUE),
                createBox(0, height, 0, width, thickness, depth, Color.DARKBLUE),
                createBox(0, 0, depth, width, height, thickness, Color.ALICEBLUE));
        return cube;
    }
    
    private static void rotate360(Rotate r, Duration cycleDuration) {
        Timeline rotating = new Timeline();
        rotating.getKeyFrames().addAll(new KeyFrame(Duration.seconds(0), new KeyValue(r.angleProperty(), 0)),
                new KeyFrame(cycleDuration, new KeyValue(r.angleProperty(), 360)));
        rotating.setCycleCount(-1);
        rotating.playFromStart();
    }
    
    private boolean forward = false;
    private boolean backward = false;
    private boolean up = false;
    private boolean down = false;
    private boolean left = false;
    private boolean right = false;
    private double before = -1;
    private double walkFrame = 0;
    private final Sphere cameraBody = new Sphere(50);
    private final Rotate elevate = new Rotate(-40, Rotate.X_AXIS);
    private final Rotate heading = new Rotate(0, Rotate.Y_AXIS);
    private final Translate pos = new Translate(-800, -900, -800);
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final ArrayList<Shape3D> solidBodies = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            Alert support = new Alert(AlertType.ERROR);
            support.setTitle("3D Test");
            support.setContentText("This computer does not support Scene3D.");
            support.showAndWait();
            System.exit(1);
        }
        
        primaryStage.setResizable(true);
        
        Group cube = createOpenCube(2000, 1000, 2000, 10);
        addSolidBodies(cube);
        
        Box box = createBox(5000,0,1000,500,500,500,Color.RED);
        box.setRotate(45);
        box.setRotationAxis(new Point3D(1,1,0));
        addSolidBodies(box);
        final PhongMaterial material = new PhongMaterial();
        Image image = new Image("file:F:\\Minecraft\\resourcePacks\\minecraftDefaultPack_1.8.9\\minecraft\\textures\\blocks\\bedrock.png");
//        Image image = new Image("file:C:\\Users\\zzc\\Desktop\\bedrock_high.png");
        image = zoomRealSize(10,image);
        WritableImage writableImage = new WritableImage(image.getPixelReader(), 16, 16);
        material.setDiffuseMap(image);
//        box.setClip();
        material.setSpecularPower(0);
        box.setMaterial(material);
        
        Box box1 = createBox(500,100,0,500,500,0,Color.ALICEBLUE);
        box1.setRotate(45);
        box1.setRotationAxis(new Point3D(1,1,0));
        addSolidBodies(box1);
        final PhongMaterial material1 = new PhongMaterial();
        material1.setDiffuseMap(image);
//        material1.setSpecularPower(0);
        box1.setMaterial(material1);
        
        
        box1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(mouseEvent.getX()+","+mouseEvent.getY());
            }
        });
        
        Box x = createBox(0,0,0,1000,5,5,Color.BLUE);
        Box y = createBox(0,0,0,5,1000,5,Color.RED);
        Box z = createBox(0,0,0,5,5,1000,Color.GREEN);
        
        PointLight light = new PointLight(Color.WHITE);
        Group cameraGroup = new Group();
        
        // Create and position camera
        // rotate360(xRotate);
//        camera.setFieldOfView(78);
        camera.setFieldOfView(78);
        camera.setFarClip(10000);
        camera.setVerticalFieldOfView(false);
        camera.setRotate(0);
        camera.setRotationAxis(Rotate.Y_AXIS);
        cameraGroup.getChildren().addAll(camera, light, cameraBody);
        cameraGroup.getTransforms().addAll(pos, elevate, heading);
        addSolidBodies(cameraBody);
        
        // Build the Scene Graph
        Group root = new Group();
//        root.getChildren().addAll(cameraGroup, testBox, cube, crystal);
        root.getChildren().addAll(cameraGroup, box1, box, x, y, z);
        Scene scene = new Scene(root, 1000, 1000, true, SceneAntialiasing.BALANCED);
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(mouseEvent.getX()+","+mouseEvent.getY());
            }
        });
        scene.setCamera(camera);
        scene.setFill(Color.GRAY);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
            switch (evt.getCode()) {
                case W -> forward = true;
                case A -> left = true;
                case S -> backward = true;
                case D -> right = true;
                case Q -> down = true;
                case E -> up = true;
            }
        });
        primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, evt -> {
            switch (evt.getCode()) {
                case W -> forward = false;
                case A -> left = false;
                case S -> backward = false;
                case D -> right = false;
                case Q -> down = false;
                case E -> up = false;
            }
        });
        AnimationTimer tick = new AnimationTimer() {
            
            @Override
            public void handle(long xx) {
                double now = System.currentTimeMillis() * .001;
                if (before != -1)
                    act(now - before);
                before = now;
            }
            
        };
        tick.start();
        primaryStage.show();
    }
    
    private void act(double time) {
        if (left ^ right) {
            double angle = heading.getAngle();
            double newAngle = angle + time * TURN_VELOCITY * (right ? 1 : -1);
            heading.setAngle(newAngle - Math.floor(newAngle / 360) * 360);
            elevate.setAxis(heading.transform(Rotate.X_AXIS));
        }
        
        if (forward ^ backward) {
            double sin = Math.sin(Math.toRadians(heading.getAngle()));
            double cos = Math.cos(Math.toRadians(heading.getAngle()));
            double walkAngle = walkFrame * Math.PI * 2;
            double dist = (Math.sin(walkAngle) * WALK_SIN_STRETCH + WALK_SIN_SHIFT) * MOVE_VELOCITY * time
                    * (forward ? 1 : -1);
            
            if (move(cameraBody, pos, new Point3D(sin * dist, 0, cos * dist))) {
                camera.setTranslateY((Math.sin(walkAngle) + 1.0) * (WALK_UPPER - WALK_LOWER) + WALK_LOWER);
                walkFrame += time * (forward ? 1 : -1);
                walkFrame -= Math.floor(walkFrame / WALK_CYCLE) * WALK_CYCLE;
            }
        }
        
        if (up ^ down) {
            double angle = elevate.getAngle();
            double newAngle = angle + time * TURN_VELOCITY * (up ? 1 : -1);
            elevate.setAngle(Math.min(90, Math.max(-90, newAngle)));
        }
    }
    
    private void addSolidBodies(Node body) {
        ArrayList<Node> tests = new ArrayList<>();
        tests.add(body);
        while (!tests.isEmpty()) {
            Node object = tests.remove(tests.size() - 1);
            if (object instanceof Shape3D)
                solidBodies.add((Shape3D) object);
            else if (object instanceof Parent)
                for (Node child : ((Parent) object).getChildrenUnmodifiable())
                    tests.add(child);
        }
    }
    
    private boolean move(Shape3D mover, Translate pos, Point3D vector) {
        double oldX = pos.getX(), oldY = pos.getY(), oldZ = pos.getZ();
        
        if (solidBodies.contains(mover)) {
            Bounds moverBounds = mover.localToScene(mover.getBoundsInLocal());
            for (Shape3D other : solidBodies) {
                if (other == mover)
                    continue;
                Bounds otherBounds = other.localToScene(other.getBoundsInLocal());
                int xColliding = collidingState(moverBounds.getMinX(), moverBounds.getMaxX(), otherBounds.getMinX(),
                        otherBounds.getMaxX(), vector.getX());
                int yColliding = collidingState(moverBounds.getMinY(), moverBounds.getMaxY(), otherBounds.getMinY(),
                        otherBounds.getMaxY(), vector.getY());
                int zColliding = collidingState(moverBounds.getMinZ(), moverBounds.getMaxZ(), otherBounds.getMinZ(),
                        otherBounds.getMaxZ(), vector.getZ());
                
                boolean intersecting = xColliding > 0 && yColliding > 0 && zColliding > 0;
                if (xColliding + yColliding + zColliding > 3) {
                    pos.setX(oldX);
                    pos.setY(oldY);
                    pos.setZ(oldZ);
                    return false;
                }
            }
        }
        
        pos.setX(oldX + vector.getX());
        pos.setY(oldY + vector.getY());
        pos.setZ(oldZ + vector.getZ());
        return true;
    }
    /*private Image zoomImage(Image image,int n){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth() * n, bufferedImage.getHeight() * n, bufferedImage.getType());
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, bufferedImage.getWidth() * n, bufferedImage.getHeight() * n, null);
        return newBufferedImage;
    }*/
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