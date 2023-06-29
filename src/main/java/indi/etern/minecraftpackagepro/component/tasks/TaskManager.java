package indi.etern.minecraftpackagepro.component.tasks;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetroStyleClass;
public class TaskManager extends ScrollPane {
    private final VBox taskList = new VBox();
    public TaskManager() {
        super();
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
        setFitToWidth(true);
        setContent(taskList);
//        setVmax(USE_COMPUTED_SIZE);
    }
    public void addTask(TaskPane task){
        Platform.runLater(()->{
            taskList.getChildren().add(task);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), task);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();
        });
    }
}
