package indi.etern.minecraftpackagepro.io.indexScanner;

import com.google.gson.Gson;
import indi.etern.minecraftpackagepro.dataBUS.model.Face;
import indi.etern.minecraftpackagepro.dataBUS.model.Model;
import indi.etern.minecraftpackagepro.dataBUS.model.Cube;

import java.io.FileNotFoundException;

class test {
    public static void main(String[] args) throws FileNotFoundException {
        Model model = new Model();
        model.elements = new Cube[1];
        final Cube cube = new Cube();
        model.elements[0] = cube;
        cube.__comment = "test";
        cube.from = new double[0];
        cube.to = new double[0];
        final Face face = new Face();
        face.texture = "test";
        face.uv = new double[0];
        face.cullface = "down";
        face.rotation = 0;
        cube.faces.down = face;
        cube.faces.up = face;
        cube.faces.north = face;
        cube.faces.south = face;
        cube.faces.west = face;
        cube.faces.east = face;
        
        Gson gson = new Gson();
        String json = gson.toJson(model);
        System.out.println(json);
        Model model2 = gson.fromJson(json, Model.class);
        System.out.println(model2.elements[0].faces.down.texture);
        
        Model anvil = gson.fromJson(new java.io.FileReader("C:\\Users\\o_345\\Desktop\\anvil.json"), Model.class);
        System.out.println(anvil.elements[0].faces.down.texture);
    }
}