package ch.smartapes.linguaadhoc.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class ClassifierReader {

	public static ArrayList<String> readClassifiers(Context context) {
		ArrayList<String> classifiers = new ArrayList<String>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					context.getAssets().open("google_classifiers"), "utf8"));

			String line;

			while ((line = reader.readLine()) != null) {
				classifiers.add(line);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return classifiers;
	}
	
	public static ArrayList<String> convertTags(List<String> tags){
		ArrayList<String> classifiers = new ArrayList<String>();
		for(String s : tags){
			String str = s.replace("_", " _");
			String[] names = str.split("_");
			String name = "";
			for (int i = 0; i < names.length; i++) {
				name = name.concat(names[i].substring(0, 1).toUpperCase()
						.concat(names[i].substring(1, names[i].length())));
			}
			classifiers.add(name);
		}
		return classifiers;
	}

}
