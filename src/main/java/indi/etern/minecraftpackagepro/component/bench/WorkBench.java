package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkDesk extends GridPane {
    @FXML
    SplitPane topPane;
    @FXML
    SplitPane leftPane;
    @FXML
    SplitPane rightPane;
    @FXML
    SplitPane bottomPane;
    @FXML
    HBox topLeftBar;
    @FXML
    HBox topRightBar;
    @FXML
    HBox leftTopBar;
    @FXML
    HBox rightTopBar;
    @FXML
    HBox leftBottomBar;
    @FXML
    HBox rightBottomBar;
    @FXML
    HBox bottomLeftBar;
    @FXML
    HBox bottomRightBar;
    @FXML
    SplitPane leftRightParent;
    @FXML
    SplitPane allParent;
    @FXML
    AnchorPane moveActionPane;
    @FXML GridPane centerParent;
    ToggleGroup topLeftGroup = new ToggleGroup();
    ToggleGroup topRightGroup = new ToggleGroup();
    ToggleGroup leftTopGroup = new ToggleGroup();
    ToggleGroup rightTopGroup = new ToggleGroup();
    ToggleGroup leftBottomGroup = new ToggleGroup();
    ToggleGroup rightBottomGroup = new ToggleGroup();
    ToggleGroup bottomLeftGroup = new ToggleGroup();
    ToggleGroup bottomRightGroup = new ToggleGroup();

    HashMap<Node,SplitPane> parents = new HashMap<>();
    public WorkDesk(){
        URL url;
        try {
            url = new URL("file:"+new File("bin/controller/resource/WorkDesk.fxml").getPath());
            FXMLLoader loader = new FXMLLoader(url);
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);

            parents.put(topPane,allParent);
            parents.put(bottomPane, allParent);
            parents.put(leftPane,leftRightParent);
            parents.put(rightPane,leftRightParent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(Node node, Way way, String name){
        HBox buttonBar;
        SplitPane splitPane;
        ToggleButton addButton = new ToggleButton(name);
        Window window = new Window(node, addButton);
        addButton.setFont(new Font(10));
        addButton.setStyle("-fx-font-size: 10; border_hover_selected_color: rgb(45,47,48)");
        //System.out.println(addButton.getStylesheets());
        ToggleGroup group;
        int index = 0;
        if (way == Way.TOP_LEFT) {
            buttonBar = topLeftBar;
            splitPane = topPane;
            group = topLeftGroup;
            index = 0;
        } else if (way == Way.TOP_RIGHT){
            buttonBar = topRightBar;
            splitPane = topPane;
            group = topRightGroup;
            if (!splitPane.getChildrenUnmodifiable().isEmpty()){
                index=1;
            }
        } else if (way == Way.LEFT_TOP) {
            buttonBar = leftTopBar;
            splitPane = leftPane;
            group = leftTopGroup;
            index=0;
        } else if (way == Way.RIGHT_TOP){
            buttonBar = rightTopBar;
            splitPane = rightPane;
            group = rightTopGroup;
            index=0;
        } else if (way == Way.LEFT_BOTTOM) {
            buttonBar = leftBottomBar;
            splitPane = leftPane;
            group = leftBottomGroup;
            if (!splitPane.getChildrenUnmodifiable().isEmpty()){
                index=1;
            }
        } else if (way == Way.RIGHT_BOTTOM){
            buttonBar = rightBottomBar;
            splitPane = rightPane;
            group = rightBottomGroup;
            if (!splitPane.getChildrenUnmodifiable().isEmpty()){
                index=1;
            }
        } else if (way == Way.BOTTOM_LEFT) {
            buttonBar = bottomLeftBar;
            splitPane = bottomPane;
            group = bottomLeftGroup;
            index=0;
        } else if (way == Way.BOTTOM_RIGHT){
            buttonBar = bottomRightBar;
            splitPane = bottomPane;
            group = bottomRightGroup;
            if (!splitPane.getChildrenUnmodifiable().isEmpty()){
                index=1;
            }
        } else {
            throw new EnumConstantNotPresentException(Way.class,"must be ways");
        }
        parents.put(window,splitPane);
        splitPane.getItems().add(index,window);
        addButton.setToggleGroup(group);
        buttonBar.getChildren().add(addButton);
        addButton.setSelected(true);
        addButton.setStyle("-fx-font-size: 10;-fx-background-color :rgb(30,30,30);border_hover_selected_color: rgb(45,47,48)");
        int finalIndex = index;
        addButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            SplitPane splitPane1 = parents.get(window);
            if (addButton.isSelected()){
                addButton.setStyle("-fx-font-size: 10;-fx-background-color :rgb(30,30,30);border_hover_selected_color: rgb(45,47,48)");
                try {
                    splitPane1.getItems().add(finalIndex,window);
                } catch (Exception e) {
                    splitPane1.getItems().add(window);
                }
                splitPane1.setMaxWidth(Integer.MAX_VALUE);
                splitPane1.setMaxHeight(Integer.MAX_VALUE);
            } else {
                addButton.setStyle("-fx-font-size: 10; border_hover_selected_color: rgb(45,47,48)");
                splitPane1.getItems().remove(window);
                if (splitPane1.getItems().isEmpty()){
                    splitPane1.setMaxWidth(0);
                    splitPane1.setMaxHeight(0);
                } else {
                    splitPane1.setMaxWidth(Integer.MAX_VALUE);
                    splitPane1.setMaxHeight(Integer.MAX_VALUE);
                }
            }
        });
        flash();
    }
    public void setCenterBasic(Node centerBasic){
        centerParent.add(centerBasic,0,0);
    }

    private void flash() {
        List<ColumnConstraints> hideColumn = new ArrayList<>();
        if (leftPane.getItems().isEmpty()){
            if (leftTopBar.getChildren().isEmpty()&&leftBottomBar.getChildren().isEmpty()){
                hideColumn.add(this.getColumnConstraints().get(0));
            }
            parents.get(leftPane).getItems().remove(leftPane);
        } else if (!parents.get(leftPane).getItems().contains(leftPane)){
            parents.get(leftPane).getItems().add(leftPane);
        }
        if (rightPane.getItems().isEmpty()) {
            if (rightTopBar.getChildren().isEmpty() && rightBottomBar.getChildren().isEmpty()) {
                hideColumn.add(this.getColumnConstraints().get(2));
            }
            parents.get(rightPane).getItems().remove(rightPane);
        } else if (!parents.get(rightPane).getItems().contains(rightPane)) {
            parents.get(rightPane).getItems().add(rightPane);
        }
        width(this.getColumnConstraints().get(0), 21);
        width(this.getColumnConstraints().get(2), 21);
        for (ColumnConstraints columnConstraints : hideColumn) {
            width(columnConstraints, 0);
        }
    
        List<RowConstraints> hideRow = new ArrayList<>();
        if (topPane.getItems().isEmpty()) {
            if (topLeftBar.getChildren().isEmpty() && topRightBar.getChildren().isEmpty()) {
                hideRow.add(this.getRowConstraints().get(0));
            }
            parents.get(topPane).getItems().remove(topPane);
        } else if (!parents.get(topPane).getItems().contains(topPane)) {
            parents.get(topPane).getItems().add(topPane);
        }
        if (bottomPane.getItems().isEmpty()) {
            if (bottomLeftBar.getChildren().isEmpty() && bottomRightBar.getChildren().isEmpty()) {
                hideRow.add(this.getRowConstraints().get(2));
            }
            parents.get(bottomPane).getItems().remove(bottomPane);
        } else if (!parents.get(bottomPane).getItems().contains(bottomPane)) {
            parents.get(bottomPane).getItems().add(bottomPane);
        }
        height(this.getRowConstraints().get(0), 21);
        height(this.getRowConstraints().get(2), 21);
        for (RowConstraints rowConstraints : hideRow) {
            height(rowConstraints, 0);
        }
    }

    private void height(RowConstraints c, int i) {
        c.setMinHeight(i);
        c.setPrefHeight(i);
        c.setMaxHeight(i);
    }

    private void width(ColumnConstraints c,int i) {
        c.setMinWidth(i);
        c.setPrefWidth(i);
        c.setMaxWidth(i);
    }

    public enum Way{
        TOP_LEFT, TOP_RIGHT,
        LEFT_TOP, RIGHT_TOP,
        LEFT_BOTTOM,RIGHT_BOTTOM,
        BOTTOM_LEFT, BOTTOM_RIGHT
    }

}
