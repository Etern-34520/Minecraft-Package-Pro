package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings {
	public static void save() {
		try {
			File settings=new File("src/settings.txt");
			//String name=settings.getName();
			//name.substring(0,8);
			//settings.renameTo(new File(settings.getParent()+"\\"+name));
			//FileReader b = new FileReader(settings);
			FileInputStream input=new FileInputStream(settings);
			InputStreamReader read = new InputStreamReader(input, "");
			BufferedReader reader = new BufferedReader(read);
			String lineTxt;
			while ((lineTxt = reader.readLine()) != null)
            {
				System.out.println(lineTxt);
            }
			read.close();
			reader.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void get() {
		
	}
	public static void main(String[] args) {
		save();
	}
}
