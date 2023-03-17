package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.MeshView;

public class Read {
	static Tab newTab;
	@FXML
	static MeshView view3D;
	@FXML
	static Pane view2D;
	static GridPane basicPane;
	public Tab oneFile(String root) {
		//EditPane.waitingText = "读取中";
		//new EditPane().waitingStart();
		try {
			//System.out.println(root);
			Parent parent=FXMLLoader.load(getClass().getResource("Tab.fxml"));
			view2D=(Pane) parent.lookup("#view2D");
			view3D=(MeshView) parent.lookup("#view3D");
			basicPane= (GridPane) parent.lookup("#view3D");
			Canvas canvas=new Canvas();
			GraphicsContext gc=canvas.getGraphicsContext2D();
			FileInputStream input;
			input = new FileInputStream(root);
			Image image=new Image(input);
			//gc.drawImage(image, 0, 0,canvas.getWidth(),canvas.getHeight());
			//view2D.getChildren().add(canvas);
			//System.out.println("abab");
			File file =new File(root);
			String name=file.getName();
			String suffix = null;
			for (String i: name.split("\\.")) suffix=i;
			List<String> allowedSuffix=new ArrayList<String>();
			allowedSuffix.add("jpg");
			allowedSuffix.add("png");
			allowedSuffix.add("bmp");
			allowedSuffix.add("gif");
			int length=allowedSuffix.size();
			for (int i = 0;i<length;i=i+1) {
				if (suffix.toLowerCase().equals(allowedSuffix.get(i))) {
					newTab=new Tab(name , basicPane);
					return newTab;
				}
			}
			System.out.println("not support suffix:"+suffix);
			return null;
			//System.out.println(file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//new EditPane().waitingFinish();
		return null;
	}
}
