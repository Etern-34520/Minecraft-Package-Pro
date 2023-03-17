package indi.etern.minecraftpackagepro.component.bench;

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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.MeshView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditPane extends Pane {
    @FXML
    FlowPane picturesPane;
    @FXML
    MeshView modelView;
    Canvas canvas;
    double size = 100;
    WritableImage canvasImage;
    Tab tab;
    
    public EditPane() {
        try {
            FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("")+"resources/"+getClass().getSimpleName()+".fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            picturesPane.setMinSize(20,20);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public MeshView getMeshView() {
        return modelView;
    }
    
    public FlowPane getPictureView() {
        return picturesPane;
    }
    
    @FXML
    public void zoomSize(ScrollEvent event) {
        double zoomedDeltaY = event.getDeltaY()/2;
        if (event.isAltDown()){
            zoomedDeltaY *= 4;
        }
        double newWidth = canvas.getWidth()*(1+zoomedDeltaY/100);
        double newHeight = canvas.getHeight()*(1+zoomedDeltaY/100);
        if (newWidth<picturesPane.getMinWidth()|newHeight<picturesPane.getMinHeight()){
            return;
        }
        //TODO 缩放有问题
        picturesPane.setLayoutX(picturesPane.getLayoutX() - (newWidth-canvas.getWidth())/2);
        picturesPane.setLayoutY(picturesPane.getLayoutY() - (newHeight-canvas.getHeight())/2);
        canvas.setWidth(newWidth);
        canvas.setHeight(newHeight);
        size += zoomedDeltaY;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.drawImage(canvasImage, 0, 0, newWidth, newHeight);
    }
    
    public void uploadPicture(File file) {
        if (file != null && file.isFile()) {
            try {
                FileInputStream input;
                input = new FileInputStream(file);
                Image image = new Image(input);
                String name = file.getName();
                String suffix = null;
                for (String i : name.split("\\.")) suffix = i;
				if (name.split("\\.").length==1) suffix="null";
                List<String> allowedSuffix = new ArrayList<>();
				allowedSuffix.add("jpg");
				allowedSuffix.add("png");
				allowedSuffix.add("bmp");
				allowedSuffix.add("gif");
				boolean unSupport = true;
				for (String s : allowedSuffix) {
					assert suffix != null;
					if (suffix.toLowerCase().equals(s)) {
						tab = new Tab(name, this);
						unSupport = false;
					}
				}
				if (unSupport) System.out.println("not support suffix:"+suffix);
				else {
                    WritableImage writableImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
                    Canvas canvas = new Canvas(size, size * image.getHeight() / image.getWidth());
                    picturesPane.getChildren().add(canvas);
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.setImageSmoothing(false);
                    Platform.runLater(() -> gc.drawImage(writableImage, 0, 0, size, size * image.getHeight() / image.getWidth()));
                    final double[] cx = new double[1];
                    final double[] cy = new double[1];
                    AtomicBoolean showMenu = new AtomicBoolean(false);
                    picturesPane.setOnMousePressed(event -> {
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
                    picturesPane.setOnMouseDragged(event -> {
                        if(event.getButton().equals(MouseButton.SECONDARY)) {
                            showMenu.set(false);
                            double originalX = picturesPane.getLayoutX();
                            double originalY = picturesPane.getLayoutY();
                            double moveX = event.getX();
                            double moveY = event.getY();
                            double y = originalY + moveY - cy[0];
                            double x = originalX + moveX - cx[0];
                            picturesPane.relocate(x, y);
                            System.out.println(x + "," + y);
                        }
                    });
                    this.canvas = canvas;
                    canvasImage = writableImage;
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
		public Tab toTab() {
			return tab;
		}
}
