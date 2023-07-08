package indi.etern.minecraftpackagepro.component.main;

import indi.etern.minecraftpackagepro.component.tasks.TaskManager;
import indi.etern.minecraftpackagepro.component.bench.WorkBench;
import indi.etern.minecraftpackagepro.component.bench.WorkBench.Way;
import indi.etern.minecraftpackagepro.component.edit.colorPicker.ColorPicker;
import indi.etern.minecraftpackagepro.component.edit.colorPlate.ColorPlate;
import indi.etern.minecraftpackagepro.component.tools.decompiler.DecompilerGui;
import indi.etern.minecraftpackagepro.dataBUS.Setting;
import indi.etern.minecraftpackagepro.io.indexScanner.IndexScanner;
import indi.etern.minecraftpackagepro.io.packTree.FileTree;
import indi.etern.minecraftpackagepro.io.packTree.FileTree.tsFile;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.File;
import java.util.Optional;

public class WorkBenchLauncher extends Application {
    
    private static final long STARTTIME = System.currentTimeMillis();
    private IndexScanner indexScanner;
    
    @Override
    public void start(Stage stage) throws Exception {
        // 初始化
        WorkBench workBench = new WorkBench();
        workBench.setCache(true);
        Scene scene = new Scene(workBench,600,400);
        stage.setScene(scene);
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
        
        // 读取设置
        Setting setting = Setting.getInstance();
        try {
            double benchWindowX = setting.getDouble("BenchWindowX");
            double benchWindowY = setting.getDouble("BenchWindowY");
            double benchWindowWidth = setting.getDouble("BenchWindowWidth");
            double benchWindowHeight = setting.getDouble("BenchWindowHeight");
            boolean benchWindowIsMaximized = setting.getBoolean("BenchWindowIsMaximized");
            stage.setX(benchWindowX);
            stage.setY(benchWindowY);
            stage.setWidth(benchWindowWidth);
            stage.setHeight(benchWindowHeight);
            stage.setMaximized(benchWindowIsMaximized);
            Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
            double width = screenRectangle.getWidth();
            double height = screenRectangle.getHeight();
            setting.put("BenchWindowX", width*0.1);
            setting.put("BenchWindowY", height*0.1);
            setting.put("BenchWindowWidth", width*0.8);
            setting.put("BenchWindowHeight", height*0.8);
            setting.flush();
        } catch (NullPointerException ignore){
        }
        // 添加组件
        ColorPicker colorPicker = new ColorPicker();
        workBench.add(colorPicker, Way.LEFT_TOP,"RGB");
        
        ColorPlate colorPlate = new ColorPlate();
        workBench.add(colorPlate, Way.LEFT_BOTTOM, "plate");
        
        colorPicker.setColorPlate(colorPlate);
        
        DecompilerGui decompilerGui = new DecompilerGui();
        workBench.add(decompilerGui, Way.LEFT_TOP,"资源包提取");
        decompilerGui.setVertical(true);
        decompilerGui.setPrefWidth(300);
        decompilerGui.setWorkBench(workBench);
        decompilerGui.getItems().remove(1);
        
        if (setting.getString("templatePackPath") == null|!new File(setting.getString("templatePackPath")).exists()) {
            setting.put("templatePackPath", "F:\\Minecraft\\resourcePacks\\");
        }
        FileTree fileTree = new FileTree("1.8.9");
        TreeItem<tsFile> root = new TreeItem<>();
        fileTree.setRootOfAll(root);
        fileTree.create(new File("F:\\Minecraft\\resourcePacks\\!     §b NotroFault §f[16x]"), root);
        TreeView<tsFile> treeView = new TreeView<>();
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        MenuItem refresh = new MenuItem("刷新");
        refresh.setOnAction(event -> fileTree.refresh());
        ContextMenu contextMenu = new ContextMenu(refresh);
        treeView.setContextMenu(contextMenu);
        workBench.add(treeView, Way.LEFT_TOP, "packView");
        
        TabPane tabPane = new TabPane();
        fileTree.setTreeViewAndTabPane(treeView,tabPane);
        
        new Thread(() -> this.indexScanner = new IndexScanner(new File("F:\\Minecraft\\resourcePacks\\minecraftDefaultPack_1.8.9"))).start();
        
        TaskManager taskManager = new TaskManager();
        
        workBench.add(taskManager, Way.RIGHT_BOTTOM, "任务");
        
        //关闭窗口时的操作
        stage.setOnCloseRequest(event -> {
            boolean maximized = stage.isMaximized();
            setting.put("BenchWindowIsMaximized", maximized);
            if (maximized){
                setting.put("BenchWindowX",stage.getWidth()*0.1);
                setting.put("BenchWindowY",stage.getHeight()*0.1);
                setting.put("BenchWindowWidth",stage.getWidth()*0.8);
                setting.put("BenchWindowHeight",stage.getHeight()*0.8);
            } else {
                setting.put("BenchWindowX",stage.getX());
                setting.put("BenchWindowY",stage.getY());
                setting.put("BenchWindowWidth",stage.getWidth());
                setting.put("BenchWindowHeight",stage.getHeight());
            }
            if (!isAllTasksOver(decompilerGui,taskManager)) {
                Alert warming = new Alert(Alert.AlertType.CONFIRMATION, "确认关闭？");
                warming.setHeaderText("确认关闭?");
                Optional<ButtonType> result = warming.showAndWait();
                if (result.isPresent()&&result.get() == ButtonType.OK) {
                    System.out.println("closed");
                    System.exit(0);
                } else {
                    event.consume();
                }
            }
        });
        workBench.setCenterBasic(tabPane);
        stage.show();
    }

    private static boolean isAllTasksOver(DecompilerGui decompilerGui,TaskManager taskManager){
//        return decompilerGui.decompileProgress.getChildren().size() == 0;
        return ((VBox)taskManager.getContent()).getChildren().size() == 0;
    }

    public static void main(String[] args){
        launch(args);
    }
    public static double getTimeSinceStart() {
        return (System.currentTimeMillis()-STARTTIME)/1000.0;
    }
    /*private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(WorkBenchLauncher.class);
    public static org.apache.log4j.Logger getLogger() {
        return LOGGER;
    }*/
}
