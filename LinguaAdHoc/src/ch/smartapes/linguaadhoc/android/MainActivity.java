package ch.smartapes.linguaadhoc.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import ch.smartapes.linguaadhoc.R;

public class MainActivity extends Activity {

	private Button buttonLearningActivity;
	private ToggleButton buttonLearningService;
	private Button buttonPictureContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Location loc = getLoc();

		/*
		 * new POIFetcherTask().execute(new String[] {
		 * String.valueOf(loc.getLatitude()),
		 * String.valueOf(loc.getLongitude()), "1000",
		 * "food|bar|store|museum|art_gallery" });
		 */

		buttonLearningActivity = (Button) findViewById(R.id.button_learning_activity);
		buttonLearningService = (ToggleButton) findViewById(R.id.button_learning_service);
		buttonPictureContext = (Button) findViewById(R.id.button_picture_context);

		buttonLearningActivity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						LearningActivity.class);
				startActivity(intent);
			}
		});

		buttonLearningService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (buttonLearningService.isChecked()) {
					Intent serviceIntent = new Intent();
					serviceIntent
							.setAction("ch.smartapes.linguaadhoc.android.LearningService");
					startService(serviceIntent);
				} else {
					Intent serviceIntent = new Intent();
					serviceIntent
							.setAction("ch.smartapes.linguaadhoc.android.LearningService");
					stopService(serviceIntent);
				}
			}
		});
		
		buttonPictureContext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				//intent.putExtra("SCAN_MODE", "");
				startActivityForResult(intent, 0);
			}
		});

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
