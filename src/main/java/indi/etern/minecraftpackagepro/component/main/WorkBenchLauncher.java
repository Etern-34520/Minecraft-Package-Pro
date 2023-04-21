package indi.etern.minecraftpackagepro.component.main;

import indi.etern.minecraftpackagepro.component.bench.WorkBench;
import indi.etern.minecraftpackagepro.component.bench.WorkBench.Way;
import indi.etern.minecraftpackagepro.component.edit.colorPicker.ColorPicker;
import indi.etern.minecraftpackagepro.component.edit.colorPlate.ColorPlate;
import indi.etern.minecraftpackagepro.component.tools.decompiler.DecompilerGui;
import indi.etern.minecraftpackagepro.dataBUS.Setting;
import indi.etern.minecraftpackagepro.io.FileTree;
import indi.etern.minecraftpackagepro.io.FileTree.tsFile;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.File;
import java.util.Optional;

public class WorkBenchLauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        WorkBench WorkBench = new WorkBench();
        Scene scene = new Scene(WorkBench,600,400);
        stage.setScene(scene);
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
        
        Setting setting = Setting.getInstance();
        
        try {
            double benchWindowX = (double) setting.get("BenchWindowX");
            double benchWindowY = (double) setting.get("BenchWindowY");
            double benchWindowWidth = (double) setting.get("BenchWindowWidth");
            double benchWindowHeight = (double) setting.get("BenchWindowHeight");
            boolean benchWindowIsMaximized = (boolean) setting.get("BenchWindowIsMaximized");
            stage.setX(benchWindowX);
            stage.setY(benchWindowY);
            stage.setWidth(benchWindowWidth);
            stage.setHeight(benchWindowHeight);
            stage.setMaximized(benchWindowIsMaximized);
        } catch (NullPointerException ignore){
        }
        
        ColorPicker colorPicker = new ColorPicker();
        WorkBench.add(colorPicker, Way.LEFT_TOP,"RGB");
        
        ColorPlate colorPlate = new ColorPlate();
        WorkBench.add(colorPlate, Way.LEFT_BOTTOM, "plate");
        
        colorPicker.setColorPlate(colorPlate);
        
        DecompilerGui decompilerGui = new DecompilerGui();
        WorkBench.add(decompilerGui, Way.RIGHT_TOP,"资源包提取");
        decompilerGui.setVertical(true);
        decompilerGui.setPrefWidth(300);
        
        FileTree fileTree = new FileTree();
        TreeItem<tsFile> root = new TreeItem<>();
        fileTree.setRootOfAll(root);
        fileTree.create(new File("D:\\minecraftDefaultPack_1.12.2"), root);
        TreeView<tsFile> treeView = new TreeView<>();
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        MenuItem refresh = new MenuItem("刷新");
        refresh.setOnAction(event -> fileTree.refresh());
        ContextMenu contextMenu = new ContextMenu(refresh);
        treeView.setContextMenu(contextMenu);
        WorkBench.add(treeView, Way.LEFT_TOP, "packView");
        
        TabPane tabPane = new TabPane();
        fileTree.setTreeViewAndTabPane(treeView,tabPane);
    
        
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
            if (isAllTasksOver(decompilerGui)) {
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
        WorkBench.setCenterBasic(tabPane);
        stage.show();
    }

    private static boolean isAllTasksOver(DecompilerGui decompilerGui) {
        return decompilerGui.decompileProgress.getChildren().size() != 0;
    }

    public static void main(String[] args){
        launch(args);
    }
}
