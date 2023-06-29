package indi.etern.minecraftpackagepro.io.indexScanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Cube {
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
