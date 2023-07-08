package indi.etern.minecraftpackagepro.component.view3D;

import indi.etern.minecraftpackagepro.io.indexScanner.Cube_old;

import java.util.Arrays;

public class JsonModel {
    private int[] points = new int[0];
    private int[] texCoords = new int[0];
    private int[] faces = new int[0];
    public void addCubes(Cube_old... cubeOlds){
        // TODO
        for (Cube_old cubeOld : cubeOlds) {
//            int[] points = cube.getPointsOf(Cube.Face.all);
//            int[] texCoords = cube.getTexCoordsOf();
//            int[] faces = cube.getFacesOf();
//            this.points = merge(this.points, points);
//            this.texCoords = merge(this.texCoords, texCoords);
//            this.faces = merge(this.faces, faces);
        }
    }
    private int[] merge(int[] array01 , int[] array02){
        int array1Length = array01.length; // 获取array01的长度
        int array2Length = array02.length; // 获取array02的长度
        // 向array01中拷贝元素并为array01扩容
        array01 = Arrays.copyOf(array01, array1Length + array2Length);
        System.arraycopy(array02, 0, array01, array1Length, array2Length);
        return array01;
    }
    public int[] getPoints() {
        return points;
    }
    public int[] getTexCoords() {
        return texCoords;
    }
    public int[] getFaces() {
        return faces;
    }
}
