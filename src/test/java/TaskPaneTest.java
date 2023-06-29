import indi.etern.minecraftpackagepro.component.tasks.TaskPane;
import javafx.application.Application;

public class TaskPaneTest extends TaskPane{
    public static void main(String[] args) {
        Application.launch(TaskPaneTestLauncher.class,args);
    }
    @Override
    protected void pause(){
    
    }
    
    @Override
    protected void resume() {
    
    }
    
    @Override
    protected void cancel(){
    
    }
    
    @Override
    protected void finish() {
    
    }
    
    @Override
    protected void doTask(){
        while (getProgress()!=100){
//            System.out.println(getProgress());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (canceled) break;
            else if (!paused) {
                setProgress(getProgress()+1);
                setTip(String.valueOf(getProgress()+1),1);
            }
        }
    }
}
