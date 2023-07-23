package indi.etern.minecraftpackagepro.component.main;

import com.google.gson.Gson;
import indi.etern.minecraftpackagepro.component.view3D.ModelView;
import indi.etern.minecraftpackagepro.dataBUS.model.Model;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class modelTest extends Application{
    static Model cube;
    public static void main(String[] args) {
        cube = new Gson().fromJson("{\n" +
                "    \"elements\": [\n" +
                "        {   \"from\": [ 0, 0, 0 ],\n" +
                "            \"to\": [ 16, 16, 16 ],\n" +
                "            \"faces\": {\n" +
                "                \"down\":  { \"texture\": \"#down\", \"cullface\": \"down\" },\n" +
                "                \"up\":    { \"texture\": \"#up\", \"cullface\": \"up\" },\n" +
                "                \"north\": { \"texture\": \"#north\", \"cullface\": \"north\" },\n" +
                "                \"south\": { \"texture\": \"#south\", \"cullface\": \"south\" },\n" +
                "                \"west\":  { \"texture\": \"#west\", \"cullface\": \"west\" },\n" +
                "                \"east\":  { \"texture\": \"#east\", \"cullface\": \"east\" }\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}"
                , Model.class);
        Application.launch();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        final ModelView modelView = new ModelView();
        Scene scene = new Scene(modelView,1000,700);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        modelView.setModel(cube);
    }
}
