package indi.etern.minecraftpackagepro.component.bench;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Window extends GridPane{
	double x;
	double y;
	double sx;
	double sy;
	int arrenge;
	@FXML
	Pane windowTitle;
	@FXML
	Button hideButton;
	@FXML
	GridPane basic;
	private ToggleButton connectedButton;
	@FXML
	private Pane paneNW;

	@FXML
	private Pane paneN;

	@FXML
	private Pane paneNE;

	@FXML
	private Pane paneW;

	@FXML
	private Pane paneE;

	@FXML
	private Pane paneSW;

	@FXML
	private Pane paneSE;

	@FXML
	private Pane paneS;
	private final List<Pane> panes = new ArrayList<>();
	
	public Window(Node node, ToggleButton connectedButton) {
		try {
			this.connectedButton = connectedButton;
			FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("")+"resources/"+getClass().getSimpleName()+".fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
			this.add(node,1,2,1,1);
			//windowTitle.setMinWidth();
			//windowTitle.setPrefHeight(node.getLayoutBounds().getHeight());
			panes.add(paneNE);
			panes.add(paneN);
			panes.add(paneNW);
			panes.add(paneE);
			panes.add(paneW);
			panes.add(paneSE);
			panes.add(paneS);
			panes.add(paneSW);
			this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
			if (node instanceof Pane) {
				getRowConstraints().get(2).setMinHeight(((Pane) node).getMinHeight());
				getColumnConstraints().get(1).setMinWidth(((Pane) node).getMinWidth());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML void windowHide(){
		connectedButton.setSelected(false);
	}

	/*@FXML void windowMove(MouseEvent event){
		Insets margin = GridPane.getMargin(this);
		double x = margin.getLeft();
		double y = margin.getTop();
		double newX = x + event.getX() - sx;
		double newY = y + event.getY() - sy;
		Region windowBasic=(GridPane) getParent();
		if (newX + sx < 0) {
			newX = 0 - sx;
		} else if (newX + sx > windowBasic.getWidth()) {
			newX = windowBasic.getWidth() - sx;
		}

		if (newY + sy < 0) {
			newY = 0 - sy;
		} else if (newY + sy > windowBasic.getHeight()) {
			newY = windowBasic.getHeight() - sy;
		}
		//System.out.println(windowBasic.getHeight());

		GridPane.setMargin(this, new Insets(newY, 0, 0, newX));
		thisTop();
		backLink.setVisible(true);
		backLink.setTextFill(Color.rgb(255, 255, 255));
		//System.out.println("x"+newX+"y"+newY);
	}
	@FXML
	private void thisTop() {
		for (int i = 0;i<desktop.windows.size();i=i+1) {
			Window window =desktop.windows.get(i);
			window.setStyle("-fx-border-color:rgb(50,50,50)");
			if (window != this) {
				desktop.windowBasic.getChildren().remove(window);
				desktop.windowBasic.getChildren().add(window.arrenge+1, window);
			}
			arrenge=0;
		}
		this.setStyle("-fx-border-color:rgb(0,120,215)");
	}*/
	public void pin(){
		for (Pane pane : panes) {
			pane.setCursor(Cursor.DEFAULT);
		}
	}
	public void  unPin(){
		paneNE.setCursor(Cursor.NE_RESIZE);
		paneN.setCursor(Cursor.N_RESIZE);
		paneNW.setCursor(Cursor.NW_RESIZE);
		paneE.setCursor(Cursor.E_RESIZE);
		paneW.setCursor(Cursor.W_RESIZE);
		paneSE.setCursor(Cursor.SE_RESIZE);
		paneS.setCursor(Cursor.S_RESIZE);
		paneSW.setCursor(Cursor.SW_RESIZE);
	}
	/*@FXML
	void movePrepare(MouseEvent event){
		sx=event.getX();
		sy=event.getY();
		thisTop();
	}*/
	@FXML
	void draggedE(MouseEvent event) {

	}

	@FXML
	void draggedN(MouseEvent event) {

	}

	@FXML
	void draggedNE(MouseEvent event) {

	}

	@FXML
	void draggedNW(MouseEvent event) {

	}

	@FXML
	void draggedS(MouseEvent event) {

	}

	@FXML
	void draggedSE(MouseEvent event) {

	}

	@FXML
	void draggedSW(MouseEvent event) {

	}

	@FXML
	void draggedW(MouseEvent event) {

	}

}
