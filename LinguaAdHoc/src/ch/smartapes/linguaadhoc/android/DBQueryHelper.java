package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBQueryHelper {

	SQLiteDatabase sqldb;

	public DBQueryHelper(DBAccessHelper dah) {
		sqldb = dah.getDatabase();
	}

	public List<WordPair> getWordPairs(String[] strings, int count) {
		StringBuilder compareSB = new StringBuilder();
		for (int i = 0; i < strings.length - 1; i++) {
			compareSB.append("tag == ");
			compareSB.append("'" + strings[i] + "'");
			compareSB.append(" OR ");
		}
		if (strings.length > 0) {
			compareSB.append("tag == ");
			compareSB.append("'" + strings[strings.length - 1] + "'");
		}
		List<WordPair> lwp = new ArrayList<WordPair>();
		Cursor cursor = sqldb.rawQuery(
				"SELECT A.language1, A.language2 FROM words A "
						+ "JOIN (SELECT C.idWord FROM belongsto C JOIN "
						+ "(SELECT _id FROM classifications WHERE "
						+ compareSB.toString()
						+ ") D ON C.idClassification == D._id) B "
						+ "ON A._id == B.idWord ORDER BY RANDOM() LIMIT "
						+ String.valueOf(count) + ";", null);
		while (cursor.moveToNext()) {
			String l1 = cursor.getString(1);
			String l2 = cursor.getString(0);
			WordPair wp = new WordPair(l1, l2);
			lwp.add(wp);
		}

		return lwp;
	}

	public WordClassifications getClassifications(String[] classes) {
		String[] classesHR = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			Cursor cursor = sqldb.rawQuery(
					"SELECT name FROM classifications WHERE tag == '"
							+ classes[i] + "' LIMIT 1;", null);
			while (cursor.moveToNext()) {
				classesHR[i] = cursor.getString(0);
			}
		}

		WordClassifications wcs = new WordClassifications(classes, classesHR);
		return wcs;
	}
}
