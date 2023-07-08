package indi.etern.minecraftpackagepro.component.view3D;

import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class ModelView extends AnchorPane {
    public ModelView() {
        Mesh triangleMesh = new TriangleMesh();
        MeshView meshView = new MeshView();
        meshView.setMesh(triangleMesh);
        Scene scene3d = new Scene(this, 800, 600, true, SceneAntialiasing.BALANCED);
    }
}
