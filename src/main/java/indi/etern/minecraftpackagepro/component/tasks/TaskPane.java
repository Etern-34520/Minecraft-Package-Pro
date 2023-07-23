package indi.etern.minecraftpackagepro.component.tasks;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.IOException;
import java.net.URL;

public abstract class TaskPane extends VBox implements Runnable{
    @FXML
    ProgressBar progressBar;
    @FXML
    Label title;
    @FXML
    Label tipLabel;
    @FXML
    Label moreInfo;
    @FXML
    Button pause;
    @FXML
    Button cancel;
    boolean pauseButtonState = true;
    protected volatile boolean canceled = false;
    protected volatile boolean paused = false;
    public TaskPane() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(new URL(TaskPane.class.getResource("") + "resources/" + TaskPane.class.getSimpleName() + ".fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            VBox.setMargin(this, new Insets(5, 5, 5, 5));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setTitle(String title) {
        title = title.replace("\n", " ");
        this.title.setText(title);
    }
    
    /**
     * @param progress : 0-100 or -1(indeterminate)
     */
    public void setProgress(double progress) {
        Platform.runLater(()->{
            if (progress == ProgressBar.INDETERMINATE_PROGRESS) progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            else progressBar.setProgress(progress/100.0);
        });
    }
    public double getProgress(){return Math.round(progressBar.getProgress()*100);}
    public void setMoreInfo(String moreInfo) {
        Platform.runLater(()->{
            this.moreInfo.setText(moreInfo);
        });
    }
    public String getMoreInfo(){return moreInfo.getText();}
    /**
     * @param seconds : normal or -1(infinite)
     */
    public synchronized void setTip(String tip, int seconds) {
        if (!tipLabel.getText().equals("已完成")) {
            Platform.runLater(() -> {
                tipLabel.setText(tip);
            });
            if (seconds == -1) return;
            new Thread(()->{
                try {
                    Thread.sleep(seconds * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (tipLabel.getText().equals(tip)) {
                    Platform.runLater(() -> tipLabel.setText(""));
                }
            }).start();
        }
    }
    @FXML
    private void pauseOrResume(){
        if (pauseButtonState) pause();
        else resume();
        paused = pauseButtonState;
        pauseButtonState = !pauseButtonState;
    };
    protected abstract void pause();
    protected abstract void resume();
    @FXML
    private void cancel(Event event){
        canceled = true;
        Thread.currentThread().interrupt();
        finish();
        setTip("已取消",-1);
        cancel();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }
        removeSelf();
    }
    protected abstract void cancel();
    
    @Override
    public void run(){
        try {
            doTask();
        } catch (InterruptedException e){
            if (canceled){
                setTip("已取消",-1);
            }
        } finally {
            pause.setDisable(true);
            cancel.setDisable(true);
            finish();
            setProgress(100);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
            setTip("已完成",-1);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            removeSelf();
        }
    }
    
    private void removeSelf() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }
        Platform.runLater(()->{
            ((VBox)getParent()).getChildren().remove(this);
        });
    }
    
    protected abstract void finish();
    
    /**
     *
     * should use boolean:paused & canceled to react
     */
    protected abstract void doTask() throws InterruptedException;
}
