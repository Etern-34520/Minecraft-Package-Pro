package indi.etern.minecraftpackagepro.component.bench;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public class MessagePane {
    public MessagePane() {
        try {
            FXMLLoader loader = new FXMLLoader(new URL(getClass().getResource("")+"resources/"+getClass().getSimpleName()+".fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
