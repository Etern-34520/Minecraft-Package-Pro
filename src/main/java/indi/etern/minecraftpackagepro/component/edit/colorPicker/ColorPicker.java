package indi.etern.minecraftpackagepro.component.edit.colorPicker;

import indi.etern.minecraftpackagepro.component.edit.colorPlate.ColorPlate;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends GridPane{
	boolean cvs = true;//syncSliderAndTextValue
	@FXML
	private Polygon colorPointer;
	@FXML
	private GridPane colorPicker;
	@FXML
	private Slider sliderR;
	@FXML
	private Slider sliderG;
	@FXML
	private Slider sliderB;
	@FXML
	private Slider sliderA;
	@FXML
	private TextField textR;
	@FXML
	private TextField textG;
	@FXML
	private TextField textB;
	@FXML
	private TextField textA;
	@FXML
	private ImageView dotLight;
	@FXML
	private ImageView dotDark;
	@FXML
	private Pane alphaBackground1;
	@FXML
	private Pane colorPane;
	@FXML
	private Rectangle colorBottom;
	@FXML
	private Rectangle colorTop;
	@FXML
	private ToggleGroup color;
	private ColorPlate colorPlate;
	public void setColorPlate(ColorPlate colorPlate){
		this.colorPlate = colorPlate;
		colorPlate.setColorPicker(this);
	}
	public ColorPicker() throws Exception{
		FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("")+"resources/"+getClass().getSimpleName()+".fxml"));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();
		List<TextField> textFields= new ArrayList<>();
		textFields.add(textR);
		textFields.add(textG);
		textFields.add(textB);
		textFields.add(textA);
		for (TextField textField : textFields) {
			textField.focusedProperty().addListener((ChangeListener<Object>) (arg0, arg1, arg2) -> {
				String t = textField.getText();
				if (t.equals("")) {
					t = "0";
					if(textField==textA){
						t = "100";
					}
					textField.setText(t);
				}
					((Slider) lookup("#slider"+textField.getId().charAt(4))).setValue(Integer.parseInt(t));
					//sliderRed.setValue(Integer.valueOf(r).intValue());
					positioningAccordingToCurrentColor();
				});
		}
	}
	
	private void positioningAccordingToCurrentColor() {
		double a = 140;
		double border = 0.5;
		int r = (int) sliderR.getValue();
		int g = (int) sliderG.getValue();
		int b = (int) sliderB.getValue();
		Color color = Color.rgb(r, g, b);
		double h = color.getHue();
		double s = color.getSaturation();
		double v = color.getBrightness();
		colorPane.setRotate(-h);
		setDotX(s * (a - 2 * border) + border);
		setDotY(a - v * (a - 2 * border) - border);
		cvs = false;
		basicColorChange(null);
		cvs = true;
		alphaBackground1.setOpacity((100-sliderA.getValue())/100.0);
		setDotPositionOfColor(r, g, b, v);
	}
	
	private void setDotPositionOfColor(int r, int g, int b, double v) {
		if (alphaBackground1.getOpacity() < 0.5) {
			dotLight.setVisible(true);
			dotDark.setVisible(false);
		}
		if (v > 0.5) {
			dotLight.setVisible(false);
			dotDark.setVisible(true);
		}
		if (v < 0.5) {
			dotLight.setVisible(true);
			dotDark.setVisible(false);
		}
		
		if (alphaBackground1.getOpacity() > 0.5) {
			dotLight.setVisible(false);
			dotDark.setVisible(true);
		}
		colorPlate.setSelectedButtonColor(r, g, b, sliderA.getValue());
	}
	
	@FXML
	private void basicColorChange(MouseEvent e) {

		double ro = colorPane.getRotate();// 度数
		if (e != null) {
			double x = e.getX();
			double y = e.getY();
			double a=Math.tanh(x*10000/(120-y)/10000.0) * 180 / Math.PI;
			if (y <= 120) {
				colorPane.setRotate(ro + a);
			}
		}

		int r = 0;
		int b = 0;
		int g = 0;

		ro = -ro;// 纠正左右翻转
		int ch = Math.abs((int) Math.pow(-1,(int) ro / 360));
		ro = ch * ro - ch * 360 * ((int) ro / 360);//不要改，否则无法正常运行
		if (0 < ro & ro <= 60) {
			r = 255;
			g = (int) (255 * ro / 60);
			//b = 0;
		} else if (60 < ro & ro <= 120) {
			r = (int) (-255 * ro / 60 + 510);
			g = 255;
			//b = 0;
		} else if (120 < ro & ro <= 180) {
			//r = 0;
			g = 255;
			b = (int) (255 * ro / 60 - 510);
		} else if (180 < ro & ro <= 240) {
			//r = 0;
			g = (int) (-255 * ro / 60 + 1020);
			b = 255;
		} else if (240 < ro & ro <= 300) {
			r = (int) (255 * ro / 60 - 1020);
			//g = 0;
			b = 255;
		} else if (300 < ro & ro <= 360) {
			r = 255;
			//g = 0;
			b = (int) (-255 * ro / 60 + 1530);
		} else if (-360 < ro & ro <= -300) {
			r = 255;
			g = (int) (255 * ro / 60 + 1530);
			//b = 0;
		} else if (-300 < ro & ro <= -240) {
			r = (int) (-255 * ro / 60 - 1020);
			g = 255;
			//b = 0;
		} else if (-240 < ro & ro <= -180) {
			//r = 0;
			g = 255;
			b = (int) (255 * ro / 60 + 1020);
		} else if (-180 < ro & ro <= -120) {
			//r = 0;
			g = (int) (-255 * ro / 60 - 510);
			b = 255;
		} else if (-120 < ro & ro <= -60) {
			r = (int) (255 * ro / 60 + 510);
			//g = 0;
			b = 255;
		} else if (-60 < ro & ro <= 0) {
			r = 255;
			//g = 0;
			b = (int) (-255 * ro / 60);
		}
		colorBottom.setFill(Color.rgb(r, g, b));
		if (cvs) syncSliderAndTextValue();
	}

	private void syncSliderAndTextValue() {
		double dx = dotLight.getLayoutX();
		double dy = dotLight.getLayoutY();
		double a = 140;
		double border = 0.5;

		double h = -colorPane.getRotate() % 360;
		double v = Math.round((a - dy) * 1000 / (a - 2 * border)) / 1000.0 - 0.004;
		double s = Math.round(dx * 1000 / (a - 2 * border)) / 1000.0 - 0.004;
		//System.out.println(v+","+s);
		if (v >= 1)
			v = 1.0;
		if (v <= 0)
			v = 0.0;
		if (s >= 1)
			s = 1.0;
		if (s <= 0)
			s = 0.0;
		Color color = Color.hsb(h, s, v);
		int r = (int) (color.getRed() * 255);
		int g = (int) (color.getGreen() * 255);
		int b = (int) (color.getBlue() * 255);
		sliderR.setValue(r);
		sliderG.setValue(g);
		sliderB.setValue(b);
		textR.setText(Integer.toString(r));
		textG.setText(Integer.toString(g));
		textB.setText(Integer.toString(b));
		setDotPositionOfColor(r, g, b, v);
	}

	private void setDotX(double x) {
		dotLight.setLayoutX(x);
		dotDark.setLayoutX(x);
	}

	private void setDotY(double y) {
		dotLight.setLayoutY(y);
		dotDark.setLayoutY(y);
	}
	
	@FXML
	public void dotPositioning(MouseEvent e) {
		double ex = e.getX();// 鼠标X位置
		double ey = e.getY();// 鼠标Y位置
		double a = colorTop.getWidth();
		boolean top = false;// 触碰到上
		boolean bottom = false;// 触碰到底
		boolean left = false;// 触碰到左
		boolean right = false;// 触碰到右
		double border = 0.5;
		double addX = 0.5;
		double addY = 0.5;
		if (ex + addX < 0 + border) {
			setDotX(0 + border);
			setDotY(ey + addY);
			left = true;

		}
		if (ex + addX > a - border) {
			setDotX(a - border);
			setDotY(ey + addY);
			right = true;
		}
		if (ey + addY < 0 + border) {
			setDotX(ex + addX);
			setDotY(0 + border);
			top = true;
		}
		if (ey + addY > a - border) {
			setDotX(ex + addX);
			setDotY(a - border);
			bottom = true;
		}
		// 这是一段很有意思的测试代码，不用管它
		/*
		 * System.out.println("  -----"+top+"-----");
		 * System.out.println("  |             |");
		 * System.out.println("  |             |");
		 * System.out.println(left+"          "+right);
		 * System.out.println("  |             |");
		 * System.out.println("  |             |");
		 * System.out.println("  -----"+bottom+"-----");
		 */
		if (left && top) {
			setDotX(0 + border);
			setDotY(0 + border);
		} else if (left && bottom) {
			setDotX(0 + border);
			setDotY(a - border);
		} else if (right && top) {
			setDotX(a - border);
			setDotY(0 + border);
		} else if (right && bottom) {
			setDotX(a - border);
			setDotY(a - border);
		} else if (!(top | bottom | left | right)) {
			setDotX(ex + addX);
			setDotY(ey + addY);
		}
		syncSliderAndTextValue();
		//这一段是为了判断是否显示鼠标
		if(showMouse) colorTop.setCursor(Cursor.CROSSHAIR);
		else colorTop.setCursor(Cursor.NONE);
	}
	
	@FXML
	public void sliderToText(Event e) {
		Slider slider = (Slider) this.lookup("#"+e.getSource().toString().substring(10,17));
		TextField textField = (TextField) this.lookup("#"+"text"+e.getSource().toString().charAt(16));
		int value = ((int) slider.getValue());
		textField.setText(String.valueOf(value));
		positioningAccordingToCurrentColor();
	}
	
	@FXML
	public void textToSlider(Event e) {
		TextField textField = (TextField) this.lookup("#"+e.getSource().toString().substring(13,18));
		Slider slider = (Slider) this.lookup("#"+"slider"+e.getSource().toString().charAt(17));
		String text = textField.getText();
		if (!text.equals("")) {
			try {
				if (text.matches("\\d*")) {
					int Value = Integer.parseInt(text);
					if (Value > 255) {
						textField.setText("255");
					}
					slider.setValue(Value);
				} else {
					textField.setText("0");
				}
			} catch (NumberFormatException ignored) {
			}
			positioningAccordingToCurrentColor();
		}
		if (text.equals("00")) {
			textField.setText("0");
		}
		positioningAccordingToCurrentColor();
	}
	public void setColor(int r, int g, int b, int a) {
		sliderR.setValue(r);
		sliderG.setValue(g);
		sliderB.setValue(b);
		sliderA.setValue(a);
		textR.setText(Integer.toString(r));
		textG.setText(Integer.toString(g));
		textB.setText(Integer.toString(b));
		textA.setText(Integer.toString(a));
		positioningAccordingToCurrentColor();
	}
	@FXML
	private void pointerCursorHandClose(){
		colorPointer.setCursor(Cursor.CLOSED_HAND);
	}
	@FXML
	private void pointerCursorHandOpen(){
		colorPointer.setCursor(Cursor.OPEN_HAND);
	}
	@FXML
	private void hideMouse(MouseEvent event){
		dotPositioning(event);
		colorTop.setCursor(Cursor.NONE);
		showMouse=false;
	}
	@FXML
	private void decideToHideMouse(MouseEvent event){
		if(event.isPrimaryButtonDown()|event.isSecondaryButtonDown()|event.isMiddleButtonDown()){
			colorTop.setCursor(Cursor.NONE);
			showMouse=false;
		}
	}
	boolean showMouse=false;
	@FXML
	private void showMouse(MouseEvent event){
		showMouse=true;
	}
	
	public Color getColor() {
		return new Color(sliderR.getValue()*1000/255/1000.0, sliderG.getValue()*1000/255/1000.0, sliderB.getValue()*1000/255/1000.0, sliderA.getValue()*0.01);
	}
}
