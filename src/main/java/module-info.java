module indi.etern.minecraftpackagepro.minecraftpackagepro {
    requires javafx.controls;
    requires javafx.fxml;
    
    
    opens indi.etern.minecraftpackagepro.minecraftpackagepro to javafx.fxml;
    exports indi.etern.minecraftpackagepro.minecraftpackagepro;
}