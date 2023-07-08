package indi.etern.minecraftpackagepro.dataBUS.model;

import java.util.ArrayList;
import java.util.List;

public class Cube {
    public String __comment;
    public double[] from;
    public double[] to;
    public Faces faces = new Faces();
    
    public List<Face> getFaces() {
        return new ArrayList<>(){{add(faces.down);add(faces.up);add(faces.north);add(faces.south);add(faces.west);add(faces.east);}};
    }
}
