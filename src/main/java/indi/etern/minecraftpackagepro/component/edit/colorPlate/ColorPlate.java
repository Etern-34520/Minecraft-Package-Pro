package indi.etern.minecraftpackagepro.component.edit.colorPlate;

import indi.etern.minecraftpackagepro.component.edit.colorPicker.ColorPicker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ColorPlate extends GridPane {
    final Pane alphaPane = new Pane();
    private final int buttonMinSize = 60;
    private final int[] lastButtonLayNum = new int[]{0, 0};
    public ToggleGroup colorToggles = new ToggleGroup();
    private ColorPicker colorPicker;
    private Color[][] colors2d = new Color[5][5];
    EventHandler<MouseEvent> eventHandler = event -> {
        ColorButton selectedButton = (ColorButton) colorToggles.getSelectedToggle();
        if (selectedButton != null) {
            selectedButton.setColor(selectedButton.r, selectedButton.g, selectedButton.b, selectedButton.o);
        }
    };
    ChangeListener<Number> buttonFiller = (observable, oldValue, newValue) -> {
        double height = getHeight();
        double width = getWidth();
        int heightNum = (int) (height / buttonMinSize);
        int widthNum = (int) (width / buttonMinSize);
        if (heightNum == 0 || widthNum == 0 || (heightNum == lastButtonLayNum[0] && widthNum == lastButtonLayNum[1])) {
            return;
        }
//        colorToggles.getToggles().removeAll(colorToggles.getToggles());
//        if (getColumnConstraints().size() != widthNum && getRowConstraints().size() != heightNum)
//            getChildren().removeAll(getChildren());
        int deltaHeightNum = heightNum - lastButtonLayNum[0];
        int deltaWidthNum = widthNum - lastButtonLayNum[1];
        boolean isAdd = deltaHeightNum > 0 || deltaWidthNum > 0;
        boolean isRemove = deltaHeightNum < 0 || deltaWidthNum < 0;
        List<ToggleButton> toggleButtons = new ArrayList<>();
        for (int column = 0; column <= Math.max(widthNum, lastButtonLayNum[1]); column++) {
            for (int row = 0; row <= Math.max(heightNum, lastButtonLayNum[0]); row++) {
                boolean exist = false;
                ColorButton button;
                try {
                    button = (ColorButton) getFirstNodeOn(row, column);
                } catch (Exception ignored) {
                    button = null;
                }
                exist = true;
                if (button == null) {
                    exist = false;
                    button = new ColorButton();
                }
                if (isAdd && !exist) {
                    button.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    button.setToggleGroup(colorToggles);
                    button.setOnMouseClicked(eventHandler);
                    GridPane.setHgrow(button, Priority.ALWAYS);
                    GridPane.setVgrow(button, Priority.ALWAYS);
                    GridPane.setRowIndex(button, row);
                    GridPane.setColumnIndex(button, column);
                    try {
                        button.setColor(colors2d[row][column]);
                    } catch (IndexOutOfBoundsException | NullPointerException ignored) {
                    }
                    toggleButtons.add(button);
                } else if (isRemove && exist && column > widthNum | row > heightNum) {
                    toggleButtons.add(button);
//                    colorToggles.getToggles().remove(button);
//                    getChildren().remove(button);
                }
            }
        }
        if (isAdd) {
            getChildren().addAll(toggleButtons);
        } else if (isRemove) {
            getChildren().removeAll(toggleButtons);
            colorToggles.getToggles().removeAll(toggleButtons);
        }
        lastButtonLayNum[0] = heightNum;
        lastButtonLayNum[1] = widthNum;
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                if (getColumnConstraints().size() != widthNum && getRowConstraints().size() != heightNum) {
                    GridPane.setColumnSpan(alphaPane, widthNum + 1);
                    GridPane.setRowSpan(alphaPane, heightNum + 1);
                }
            });
        }).start();
        GridPane.setHgrow(alphaPane, Priority.ALWAYS);
        GridPane.setVgrow(alphaPane, Priority.ALWAYS);
    };
    public ColorPlate() {
        this.setMinSize(0, 0);
        this.setPrefSize(0, 0);
        this.setAlignment(Pos.TOP_CENTER);
        widthProperty().addListener(buttonFiller);
        heightProperty().addListener(buttonFiller);
        BackgroundImage backgroundImage = new BackgroundImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/alphabg.png"))), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        final Background background = new Background(backgroundImage);
        alphaPane.setBackground(background);
        add(alphaPane, 0, 0);
        setMinSize(buttonMinSize *2, buttonMinSize *2);
    }
    
    Node getFirstNodeOn(int row, int column) {
        for (Node child : getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            Integer columnIndex = GridPane.getColumnIndex(child);
            if (rowIndex != null && rowIndex == row && columnIndex != null && columnIndex == column) {
                return child;
            }
        }
        return null;
    }
    
    public void setColorPicker(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
    }
    
    public void setSelectedButtonColor(int r, int g, int b, double a) {
        double o = Math.round(a * 1000 / 100) / 1000.0;
        ColorButton selectedButton = (ColorButton) colorToggles.getSelectedToggle();
        if (selectedButton != null) {
            selectedButton.setColor(r, g, b, o);
            selectedButton.setDefaultColor(false);
        }
    }
    
    private class ColorButton extends ToggleButton {
        private int r;
        private int g;
        private int b;
        private double o;
        private boolean defaultColor = true;
        
        ColorButton() {
            o = 1;
            r = g = b = 51;
            setStyle("-fx-border-color: rgb(37,37,37);-fx-border-width: 1px;-fx-border-insets: 0px;-fx-background-color: rgb(" + r + "," + g + "," + b + "," + o + ")");
            selectedProperty().addListener(event -> new Thread(() -> {
                if (isSelected()) {
                    setStyle("-fx-border-color: rgb(112,112,112);-fx-border-width: 1px;-fx-border-insets: 0px;-fx-background-color: rgb(" + r + "," + g + "," + b + "," + o + ")");
                    if (!defaultColor) {
                        colorPicker.setColor(r, g, b, (int) (o * 100));
                    } else {
                        final Color currentColor = colorPicker.getColor();
                        for (Color[] colorsX : colors2d) {
                            for (Color color : colorsX) {
                                try {//TODO fix equals
//                                    if (color.equals(currentColor)) {
//                                        return;
//                                    }
                                    if (color.getRed()==currentColor.getRed() &&
                                            color.getGreen()==currentColor.getGreen() &&
                                            color.getBlue()==currentColor.getBlue() &&
                                            Math.abs(color.getOpacity()-currentColor.getOpacity())<=0.02 ) {
                                        return;
                                    }
                                } catch (NullPointerException ignored) {
                                }
                            }
                        }
                        setColor(currentColor);
                    }
                    defaultColor = false;
                } else {
                    setStyle("-fx-border-color: rgb(37,37,37);-fx-border-width: 1px;-fx-border-insets: 0px;-fx-background-color: rgb(" + r + "," + g + "," + b + "," + o + ")");
                }
            }).start());
        }
        
        public void setDefaultColor(boolean defaultColor) {
            this.defaultColor = defaultColor;
        }
        
        void setColor(Color color) {
            setColor((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), color.getOpacity());
        }
        
        /**
         * @param r int 0-255
         * @param g int 0-255
         * @param b int 0-255
         * @param o double 0.0-1.0
         */
        void setColor(int r, int g, int b, double o) {
            this.r = r;
            this.g = g;
            this.b = b;
            o = Math.round(o * 100) * 0.01d;
            this.o = o;
            if (isSelected()) {
                setStyle("-fx-border-color: rgb(112,112,112);-fx-border-width: 1px;-fx-border-insets: 0px;-fx-background-color: rgb(" + r + "," + g + "," + b + "," + o + ")");
            } else {
                setStyle("-fx-border-color: rgb(37,37,37);-fx-border-width: 1px;-fx-border-insets: 0px;-fx-background-color: rgb(" + r + "," + g + "," + b + "," + o + ")");
            }
            final int x = GridPane.getRowIndex(this);
            final int y = GridPane.getColumnIndex(this);
            boolean over = false;
            while (!over) {
                try {
                    colors2d[x][y] = Color.color(r / 255.0, g / 255.0, b / 255.0, o);
                    over = true;
                } catch (IndexOutOfBoundsException e) {
                    Color[][] colors2dNew = new Color[Math.max(x, colors2d.length) + 5][Math.max(y, colors2d[0].length) + 5];
                    for (int i = 0; i < colors2d.length; i++) {
                        System.arraycopy(colors2d[i], 0, colors2dNew[i], 0, colors2d[i].length);
                    }
                    colors2d = colors2dNew;
                }
            }
//			int index = colorToggles.getToggles().indexOf(this);
        }
    }
    
}
