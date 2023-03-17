package controller;

import io.PackDecompiler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class DecompilerGui extends SplitPane {
    @FXML
    private TextField minecraftPathTextField;
    @FXML
    private TextField putPathTextField;
    @FXML
    public TilePane decompileProgress;
    @FXML
    private ListView<String> minecraftVersionsView;
    @FXML
    private TabPane decompileModeSelectPane;
    @FXML
    private CheckBox onlyPathCheck;
    @FXML
    private CheckBox librariesCheck;
    @FXML
    private CheckBox jarCheck;
    @FXML
    private Button startButton;
    @FXML
    private Label tips;
    @FXML
    private TextField putPathTextField1;
    @FXML
    private ScrollPane decompileProgressParent;
    @FXML
    private TextField minecraftPathTextField1;
    @FXML
    private TextField minecraftCorePathTextField;
    @FXML
    private TextField minecraftVersionTextField;
    File minecraftPath;
    public String putPath;
    
    public DecompilerGui() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resource/DecompilerGui.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            decompileProgress.setHgap(20);
            decompileProgress.setVgap(20);
            byte[] def = "".getBytes();
            {
                String readedMinecraftPath = byteArrayToString(Preferences.userRoot().getByteArray("MinecraftPath", def));
                minecraftPathTextField.setText(readedMinecraftPath);
                minecraftPathTextField1.setText(readedMinecraftPath);
                minecraftPath = new File(readedMinecraftPath);
                if (new File(readedMinecraftPath).exists()) {
                    versionsViewFlash();
                    minecraftVersionsView.setDisable(false);
                }
            }
            {
                String readedPutPath = byteArrayToString(Preferences.userRoot().getByteArray("PutPath", def));
                putPathTextField.setText(readedPutPath);
                putPathTextField1.setText(readedPutPath);
                putPath = readedPutPath;
            }
            minecraftVersionsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            this.getStyleClass().add("text_color: rgb(135,147,154);");
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            decompileProgress.prefWidthProperty().bind(decompileProgressParent.widthProperty().subtract(5));
            minecraftPathTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                File minecraftPathNew = new File(minecraftPathTextField.getText());
                if (!minecraftPathNew.exists()) {
                    tip("路径不存在", 2000);
                } else if (!new File(minecraftPathNew.getPath() + "\\" + "versions").exists() | !new File(minecraftPathNew.getPath() + "\\" + "libraries").exists()) {
                    tip("路径不正确或者minecraft文件缺失", 2000);
                } else {
                    minecraftPath = minecraftPathNew;
                }
            });
            minecraftVersionsView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> startButton.setDisable(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void selectAll() {
        minecraftVersionsView.getSelectionModel().selectAll();
    }
    
    @FXML
    void refreshPath(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            File minecraftPath = new File(((TextField) event.getSource()).getText());
            if (!minecraftPath.exists()) {
                tip("路径不存在", 2000);
            } else if (!new File(minecraftPath.getPath() + "\\" + "versions").exists() | !new File(minecraftPath.getPath() + "\\" + "libraries").exists()) {
                tip("路径不正确或者minecraft文件缺失", 2000);
            } else {
                this.minecraftPath = minecraftPath;
                startButton.setDisable(false);
                minecraftVersionsView.setDisable(false);
                versionsViewFlash();
            }
            putPath = putPathTextField.getText();
            if (!new File(putPath).exists()) {
                new File(putPath).mkdirs();
                tip("路径不存在，已经创建完成", 2000);
            } else if (!new File(putPath).isDirectory()) {
                tip("不是路径", 2000);
                putPathTextField.setText("");
                putPath = null;
            }
        }
    }
    
    @FXML
    void selectPath(MouseEvent event) {
        DirectoryChooser minecraftPathChooser = new DirectoryChooser();
        minecraftPathChooser.setTitle("选择.minecraft文件夹路径");
        new FileChooser.ExtensionFilter("JAR", "*.jar");
        //minecraftPathChooser.setInitialDirectory(new File("C:\\Users\\.minecraft"));
        File minecraftPath =
                minecraftPathChooser.showDialog(this.getScene().getWindow());
        if (minecraftPath != null) {
            this.minecraftPath = minecraftPath;
            minecraftPathTextField.setText(minecraftPath.getPath());
            minecraftPathTextField1.setText(minecraftPath.getPath());
            minecraftVersionsView.setDisable(false);
            Preferences.userRoot().putByteArray("MinecraftPath", minecraftPath.getPath().getBytes());
            versionsViewFlash();
        }
    }
    
    @FXML
    void selectPutPath() {
        DirectoryChooser putPathChooser = new DirectoryChooser();
        putPathChooser.setTitle("选择导出MinecraftDefaultPack文件夹路径");
        File put =
                putPathChooser.showDialog(this.getScene().getWindow());
        if (put != null) {
            this.putPath = put.getPath();
            if (!putPath.endsWith("\\")) {
                putPath = putPath + "\\";
            }
            putPathTextField.setText(put.getPath());
            putPathTextField1.setText(put.getPath());
            Preferences.userRoot().putByteArray("PutPath", putPath.getBytes());
        }
    }
    public void versionsViewFlash(){
        minecraftVersionsView.getItems().removeAll(minecraftVersionsView.getItems());
        File file=new File(minecraftPath.getPath()+"\\versions");
        //判断是否有目录
        if(file.isDirectory()) {
            File[] files=file.listFiles();
            assert files != null;
            Arrays.stream(files).forEach(f-> {
                System.out.println(f);
                String regVersion = "(\\d)+\\.+(\\d)+(\\.+(\\d))?";//x.x.x或x.x形式
                if (f.getName().matches(regVersion)) {
                    minecraftVersionsView.getItems().add(f.getName());
                }
            });
            try {
                minecraftVersionsView.getSelectionModel().selectFirst();
                startButton.setDisable(false);
            } catch (Exception e) {
                tip("还没有任何Minecraft版本", 5000);
            }
        }
    }
    
    @FXML
    void start(MouseEvent event) {
        if (librariesCheck.isSelected() | jarCheck.isSelected()) {
            if (decompileModeSelectPane.getSelectionModel().getSelectedIndex() == 0) {
                List<String> versions = minecraftVersionsView.getSelectionModel().getSelectedItems();
                putPath = putPathTextField.getText();
                if (!putPath.endsWith("\\")) {
                    putPath = putPath + "\\";
                }
                for (String version : versions) {
                    PackDecompiler packDecompiler = new PackDecompiler(
                            minecraftPath,
                            version,
                            putPath + "minecraftDefaultPack_" + version
                    );
                    packDecompiler.librariesAble(librariesCheck.isSelected());
                    packDecompiler.jarAble(jarCheck.isSelected());
                    ProgressPane progressPane = new ProgressPane(packDecompiler, version);
                    decompileProgress.getChildren().add(progressPane);
                    if ((!librariesCheck.isSelected()) && jarCheck.isSelected()) {
                        progressPane.onlyIndeterminateProgress();
                    }
                }
            } else if (decompileModeSelectPane.getSelectionModel().getSelectedIndex() == 1) {
                putPath = putPathTextField.getText();
                if (!putPath.endsWith("\\")) {
                    putPath = putPath + "\\";
                }
                String version = minecraftVersionTextField.getText();
                PackDecompiler packDecompiler = new PackDecompiler(
                        minecraftPath,
                        version,
                        putPath + "minecraftDefaultPack_" + version
                );
                packDecompiler.librariesAble(librariesCheck.isSelected());
                packDecompiler.jarAble(jarCheck.isSelected());
                ProgressPane progressPane = new ProgressPane(packDecompiler, version);
                decompileProgress.getChildren().add(progressPane);
                if ((!librariesCheck.isSelected()) && jarCheck.isSelected()) {
                    progressPane.onlyIndeterminateProgress();
                }
            }
        } else {
            tip("未选择反混淆项", 2000);
        }
    }
    
    public void tip(String text, int time) {
        new Thread(() -> {
            Platform.runLater(() -> tips.setText(text));
            try {
                Thread.sleep(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> tips.setText(""));
        }).start();
    }
    
    @FXML
    void selectJarPath(MouseEvent event) {
        FileChooser minecraftJarChooser = new FileChooser();
        minecraftJarChooser.setInitialDirectory(new File(minecraftPathTextField.getText() + "\\versions"));
        minecraftJarChooser.setTitle("选择minecraft核心Jar");
        new FileChooser.ExtensionFilter("JAR", "*.jar");
        //minecraftPathChooser.setInitialDirectory(new File("C:\\Users\\.minecraft"));
        File minecraftJar =
                minecraftJarChooser.showOpenDialog(this.getScene().getWindow());
        if (minecraftJar != null) {
            minecraftCorePathTextField.setText(minecraftJar.getPath());
//            versionsViewFlash();
        }
    }
    
    public String byteArrayToString(byte[] a) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte i : a) {
            char c = (char) i;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
    
}
