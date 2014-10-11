package ch.smartapes.linguaadhoc.android;

import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAccessHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/ch.smartapes.linguaadhoc.android/databases/";

	private SQLiteDatabase sqldb;

	private final Context context;

	public DBAccessHelper(Context context, String name) {
		super(context, name, null, 1);
		this.context = context;
		openDB();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public void createDB() {
		boolean exists = checkExists();
		if (exists) {

		} else {
			this.getReadableDatabase();
			try {
				copyDB();
			} catch (IOException e) {

			}
		}
	}

	public synchronized void copyDB() {

	}

	public synchronized void openDB() {
		sqldb = 
	}

	public synchronized boolean checkDB() {
		SQLiteDatabase sqrdb = null;
		try {
			sqrdb = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			return false;
		}
		if(sqrdb != null)
		{
			sqrdb.close();
		}
		return sqrdb != null;
	}

	public synchronized void closeDB() {
		if (sqldb != null && sqldb.isOpen()) {
			sqldb.close();
			sqldb = null;
		}
	}
}
