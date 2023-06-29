import indi.etern.minecraftpackagepro.component.tasks.TaskManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;

public class TaskPaneTestLauncher extends Application {
    @Override
    public void start(Stage stage) {
        TaskManager taskManager = new TaskManager();
        Scene scene = new Scene(taskManager, 600, 400);
        JMetro jMetro = new JMetro(jfxtras.styles.jmetro.Style.DARK);
        jMetro.setScene(scene);
        final TaskPaneTest taskPaneTest = new TaskPaneTest();
        taskManager.addTask(taskPaneTest);
        new Thread(taskPaneTest).start();
        stage.setScene(scene);
        stage.show();
    }
    
}
