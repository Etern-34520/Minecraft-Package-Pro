package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class ColorPlate extends GridPane {
@FXML
public ToggleGroup color;
private ColorPicker colorPicker;
public void setColorPicker(ColorPicker colorPicker){
	this.colorPicker = colorPicker;
}
	public ColorPlate() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("resource/ColorPlate.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();
	}
	
	public void colorSet(int r, int g, int b, double a) {
			double o =Math.round(a * 1000 / 100) / 1000.0;
			boolean same = false;
			ToggleButton selectedBotton = (ToggleButton) color.getSelectedToggle();
			for (int i = 0; i < color.getToggles().size(); i = i + 1) {
				if (color.getToggles() != null) {
					String style = ((ToggleButton) color.getToggles().get(i)).getStyle();
					int length = style.length();
					/*
					colorShow1.setFill(Color.rgb(r,g,b));
					colorShow2.setFill(Color.rgb(r,g,b,o));
					*/
					if (length > 25) {
						String rgba = style.substring(25, length - 1);
						String delimeter = ","; // 指定分割字符
						String[] temp = rgba.split(delimeter); // 分割字符串
						int r1 = Integer.parseInt(temp[0]);
						int g1 = Integer.parseInt(temp[1]);
						int b1 = Integer.parseInt(temp[2]);
						int a1 = (int) ((Double.parseDouble(temp[3])) * 100);
						if (r1 == r && g1 == g && b1 == b && a1 == a) {
							same = true;
						}
					} else {
						//same = true;
					}
				}
			}
			if (selectedBotton != null && !same) {
				selectedBotton.setStyle("-fx-background-color:" + "rgb(" + r + "," + g + "," + b + "," + o + ")");
			}
	}
	
	public void colorChoose() {
		ToggleButton selectedBotton = (ToggleButton) color.getSelectedToggle();
		if (selectedBotton != null) {
			String style = selectedBotton.getStyle();
			int length = style.length();
			if (length > 25) {
				String rgba = style.substring(25, length - 1);
				String delimeter = ","; // 指定分割字符
				String[] temp = rgba.split(delimeter); // 分割字符串
				int r = Integer.parseInt(temp[0]);
				int g = Integer.parseInt(temp[1]);
				int b = Integer.parseInt(temp[2]);
				int a = (int) ((Double.parseDouble(temp[3])) * 100);
				colorPicker.setColor(r,g,b,a);
			}
		}
	}
}
