package indi.etern.minecraftpackagepro.component.main;

import indi.etern.minecraftpackagepro.component.bench.EditPane;
import indi.etern.minecraftpackagepro.component.bench.WorkBench;
import indi.etern.minecraftpackagepro.component.bench.WorkBench.Way;
import indi.etern.minecraftpackagepro.component.edit.colorPicker.ColorPicker;
import indi.etern.minecraftpackagepro.component.edit.colorPlate.ColorPlate;
import indi.etern.minecraftpackagepro.component.tools.decompiler.DecompilerGui;
import indi.etern.minecraftpackagepro.io.FileTree;
import indi.etern.minecraftpackagepro.io.FileTree.tsFile;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkBenchLauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //TODO 记住大小和位置
        WorkBench WorkBench = new WorkBench();
        Scene scene = new Scene(WorkBench,600,400);
        stage.setScene(scene);
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
        stage.show();

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
        refresh.setOnAction(event -> {
            fileTree.refresh();
        });
        ContextMenu contextMenu = new ContextMenu(refresh);
        treeView.setContextMenu(contextMenu);
        WorkBench.add(treeView, Way.LEFT_TOP, "packView");
    
        TabPane tabPane = new TabPane();
    
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<tsFile>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<tsFile>> observable, TreeItem<FileTree.tsFile> oldValue, TreeItem<FileTree.tsFile> newValue) {
                EditPane EditPane = new EditPane();
                if (newValue != null) {
                    tsFile picture = newValue.getValue();
                    EditPane.uploadPicture(picture);
                }
                if (EditPane.toTab()!=null) {
                    if (contains(EditPane.toTab())) {
                        tabPane.selectionModelProperty().get().select(getInTabList(EditPane.toTab()));
                    } else {
                        tabPane.getTabs().add(EditPane.toTab());
                        tabPane.selectionModelProperty().get().select(getInTabList(EditPane.toTab()));
                    }
                }

                //System.out.println("item:"+newValue);
            }

            private Tab getInTabList(Tab tab) {
                List<tsFile> tabNames = new ArrayList<>();
                for (int i = 0; i < tabPane.getTabs().size(); i++) {
                    tabNames.add(new FileTree.tsFile(tabPane.getTabs().get(i).getText()));
                }
                int array = 0;
                for (int i = 0; i < tabPane.getTabs().size(); i++) {
                    if(tabNames.get(i).getName().equals(tab.getText())) array=i;
                }
                return tabPane.getTabs().get(array);
            }
            private boolean contains(Tab tab) {
                List<String> tabNames = new ArrayList<>();
                for (int i = 0; i < tabPane.getTabs().size(); i++) {
                    tabNames.add(tabPane.getTabs().get(i).getText());
                }
                return tabNames.contains(tab.getText());
            }

        });
        stage.setOnCloseRequest(event -> {
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
    }

    private static boolean isAllTasksOver(DecompilerGui decompilerGui) {
        return decompilerGui.decompileProgress.getChildren().size() != 0;
    }

    public static void main(String[] args){
        launch(args);
    }
}
