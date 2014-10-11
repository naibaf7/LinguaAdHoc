package ch.smartapes.linguaadhoc.android;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.smartapes.linguaadhoc.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Location loc = getLoc();

		/*new POIFetcherTask().execute(new String[] {
				String.valueOf(loc.getLatitude()),
				String.valueOf(loc.getLongitude()), "1000",
				"food|bar|store|museum|art_gallery" });*/
		
		//TTSSynth ttsDe = new TTSSynth(this, 0.8f, 1.0f, new Locale("de"));
		//ttsDe.speak("Monozytenzytotoxizität!");
		//TTSSynth ttsEn = new TTSSynth(this, 0.8f, 1.0f, new Locale("en"));
		//ttsEn.speak("Hell !");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
