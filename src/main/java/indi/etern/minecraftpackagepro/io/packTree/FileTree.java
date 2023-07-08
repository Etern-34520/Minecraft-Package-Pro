package indi.etern.minecraftpackagepro.io.packTree;

import indi.etern.minecraftpackagepro.component.bench.EditPane;
import indi.etern.minecraftpackagepro.component.main.WorkBenchLauncher;
import indi.etern.minecraftpackagepro.dataBUS.Setting;
import indi.etern.minecraftpackagepro.dataBUS.model.Model;
import indi.etern.minecraftpackagepro.io.indexScanner.IndexScanner;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileTree {
    private final tsFile LOADING = new tsFile("loading...");
    private final TreeItem<tsFile> LOADING_ITEM = new TreeItem<>(LOADING);

    public void setRootOfAll(TreeItem<tsFile> rootOfAll) {
        this.rootOfAll = rootOfAll;
    }

    private TreeItem<tsFile> rootOfAll;
    private String rootPath;
    private IndexScanner indexScanner;
    private final String packVersion;
    public FileTree(String packVersion){
        this.packVersion = packVersion;
    }
    
    public void setTreeViewAndTabPane(TreeView<tsFile> treeView, TabPane tabPane) {
        final EventHandler<Event> eventHandler = new EventHandler<>() {
            @Override
            public void handle(Event event) {
                if (event instanceof MouseEvent mouseEvent) {
                    if (mouseEvent.getClickCount() == 2) {
                        uploadSelectedPictureToEditPane();
                    }
                } else if (event instanceof KeyEvent keyEvent) {
                    if (keyEvent.getCode().equals(KeyCode.F5)) {
                        refresh();
                    } else if (keyEvent.getCode().equals(KeyCode.ENTER)){
                        uploadSelectedPictureToEditPane();
                    }
                }
            }
            
            private void uploadSelectedPictureToEditPane() {
                EditPane editPane = new EditPane();
                final TreeItem<tsFile> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem == null) return;
                tsFile picture = selectedItem.getValue();
                List<Model> models = indexScanner.getModelsOf(picture);
                if (models != null) {
                    System.out.println(models);
                }
                editPane.uploadPicture(picture);
                if (editPane.toTab() != null) {
                    if (contains(editPane.toTab())) {
                        tabPane.selectionModelProperty().get().select(getInTabList(editPane.toTab()));
                    } else {
                        tabPane.getTabs().add(editPane.toTab());
                        tabPane.selectionModelProperty().get().select(getInTabList(editPane.toTab()));
                        new Thread(() -> {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            editPane.reDrawOriginalSize();
                        }).start();
                    }
                }
            }
            
            private Tab getInTabList(Tab tab) {
                List<tsFile> tabNames = new ArrayList<>();
                for (int i = 0; i < tabPane.getTabs().size(); i++) {
                    tabNames.add(new tsFile(tabPane.getTabs().get(i).getText()));
                }
                int array = 0;
                for (int i = 0; i < tabPane.getTabs().size(); i++) {
                    if (tabNames.get(i).getName().equals(tab.getText())) array = i;
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
        };
        treeView.setOnMouseClicked(eventHandler);
        treeView.setOnKeyPressed(eventHandler);
    }
    
    public static class tsFile extends File{
        public tsFile(String pathname) {
            super(pathname);
        }
        @Override
        public String toString(){
            return super.getName();
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof File){
                return Objects.equals(this.getName(),((File) obj).getName());
            }
            return false;
        }

        public List<tsFile> listTsFiles(){
            File[] originalFiles = super.listFiles();
            List<tsFile> files = new ArrayList<>();
            if (originalFiles != null) {
                for (File originalFile : originalFiles){
                    files.add(new tsFile(originalFile.getPath()));
                }
            }
            return files;
        }
    }
    static class CompareList{
        public List<TreeItem<tsFile>> getAddList() {
            return addList;
        }

        public List<TreeItem<tsFile>> getRemoveList() {
            return removeList;
        }
        private final List<TreeItem<tsFile>> addList = new ArrayList<>();
        private final List<TreeItem<tsFile>> removeList = new ArrayList<>();

        public CompareList(TreeItem<tsFile> parent){
            List<tsFile> childFiles = parent.getValue().listTsFiles();
            List<TreeItem<tsFile>> childItems = parent.getChildren();
            for (tsFile childFile : childFiles) {
                boolean add = true;
                for (TreeItem<tsFile> childItem : childItems) {
                    if (childItem.getValue().getPath().equals(childFile.getPath())){
                        add = false;
                        break;
                    }
                }
                if (add) addList.add(new TreeItem<>(childFile));
            }
            for (TreeItem<tsFile> childItem : childItems) {
                boolean remove = true;
                for (tsFile childFile : childFiles) {
                    if (childItem.getValue().getPath().equals(childFile.getPath())){
                        remove = false;
                    }
                }
                if (remove) removeList.add(childItem);
            }
        }
    }

    public void create(File originalParent,TreeItem<tsFile> root){
        tsFile parent = new tsFile(originalParent.getPath());
        List<tsFile> children = parent.listTsFiles();
        root.setValue(parent);
        root.getChildren().removeAll(root.getChildren());
        if (rootPath==null) {
            rootPath = parent.getPath();
            try {
                indexScanner = new IndexScanner(new File(rootPath));
            } catch (RuntimeException e) {
                System.out.println("["+ WorkBenchLauncher.getTimeSinceStart()+ "s][warning] index not found: \"" + rootPath + "\"");
                System.out.println(                                      "                | will use default index");
                Setting setting = Setting.getInstance();
                String templatePackPath = setting.getString("templatePackPath");
                File templatePack = new File(templatePackPath);
                indexScanner = new IndexScanner(new File(templatePack,"minecraftDefaultPack_"+packVersion));
            }
        }
        for (tsFile child : children) {
            TreeItem<tsFile> childItem = new TreeItem<>(child);
            root.getChildren().add(childItem);
            if(child.isDirectory()&&child.listFiles()!=null&& Objects.requireNonNull(child.listFiles()).length>0){
                childItem.getChildren().add(LOADING_ITEM);
                childItem.addEventHandler(TreeItem.branchExpandedEvent(), (EventHandler<TreeItem.TreeModificationEvent<tsFile>>) event -> {
                    TreeItem<tsFile> newRoot=event.getTreeItem();
                    if (newRoot.getChildren().size()==1&&newRoot.getChildren().get(0).equals(LOADING_ITEM)) {
                        newRoot.getChildren().remove(0);
                        create(new tsFile(child.getPath() + "\\"),newRoot);
                    }
                });
            } else if(!child.isDirectory()){
                String[] nameSplit = child.getName().split("\\.");
                childItem.setGraphic(new ImageView(new Image(getClass().getResource("")+"resources/"+nameSplit[nameSplit.length-1]+".png")));
            }
            if (child.isDirectory()){
                childItem.setGraphic(new ImageView(new Image(getClass().getResource("")+"resources/folder.png")));
            }
        }
    }
    public void refresh(){
        refreshChildOf(rootOfAll);
    }

    private void refreshChildOf(TreeItem<tsFile> parent) {
        CompareList compareList = new CompareList(parent);
        parent.getChildren().addAll(compareList.getAddList());
        parent.getChildren().removeAll(compareList.getRemoveList());
        for (TreeItem<tsFile> child : parent.getChildren()) {
            if (!child.getChildren().contains(LOADING_ITEM)){
                refreshChildOf(child);
            }
        }
    }
}
