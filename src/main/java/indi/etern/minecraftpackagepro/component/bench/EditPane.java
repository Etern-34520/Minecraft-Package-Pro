package indi.etern.minecraftpackagepro.component.bench;

import indi.etern.minecraftpackagepro.component.main.WorkBenchLauncher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.MeshView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditPane extends Pane {
    @FXML
    MeshView modelView;
    Canvas canvas;
    double imageDrawWidth = 100;
    WritableImage canvasImage;
    Tab tab;
    final double[] location = new double[4];
    public EditPane() {
        try {
            FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("")+"resources/"+getClass().getSimpleName()+".fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void drawAlphaBackground(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        if (location[2]==0|location[3]==0){
            return;
        }
        double size = (Math.max(location[2], location[3]))/32;
        boolean isWhite;
        int x = 0;
        for (int i = (int) location[0]; i <= location[0]+location[2]; i+=size) {
            x++;
            isWhite = x % 2 == 0;
            for (int j = (int) location[1]; j <= location[1]+location[3]; j+=size) {
                if (isWhite){
                    gc.setFill(javafx.scene.paint.Color.WHITE);
                    isWhite = false;
                } else {
                    gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                    isWhite = true;
                }
                gc.fillRect(i,j,size,size);
            }
        }
        gc.clearRect(0,0,location[0],canvas.getHeight());
        gc.clearRect(0,0,canvas.getWidth(),location[1]);
        gc.clearRect(location[0]+location[2],0,canvas.getWidth()-location[0]-location[2],canvas.getHeight());
        gc.clearRect(0,location[1]+location[3],canvas.getWidth(),canvas.getHeight()-location[1]-location[3]);
    }
    
    public MeshView getMeshView() {
        return modelView;
    }
    
    public void zoomSize(ScrollEvent event) {
        double zoomedDeltaY = event.getDeltaY()/2;
        double mousePointX = event.getX();
        double mousePointY = event.getY();
        if (event.isAltDown()){
            zoomedDeltaY *= 4;
        }
        double newWidth = location[2]*(1+zoomedDeltaY/100);
        double newHeight = location[3]*(1+zoomedDeltaY/100);
        double newX = canvas.getLayoutX() - (newWidth-canvas.getWidth())*(mousePointX/canvas.getWidth());
        double newY = canvas.getLayoutY() - (newHeight-canvas.getHeight())*(mousePointY/canvas.getHeight());
        if (newWidth<canvas.getWidth()/10|newHeight<canvas.getHeight()/10){
            return;
        }
//        if (newWidth<picturesPane.getMinWidth()|newHeight<picturesPane.getMinHeight()){
//            return;
//        }
//        picturesPane.setLayoutX(picturesPane.getLayoutX() - (newWidth-canvas.getWidth())/2);
//        picturesPane.setLayoutY(picturesPane.getLayoutY() - (newHeight-canvas.getHeight())/2);
//        canvas.setWidth(newWidth);
//        canvas.setHeight(newHeight);
        imageDrawWidth = newWidth;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        location[0] = newX;
        location[1] = newY;
        location[2] = newWidth;
        location[3] = newHeight;
        drawAlphaBackground();
//        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.drawImage(canvasImage, newX, newY, newWidth, newHeight);
    }
    public void uploadPicture(File file) {
        if (file != null && file.isFile()) {
            try {
                FileInputStream input = new FileInputStream(file);
                Image image = new Image(input);
                String name = file.getName();
                String suffix = null;
                for (String i : name.split("\\.")) suffix = i;
				if (name.split("\\.").length==1) suffix="null";
                String[] allowedSuffix = new String[]{"jpg","png","bmp","gif"};
				boolean unSupport = true;
				for (String s : allowedSuffix) {
					assert suffix != null;
					if (suffix.toLowerCase().equals(s)) {
						tab = new Tab(name, this);
                        
                        unSupport = false;
					}
				}
				if (unSupport) System.out.println("["+ WorkBenchLauncher.getTimeSinceStart()+"s]not support suffix:"+suffix);
				else {
                    tab.selectedProperty().addListener((observableValue, isSelected, t1) -> {
                        if (isSelected) {
                            getChildren().removeAll(getChildren());
//                            getChildren().remove(canvas);
                            canvas = null;
                        } else if (canvasImage!=null){
                            Platform.runLater(() -> createCanvasAndDraw(canvasImage));
                        }
                    });
                    canvasImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
//                    createCanvasAndDraw(canvasImage);
                }
			} catch (IOException e) {
				String message=e.getMessage();
				if(message.endsWith("(拒绝访问。)")) {
					System.out.println(message);
				} else {
					e.printStackTrace();
				}
				
			}
		}
		
		//new EditPane().waitingFinish();
	}
    
    private void createCanvasAndDraw(WritableImage writableImage) {
        double imageDrawHeight = imageDrawWidth * writableImage.getHeight() / writableImage.getWidth();
        Canvas canvas = new Canvas(imageDrawWidth, imageDrawHeight);
        this.getChildren().add(canvas);
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            try {
                reDrawOriginalSize();
            } catch (NullPointerException ignored){
            }
        });
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        canvas.setOnScroll(this::zoomSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        location[0] = (getWidth()-imageDrawWidth)/2;
        location[1] = (getHeight()-imageDrawHeight)/2;
        location[2] = imageDrawWidth;
        location[3] = imageDrawHeight;
        Platform.runLater(() -> {
            drawAlphaBackground();
            gc.drawImage(writableImage,
                    (getWidth()-imageDrawWidth)/2, (getHeight()-imageDrawHeight)/2,
                    imageDrawWidth, imageDrawHeight);
        }
        );
        final double[] cx = new double[1];
        final double[] cy = new double[1];
        AtomicBoolean showMenu = new AtomicBoolean(false);
        canvas.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.SECONDARY)){
                showMenu.set(true);
                cx[0] = event.getX();
                cy[0] = event.getY();
                new Thread(() -> {
                    try {
                        Thread.sleep(10);
                        if (showMenu.get()){
                            //TODO 这里以后写menu组件
                            System.out.println("show menu");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        });
        canvas.setOnMouseDragged(event -> {
            if(event.getButton().equals(MouseButton.SECONDARY)) {
                showMenu.set(false);
                double moveX = event.getX();
                double moveY = event.getY();
                location[0] = location[0] + moveX - cx[0];
                location[1] = location[1] + moveY - cy[0];
                final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
                drawAlphaBackground();
                graphicsContext2D.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
                graphicsContext2D.drawImage(canvasImage,
                        location[0],location[1],location[2],location[3]);
            }
        });
        this.canvas = canvas;
        canvasImage = writableImage;
    }
    
    public Tab toTab() {
        return tab;
    }
    
    public void reDrawOriginalSize() {
        while (canvasImage==null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double imageDrawHeight = imageDrawWidth * canvasImage.getHeight() / canvasImage.getWidth();
        location[0] = (getWidth()-imageDrawWidth)/2;
        location[1] = (getHeight()-imageDrawHeight)/2;
        location[2] = imageDrawWidth;
        location[3] = imageDrawHeight;
        final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        drawAlphaBackground();
//        graphicsContext2D.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        graphicsContext2D.drawImage(canvasImage,
                location[0],location[1],location[2],location[3]);
    }
}
