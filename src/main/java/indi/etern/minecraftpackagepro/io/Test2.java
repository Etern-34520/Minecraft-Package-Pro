package indi.etern.minecraftpackagepro.io;

import java.io.IOException;

public class Test2{
	public static void main(String[] args) throws IOException {
		ModelReader reader=new ModelReader();
		Cube cube=reader.Read("U:\\cube.json");
		System.out.println(cube.toString());
	}
}