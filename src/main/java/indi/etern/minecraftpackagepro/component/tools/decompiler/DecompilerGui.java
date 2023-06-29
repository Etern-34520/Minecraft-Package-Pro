package indi.etern.minecraftpackagepro.component.tools.decompiler;

import indi.etern.minecraftpackagepro.component.tasks.TaskManager;
import indi.etern.minecraftpackagepro.component.tasks.TaskPane;
import indi.etern.minecraftpackagepro.component.bench.WorkBench;
import indi.etern.minecraftpackagepro.dataBUS.Setting;
import indi.etern.minecraftpackagepro.io.originalPackDecompiler.PackDecompiler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class DecompilerGui extends SplitPane {
    private final Setting setting = Setting.getInstance();
    @FXML
    public VBox decompileProgress;
    @FXML
    private TextField minecraftPathTextField;
    @FXML
    private TextField putPathTextField;
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
    private String minecraftPath;
    private String putPath;
    
    public void setWorkBench(WorkBench workBench) {
        this.workBench = workBench;
    }
    
    private WorkBench workBench;
    
    public DecompilerGui() {
        try {
            FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("") + "resources/" + getClass().getSimpleName() + ".fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
//            byte[] def = "".getBytes();
            
            String[] paths = (String[]) setting.get("DecompilerLastPath");
            if (paths != null) {
                String readPutPath = paths[1];
                if (readPutPath != null) {
                    putPathTextField.setText(readPutPath);
                    putPathTextField1.setText(readPutPath);
                    putPath = readPutPath;
                    testPutPath();
                }
                
                String readMinecraftPath = paths[0];
                if (readMinecraftPath != null && new File(readMinecraftPath).exists()) {
                    minecraftPathTextField.setText(readMinecraftPath);
                    minecraftPathTextField1.setText(readMinecraftPath);
                    minecraftPath = readMinecraftPath;
                    versionsViewFlash();
                    minecraftVersionsView.setDisable(false);
                }
            }
            
            minecraftVersionsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            this.getStyleClass().add("text_color: rgb(135,147,154);");
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            decompileProgress.prefWidthProperty().bind(decompileProgressParent.widthProperty().subtract(5));
            
            TextField[] textFields = {minecraftPathTextField, minecraftPathTextField1, putPathTextField, putPathTextField1, minecraftCorePathTextField, minecraftVersionTextField};
            for (TextField textField : textFields) {
                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    testMinecraftPath();
                    testPutPath();
                    testVersion();
                    startButton.setDisable(!minecraftVersionTextField.getText().matches("(\\d)+\\.+(\\d)+(\\.+(\\d))?"));//x.x.x
                });
            }
            minecraftVersionsView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> testPutPath());
            decompileModeSelectPane.getSelectionModel().selectedItemProperty().addListener(observable -> {
                testMinecraftPath();
                testPutPath();
                if (decompileModeSelectPane.getSelectionModel().isSelected(1)) {
                    startButton.setDisable(!minecraftVersionTextField.getText().matches("(\\d)+\\.+(\\d)+(\\.+(\\d))?"));
                }
            });
            minecraftVersionTextField.focusedProperty().addListener(observable -> {
                startButton.setDisable(!minecraftVersionTextField.getText().matches("(\\d)+\\.+(\\d)+(\\.+(\\d))?"));//x.x.x
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setVertical(boolean b) {
        if (b) setOrientation(Orientation.VERTICAL);
        else setOrientation(Orientation.HORIZONTAL);
    }
    
    @FXML
    private void selectAll() {
        minecraftVersionsView.getSelectionModel().selectAll();
    }
    
    @FXML
    private void refreshPath(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            testMinecraftPath();
        }
    }
    
    private void testMinecraftPath() {
        String minecraftPath;
        if (decompileModeSelectPane.getSelectionModel().isSelected(0)) {
            minecraftPath = minecraftPathTextField.getText();
            this.minecraftPath = minecraftPathTextField.getText();
            minecraftPathTextField1.setText(minecraftPath);
        } else {
            minecraftPath = minecraftPathTextField1.getText();
            minecraftPathTextField1.setText(minecraftPath);
        }
        if (!new File(minecraftPath).exists()) {
            tip("路径不存在", 2000);
            startButton.setDisable(true);
        } else if (!new File(minecraftPath + "\\" + "versions").exists() | !new File(minecraftPath + "\\" + "libraries").exists()) {
            tip("路径不正确或者minecraft文件缺失", 2000);
            startButton.setDisable(true);
        } else {
            this.minecraftPath = minecraftPath;
            minecraftVersionsView.setDisable(false);
            versionsViewFlash();
        }
        putPath = putPathTextField.getText();
//        System.out.println(putPath);
        testPutPath();
    }
    
    private void testPutPath() {
        File put = new File(putPath);
        if (!put.canWrite()) {
            tip("输出目录不可写", 4000);
            startButton.setDisable(true);
        } else if (!put.exists()) {
            boolean result = put.mkdirs();
            if (result) {
                tip("路径不存在，已经创建完成", 2000);
                startButton.setDisable(false);
            } else {
                tip("路径不存在，创建失败", 2000);
                startButton.setDisable(true);
            }
        } else if (!put.isDirectory()) {
            tip("不是路径", 2000);
            putPathTextField.setText("");
            putPath = null;
            startButton.setDisable(true);
        } else {
            startButton.setDisable(false);
        }
    }
    
    @FXML
    private void selectPath(MouseEvent event) {
        DirectoryChooser minecraftPathChooser = new DirectoryChooser();
        minecraftPathChooser.setTitle("选择.minecraft文件夹路径");
        new FileChooser.ExtensionFilter("JAR", "*.jar");
        //minecraftPathChooser.setInitialDirectory(new File("C:\\Users\\.minecraft"));
        File minecraftPath =
                minecraftPathChooser.showDialog(this.getScene().getWindow());
        if (minecraftPath != null) {
            this.minecraftPath = minecraftPath.getPath();
            minecraftPathTextField.setText(minecraftPath.getPath());
            minecraftPathTextField1.setText(minecraftPath.getPath());
            minecraftVersionsView.setDisable(false);
//            Preferences.userRoot().putByteArray("MinecraftPath", minecraftPath.getPath().getBytes());
            setting.put("DecompilerLastPath", new String[]{this.minecraftPath, putPath});
            versionsViewFlash();
        }
    }
    
    @FXML
    private void selectPutPath() {
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
//            Preferences.userRoot().putByteArray("PutPath", putPath.getBytes());
            setting.put("DecompilerLastPath", new String[]{minecraftPath, putPath});
        }
    }
    
    public void versionsViewFlash() {
        Platform.runLater(()->{
            minecraftVersionsView.getItems().removeAll(minecraftVersionsView.getItems());
            File file = new File(minecraftPath + "\\versions");
            //判断是否有目录
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                assert files != null;
                Arrays.stream(files).forEach(f -> {
//                System.out.println(f);
                    String regVersion = "(\\d)+\\.+(\\d)+(\\.+(\\d))?";//x.x.x或x.x形式
                    if (f.getName().matches(regVersion)) {
                        minecraftVersionsView.getItems().add(f.getName());
                    }
                });
                try {
                    minecraftVersionsView.getSelectionModel().selectFirst();
                    testPutPath();
//                startButton.setDisable(false);
                } catch (Exception e) {
                    tip("还没有任何Minecraft版本", 5000);
                }
            }
        });
    }
    
    @FXML
    private void start(MouseEvent event) {
        if (putPathTextField.getText() != null && putPathTextField1.getText() != null && minecraftVersionTextField != null && minecraftCorePathTextField != null && (librariesCheck.isSelected() | jarCheck.isSelected())) {
            if (decompileModeSelectPane.getSelectionModel().getSelectedIndex() == 0) {
                List<String> versions = minecraftVersionsView.getSelectionModel().getSelectedItems();
                putPath = putPathTextField.getText();
                if (!putPath.endsWith("\\")) {
                    putPath = putPath + "\\";
                }
                new Thread(()->{
                    for (String version : versions) {
                        PackDecompiler decompiler = new PackDecompiler(
                                new File(minecraftPath),
                                version,
                                putPath + "minecraftDefaultPack_" + version
                        );
                        decompiler.librariesAble(librariesCheck.isSelected());
                        decompiler.jarAble(jarCheck.isSelected());
                    /*ProgressPane progressPane = new ProgressPane(decompiler, version);
                    decompiler.setProgressPane(progressPane);
                    decompileProgress.getChildren().add(progressPane);
                    if ((!librariesCheck.isSelected()) && jarCheck.isSelected()) {
                        progressPane.onlyIndeterminateProgress();
                    }*/
                        newTask(decompiler);
                    }
                }).start();
            } else if (decompileModeSelectPane.getSelectionModel().getSelectedIndex() == 1) {
                minecraftPath = minecraftPathTextField1.getText();
                minecraftPathTextField.setText(minecraftPathTextField1.getText());
                putPath = putPathTextField.getText();
                if (!putPath.endsWith("\\")) {
                    putPath = putPath + "\\";
                }
                PackDecompiler decompiler = new PackDecompiler(new File(minecraftPath), new File(minecraftCorePathTextField.getText()), minecraftVersionTextField.getText(), putPath);
               /* ProgressPane progressPane = new ProgressPane(decompiler, minecraftVersionTextField.getText());
                decompiler.setProgressPane(progressPane);
                decompileProgress.getChildren().add(progressPane);
                if ((!librariesCheck.isSelected()) && jarCheck.isSelected()) {
                    progressPane.onlyIndeterminateProgress();
                }*/
                newTask(decompiler);
            }
        } else {
            tip("未选择反混淆项", 2000);
        }
    }
    
    private void newTask(PackDecompiler decompiler) {
        TaskPane taskPane = new TaskPane() {
            
            @Override
            protected void pause() {
//                        pauseButtonImageView.setImage(new Image(pauseImage1Path));
                decompiler.pause();
            }
            
            @Override
            protected void resume() {
//                        pauseButtonImageView.setImage(new Image(pauseImage2Path));
                decompiler.reStart();
            }
            
            @Override
            protected void cancel() {
                decompiler.cancel();
                
            }
            
            @Override
            protected void finish() {
            
            }
            
            @Override
            protected void doTask() {
                decompiler.setTaskPane(this);
                decompiler.start();
                boolean progressStateIsNormal = true;
                while (!decompiler.isOver()) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (decompiler.isReachException()) {
                        setTip("遇到错误", -1);
                        decompiler.getExceptions().forEach(e -> {
                            setMoreInfo(getMoreInfo()+e.getMessage()+"\n");
                        });
                        break;
                    }
                    if (progressStateIsNormal) {
                        setProgress(decompiler.getProgress());
                    }
                    if (decompiler.getProgress() == 100) {
                        progressStateIsNormal = false;
                        Platform.runLater(() -> {
                            setTip("解压缩中", -1);
                            setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                        });
                    }
                }
                setProgress(100);
            }
        };
        taskPane.setTitle(decompiler.getVersion());
        taskPane.setMoreInfo("");
        for (Node node: workBench.getComponents()) {
            if (node instanceof TaskManager) {
                ((TaskManager) node).addTask(taskPane);
                new Thread(taskPane).start();
            }
        }
    }
    
    private void testVersion() {
        String version = minecraftVersionTextField.getText();
        if (minecraftVersionTextField.getText().matches("(\\d)+\\.+(\\d)+(\\.+(\\d))?")) {
            String[] versionNum = version.split("\\.");
            File versionHash = new File(minecraftPath + "\\assets\\indexes\\" + versionNum[0] + "." + versionNum[1] + ".json");
            if (!versionHash.exists()) {
                tip("版本不存在", 2000);
            }
        } else {
            tip("版本格式错误", 2000);
        }
        File jar = new File(minecraftCorePathTextField.getText());
        if (!jar.exists()) tip("核心Jar位置错误", 2000);
        else if (!jar.canRead()) tip("核心Jar不可读", 2000);
    }
    
    public void tip(String text, int time) {
        versionsViewFlash();
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
    private void selectJarPath(MouseEvent event) {
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
    
}
