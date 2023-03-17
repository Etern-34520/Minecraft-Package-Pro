module indi.etern.minecraftpackagepro.minecraftpackagepro {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfxtras.styles.jmetro;
    requires java.prefs;
    requires commons.io;
    requires com.google.gson;
    requires commons.lang3;
    requires guava;
    requires javafx.graphics;
    requires java.desktop;
    
    
    opens indi.etern.minecraftpackagepro.component.main to javafx.fxml;
    opens indi.etern.minecraftpackagepro.component.bench to javafx.fxml;
    opens indi.etern.minecraftpackagepro.component.edit.colorPicker to javafx.fxml;
    opens indi.etern.minecraftpackagepro.component.edit.colorPlate to javafx.fxml;
    exports indi.etern.minecraftpackagepro.component.main;
}