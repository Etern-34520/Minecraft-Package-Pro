package indi.etern.minecraftpackagepro.io;

import java.util.List;

public class Cube {
	double x;
	double y;
	double z;
	double length;
	double width;
	double height;
	boolean fromAlready;
	public void setFrom(double x,double y,double z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public void setTo(double x,double y,double z){
		this.height=z-this.z;
		this.width=y-this.y;
		this.length=x-this.x;
	}
	public void setFaces(List<String> textures) {
		
	}
	@Override
	public String toString() {
		return "cube[x:" + x + ";y:" + y + ";z:" + z + ";length:" + length + ";width:" + width + ";height:" + height;
	}
}
