package indi.etern.minecraftpackagepro.component.tools.decompiler;

import indi.etern.minecraftpackagepro.io.PackDecompiler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;

public class ProgressPane extends VBox {
    private ImageView pauseButtonImageView;
    private PackDecompiler packDecompiler;
    @FXML
    private Label versionLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button cancelButton;
    @FXML
    private Button pauseButton;
    String version;
    boolean paused = false;
    String pauseImage1Path;
    String pauseImage2Path;
    boolean showProgress = true;
    public void onlyIndeterminateProgress(){
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }
    
    ProgressPane(PackDecompiler packDecompiler, String version) {
        try {
            FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("")+"resources/"+getClass().getSimpleName()+".fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            pauseImage1Path = String.valueOf(new URL(getClass().getResource("")+"resources/pause1.png"));
            pauseImage2Path = String.valueOf(new URL(getClass().getResource("")+"resources/pause2.png"));
            String stopImagePath = String.valueOf(new URL(getClass().getResource("")+"resources/stop.png"));
            pauseButtonImageView = new ImageView(pauseImage2Path);
            pauseButton.setGraphic(pauseButtonImageView);
            cancelButton.setGraphic(new ImageView(stopImagePath));
            this.version=version;
            this.versionLabel.setText(version);
            this.packDecompiler = packDecompiler;
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            TilePane.setMargin(this,new Insets(20,0,0,20));
            VBox.setVgrow(this, Priority.ALWAYS);
            VBox.setMargin(this,new Insets(8));
            Thread flasher = new Thread(() -> {
                while (!packDecompiler.isOver()) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (packDecompiler.isReachException()) {
                        tip("遇到错误");
                        break;
                    }
                    if (showProgress) {
                        progressBar.setProgress(packDecompiler.getProgress() / 100.0);
                    }
                    if (!showProgress&&packDecompiler.getProgress() == 100) {
                        showProgress = false;
                        Platform.runLater(() -> {
                            tip("解压缩中");
                            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                        });
                    }
                }
                Platform.runLater(() -> {
                    pauseButton.setDisable(true);
                    cancelButton.setDisable(true);
                    progressBar.setProgress(0);
                });
                if (!packDecompiler.isStop()) tip("完成");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                assert this.getParent() instanceof VBox;
                try {
                    Platform.runLater(() -> ((VBox) this.getParent()).getChildren().remove(this));
                } catch (NullPointerException ignored){
                }
            });
            flasher.start();
            packDecompiler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void cancel(MouseEvent event) {
        packDecompiler.cancel();
        final ProgressPane finalPane = this;
        new Thread(() -> {
            tip("已取消");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> ((VBox) finalPane.getParent()).getChildren().remove(finalPane));
        }).start();
    }
    @FXML
    void pause() {
        if (!paused) {
            pauseButtonImageView.setImage(new Image(pauseImage1Path));
            packDecompiler.pause();
        } else {
            pauseButtonImageView.setImage(new Image(pauseImage2Path));
            packDecompiler.reStart();
        }
        paused = !paused;
    }
    
    public void tip(String message) {
        Platform.runLater(() -> versionLabel.setText(version + "：" + message));
    }
}
