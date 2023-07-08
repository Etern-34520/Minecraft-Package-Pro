package indi.etern.minecraftpackagepro.io.indexScanner;

import javafx.geometry.Point3D;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cube_old {
	double fromX;
	double fromY;
	double fromZ;
	double length;
	double width;
	double height;
	boolean fromAlready;
	private File parent;
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private final Map<Face, File> face2TextureMap = new HashMap<>();
	
	public String getTextureOf(Face face) {
		File texture = face2TextureMap.get(face);
		if (texture == null) {
			return null;
		}
		return texture.getName();
	}
	private Point3D[] merge(Point3D[] array01 , Point3D[] array02){
		int array1Length = array01.length; // 获取array01的长度
		int array2Length = array02.length; // 获取array02的长度
		// 向array01中拷贝元素并为array01扩容
		array01 = Arrays.copyOf(array01, array1Length + array2Length);
		System.arraycopy(array02, 0, array01, array1Length, array2Length);
		return array01;
	}
	public Point3D[] getPointsOf(Face face){
		Point3D[] points = new Point3D[4];
		switch (face){
			case all -> {
				points = new Point3D[24];
				points = merge(points,getPointsOf(Face.east));
				points = merge(points,getPointsOf(Face.west));
				points = merge(points,getPointsOf(Face.down));
				points = merge(points,getPointsOf(Face.up));
				points = merge(points,getPointsOf(Face.north));
				points = merge(points,getPointsOf(Face.south));
			}
			case down -> {
				points[0] = new Point3D(fromX,fromY,fromZ);
				points[1] = new Point3D(fromX+length,fromY,fromZ);
				points[2] = new Point3D(fromX+length,fromY,fromZ+height);
				points[3] = new Point3D(fromX,fromY,fromZ+height);
			}
			case up -> {
				points[0] = new Point3D(fromX,fromY+width,fromZ);
				points[1] = new Point3D(fromX+length,fromY+width,fromZ);
				points[2] = new Point3D(fromX+length,fromY+width,fromZ+height);
				points[3] = new Point3D(fromX,fromY+width,fromZ+height);
			}
			case north -> {
				points[0] = new Point3D(fromX,fromY,fromZ);
				points[1] = new Point3D(fromX,fromY+width,fromZ);
				points[2] = new Point3D(fromX,fromY+width,fromZ+height);
				points[3] = new Point3D(fromX,fromY,fromZ+height);
			}
			case south -> {
				points[0] = new Point3D(fromX+length,fromY,fromZ);
				points[1] = new Point3D(fromX+length,fromY+width,fromZ);
				points[2] = new Point3D(fromX+length,fromY+width,fromZ+height);
				points[3] = new Point3D(fromX+length,fromY,fromZ+height);
			}
			case west -> {
				points[0] = new Point3D(fromX,fromY,fromZ);
				points[1] = new Point3D(fromX,fromY,fromZ+height);
				points[2] = new Point3D(fromX+length,fromY,fromZ+height);
				points[3] = new Point3D(fromX+length,fromY,fromZ);
			}
			case east -> {
				points[0] = new Point3D(fromX,fromY+width,fromZ);
				points[1] = new Point3D(fromX,fromY+width,fromZ+height);
				points[2] = new Point3D(fromX+length,fromY+width,fromZ+height);
				points[3] = new Point3D(fromX+length,fromY+width,fromZ);
			}
		}
		return points;
	}
	
	public void setFrom(double x,double y,double z){
		this.fromX = x;
		this.fromY = y;
		this.fromZ = z;
	}
	
	public void setTo(double x,double y,double z){
		this.height=z-this.fromZ;
		this.width=y-this.fromY;
		this.length=x-this.fromX;
	}
	
	public void setFaceTexture(Face face , File textures) {
		if (face == Face.all) {
			for (Face f : Face.values()) {
				if (f != Face.all)
					face2TextureMap.put(f , textures);
			}
		} else {
			face2TextureMap.put(face , textures);
		}
	}
	
	@Override
	public String toString() {
		return "cube:" + name + "[x:" + fromX + ";y:" + fromY + ";z:" + fromZ + ";length:" + length + ";width:" + width + ";height:" + height + "]";
	}
	
	public void setParent(File packPath , String parent) {
		this.parent = new File(packPath + "\\minecraft\\models\\" + parent + ".json");
	}
	
	public enum Face{
		up,down,north,south,east,west,all
	}
}
