package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import ch.smartapes.linguaadhoc.R;
import ch.smartapes.linguaadhoc.android.POIFetcherTask.POIFetchListener;

public class LearningService extends Service implements POIFetchListener {

	private LocationContext locc;

	private float pitch1 = 1.0f;
	private float speed1 = 1.0f;

	private float pitch2 = 1.0f;
	private float speed2 = 1.0f;

	private String language1 = "en";
	private String language2 = "de";

	private TTSSynth tts1;
	private TTSSynth tts2;

	private Timer timer1;
	private Timer timer2;

	private int currentPos;
	private WordPair current;
	private List<WordPair> wordPairs;

	private DBAccessHelper dbah;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences spf = getSharedPreferences("ServicePrefs", 0);
		Editor edit = spf.edit();
		edit.putBoolean("ServiceEnabled", true);
		edit.commit();

		locc = new LocationContext(this);

		dbah = new DBAccessHelper(this, "en_de.sqlite");
		dbah.createDB();
		dbah.openDB();

		tts1 = new TTSSynth(this, speed1, pitch1, new Locale(language1));
		tts2 = new TTSSynth(this, speed2, pitch2, new Locale(language2));

		newWordPairs();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			timer1.cancel();
			timer2.cancel();
			cancelNotification();
		} catch (Exception e) {
		}
		SharedPreferences spf = getSharedPreferences("ServicePrefs", 0);
		Editor edit = spf.edit();
		edit.putBoolean("ServiceEnabled", false);
		edit.commit();
	}

	private void learnBackground() {
		timer1 = new Timer();
		timer2 = new Timer();
		timer1.schedule(new TaskFront(), 0);
	}

	private void newWordPairs() {
		Location loc = locc.getLoc();

		POIFetcherTask task = new POIFetcherTask();

		task.addListener(this);
		task.execute(new String[] { String.valueOf(loc.getLatitude()),
				String.valueOf(loc.getLongitude()), "100" });
	}

	@Override
	public void poisReady(List<WordCriteria> wcl) {
		currentPos = -1;
		List<String> contexts = new ArrayList<String>();
		for (WordCriteria wc : wcl) {
			for (int i = 0; i < wc.getClassificators().length; i++) {
				String cont = wc.getClassificators()[i];
				if (!contexts.contains(cont)) {
					contexts.add(cont);
				}
			}
		}

		DBQueryHelper dbqh = new DBQueryHelper(dbah);
		wordPairs = dbqh.getWordPairs(contexts.toArray(new String[] {}), 30);

		if (wordPairs.size() > 0) {
			learnBackground();
		}
	}

	private void pushNotification(String text) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification.Builder notificationBuilder = new Notification.Builder(
				this);
		notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
		notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(
				getResources(), R.drawable.ic_launcher));
		notificationBuilder.setAutoCancel(false);
		notificationBuilder.setOngoing(true);
		notificationBuilder.setContentTitle(getResources().getString(
				R.string.app_name));
		notificationBuilder.setContentText(text);
		notificationBuilder.setWhen(System.currentTimeMillis());
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.setContentIntent(pendingIntent);
		notificationManager.notify(0, notificationBuilder.build());
	}

	private void cancelNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);

	}

	class TaskFront extends TimerTask {

		@Override
		public void run() {
			currentPos++;
			if (currentPos < wordPairs.size()) {
				current = wordPairs.get(currentPos);
				pushNotification(current.getLanguage1());
				tts1.speak(current.getLanguage1());
				timer2.schedule(new TaskBack(), 4000);
			} else {
				newWordPairs();
			}
		}

	}

	class TaskBack extends TimerTask {

		@Override
		public void run() {
			pushNotification(current.getLanguage2());
			tts2.speak(current.getLanguage2());
			timer1.schedule(new TaskFront(), 8000);
		}

	}

}
