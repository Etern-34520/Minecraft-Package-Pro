package io;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileTree {
    private final tsFile LOADING = new tsFile("loading...");
    private final TreeItem<tsFile> loading = new TreeItem<>(LOADING);

    public void setRootOfAll(TreeItem<tsFile> rootOfAll) {
        this.rootOfAll = rootOfAll;
    }

    private TreeItem<tsFile> rootOfAll;
    public static class tsFile extends File{
        public tsFile(String pathname) {
            super(pathname);
        }
        @Override
        public String toString(){
            return super.getName();
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
        for (tsFile child : children) {
            TreeItem<tsFile> childItem = new TreeItem<>(child);
            root.getChildren().add(childItem);
            if(child.isDirectory()&&child.listFiles()!=null&& Objects.requireNonNull(child.listFiles()).length>0){
                childItem.getChildren().add(loading);
                childItem.addEventHandler(TreeItem.branchExpandedEvent(), (EventHandler<TreeItem.TreeModificationEvent<tsFile>>) event -> {
                    TreeItem<tsFile> newRoot=event.getTreeItem();
                    if (newRoot.getChildren().size()==1&&newRoot.getChildren().get(0).equals(loading)) {
                        newRoot.getChildren().remove(0);
                        create(new tsFile(child.getPath() + "\\"),newRoot);
                    }
                });
            } else if(!child.isDirectory()){
                String[] nameSplit = child.getName().split("\\.");
                switch (nameSplit[1]){
                    case "json":
                        childItem.setGraphic(new ImageView(new Image("file:\\jsonFileIcon.png")));
                        break;
                }
            }
            if (child.isDirectory()){
                childItem.setGraphic(new ImageView(new Image("file:\\folder.png")));
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
            if (!child.getChildren().contains(loading)){
                refreshChildOf(child);
            }
        }
    }
}
