package io;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModelReader {
	List<Cube> cubes = new ArrayList<Cube>();
	static int t = 0;
	static List<Double> position = new ArrayList<Double>();

	public ModelReader() {
		// TODO �Զ����ɵĹ��캯�����

	}

	public Cube Read(String path) throws IOException {

		String jsonInformation = null;

		FileInputStream fin = new FileInputStream(path);
		FileInputStream fin1 = new FileInputStream(path);
		InputStreamReader reader = new InputStreamReader(fin);
		InputStreamReader reader1 = new InputStreamReader(fin1);
		// BufferedReader buffReader = new BufferedReader(reader);

		Cube cube = handleJsonObject(new JsonReader(reader));

		BufferedReader buffReader = new BufferedReader(reader1);
		String strTmp = "";
		while ((strTmp = buffReader.readLine()) != null) {
			jsonInformation = jsonInformation + strTmp;
		}

		buffReader.close();
		reader.close();
		reader1.close();
		fin.close();
		fin1.close();
		cube.setFrom(position.get(0), position.get(1), position.get(2));
		cube.setTo(position.get(3), position.get(4), position.get(5));
		return cube;

	}

	private static Cube handleJsonObject(JsonReader reader) throws IOException {
		reader.beginObject();
		String name = null;
		Cube cube = new Cube();
		int a = 0;
		// ln(readFrom+";"+from);
		while (reader.hasNext()) {
			// Cube cube=new Cube();
			try {
				Thread.sleep(t);
			} catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			JsonToken token = reader.peek();
			// ln(token);
			a++;
			// ln(token);
			if (token.equals(JsonToken.BEGIN_ARRAY)) {
				// ("Marks [ ");
				handleJsonArray(reader, cube);
				// token = reader.peek();
				// ("]");
			} else if (token.equals(JsonToken.NAME)) {
				name = reader.nextName();
				eqaqlsName(name);
			} else if (token.equals(JsonToken.STRING)) {
				// get the current token
				String information = reader.nextString();
				// token = reader.peek();
			} else if (token.equals(JsonToken.NUMBER)) {
				// ln(readFrom);
				// return;
			} else if (token.equals(JsonToken.BEGIN_OBJECT)) {
				handleJsonObject(reader);
			} else {
				break;
			}
		}
		return cube;
	}

	private static void handleJsonArray(JsonReader reader, Cube cube) throws IOException {
		reader.beginArray();
		while (true) {
			JsonToken token = reader.peek();
			try {
				Thread.sleep(t);
			} catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			// ln(token);
			if (token.equals(JsonToken.END_ARRAY)) {
				reader.endArray();
				break;
			} else if (token.equals(JsonToken.BEGIN_OBJECT)) {
				handleJsonObject(reader);
			} else if (token.equals(JsonToken.END_OBJECT)) {
				reader.endObject();
			} else if (token.equals(JsonToken.NUMBER)) {
				position.add(reader.nextDouble());
				// (reader.nextInt() + " ");
				// return;
			} else if (token.equals(JsonToken.NAME)) {
				String name = reader.nextName();
				eqaqlsName(name);
			} else {
				break;
			}
		}

	}
	// �������Ķ���https://www.yiibai.com/gson/gson_streaming.html

	private static void eqaqlsName(String name) {
		Cube cube = new Cube();
		switch (name) {
		case "elements":
		case "from":
		case "to":
		case "faces":
		case "down":
		case "up":
		case "north":
		case "south":
		case "west":
		case "east":
		}
	}

}
