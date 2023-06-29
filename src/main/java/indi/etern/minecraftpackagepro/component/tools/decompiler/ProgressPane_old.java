package indi.etern.minecraftpackagepro.component.tools.decompiler;

import indi.etern.minecraftpackagepro.io.originalPackDecompiler.PackDecompiler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;

public class ProgressPane_old extends GridPane {
    private PackDecompiler packDecompiler;
    @FXML
    private Label versionLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressBar progressBar1;
    @FXML
    private AnchorPane progress;
    @FXML
    private Button cancelButton;
    @FXML
    private Button pauseButton;
    boolean paused = false;
    public void onlyIndeterminateProgress(){
        progressBar.setVisible(false);
        progressBar1.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressBar1.setVisible(true);
    }
    ProgressPane_old(PackDecompiler packDecompiler, String version) {
        try {
            FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("")+"resources/"+getClass().getSimpleName()+".fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            this.versionLabel.setText(version);
            this.packDecompiler = packDecompiler;
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            TilePane.setMargin(this,new Insets(20,0,0,20));
//            startButton.setDisable(true);
//            tips.setText("正在反混淆中...");
            Thread flasher = new Thread(() -> {
                while (!packDecompiler.isOver()) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (packDecompiler.isReachException()) {
                        Platform.runLater(() -> versionLabel.setText(versionLabel.getText() + ":遇到错误"));
                        break;
                    }
                    if (aBoolean) {
                        progressBar.setProgress(packDecompiler.getProgress() / 100.0);
                    }
                    if (packDecompiler.getProgress() == 100&aBoolean) {
                        aBoolean = false;
                        Platform.runLater(() -> {
                            versionLabel.setText(versionLabel.getText()+":解压缩中");
                            progressBar.setProgress(0);
                            //tips.setText("解压缩jar中，这可能需要几分钟...")
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Platform.runLater(() ->{
                            progressBar.setVisible(false);
                            progressBar.setProgress(0);
                            progressBar1.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                            progressBar1.setVisible(true);
                        });
                    }
                }
                Platform.runLater(() -> {
                    progressBar1.setVisible(false);
                    versionLabel.setText("完成");
                    pauseButton.setDisable(true);
                    cancelButton.setDisable(true);
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> ((TilePane) this.getParent()).getChildren().remove(this));
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
        final ProgressPane_old finalPane = this;
        new Thread(() -> {
            Platform.runLater(() -> versionLabel.setText("已取消"));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> ((TilePane) finalPane.getParent()).getChildren().remove(finalPane));
        }).start();
    }
    
    @FXML
    void showSettings() {
        cancelButton.setVisible(true);
        pauseButton.setVisible(true);
    }
    
    @FXML
    void hideSettings() {
        cancelButton.setVisible(false);
        pauseButton.setVisible(false);
    }
    
    boolean aBoolean = true;
    
    @FXML
    void pause() {
        if (!paused) {
            packDecompiler.pause();
            pauseButton.setText("继续");
        } else {
            packDecompiler.reStart();
            pauseButton.setText("暂停");
        }
        paused = !paused;
    }
}
