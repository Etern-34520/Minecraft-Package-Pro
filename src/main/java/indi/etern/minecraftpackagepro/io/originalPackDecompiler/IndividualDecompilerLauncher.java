package indi.etern.minecraftpackagepro.io.originalPackDecompiler;

import indi.etern.minecraftpackagepro.component.tools.decompiler.DecompilerGui ;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.Optional;

public class IndividualDecompilerLauncher extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		DecompilerGui decompilerGui = new DecompilerGui();
		Scene scene = new Scene(decompilerGui);
		stage.setScene(scene);
		JMetro jMetro = new JMetro();
		jMetro.setScene(scene);
		jMetro.setStyle(Style.DARK);
		stage.show();
		stage.setTitle("Minecraft原版资源包提取器");
		stage.setOnCloseRequest(event -> {
			if (decompilerGui.decompileProgress.getChildren().size() != 0) {
				Alert warming = new Alert(Alert.AlertType.CONFIRMATION, "确认关闭？");
				warming.setHeaderText("任务正在进行，确认关闭?");
				Optional<ButtonType> result = warming.showAndWait();
				if (result.get() == ButtonType.OK) {
					System.out.println("closed");
					System.exit(0);
				} else {
					event.consume();
				}
			}
		});
	}
}
