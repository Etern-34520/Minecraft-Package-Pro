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
