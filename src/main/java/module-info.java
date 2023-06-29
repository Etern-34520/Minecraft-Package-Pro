module indi.etern.minecraftpackagepro.minecraftpackagepro {
    requires java.prefs;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfxtras.styles.jmetro;
    requires commons.io;
    requires com.google.gson;
    requires commons.lang3;
    requires guava;
    
    opens indi.etern.minecraftpackagepro.component.bench to javafx.fxml;
    opens indi.etern.minecraftpackagepro.component.edit.colorPicker to javafx.fxml;
    opens indi.etern.minecraftpackagepro.component.edit.colorPlate to javafx.fxml;
    opens indi.etern.minecraftpackagepro.component.tools.decompiler to javafx.fxml;
    exports indi.etern.minecraftpackagepro.component.main;
    opens indi.etern.minecraftpackagepro.component.tasks to javafx.fxml;
}