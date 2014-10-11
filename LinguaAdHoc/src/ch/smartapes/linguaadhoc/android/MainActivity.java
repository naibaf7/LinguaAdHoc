package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import ch.smartapes.linguaadhoc.R;

public class MainActivity extends Activity {

	private Button buttonLearningActivity;
	private ToggleButton buttonLearningService;
	private Button buttonPictureContext;
	private Button buttonSelectInterests;

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences spf = getSharedPreferences("ServicePrefs", 0);
		buttonLearningService.setChecked(spf
				.getBoolean("ServiceEnabled", false));
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonLearningActivity = (Button) findViewById(R.id.button_learning_activity);
		buttonLearningService = (ToggleButton) findViewById(R.id.button_learning_service);
		buttonPictureContext = (Button) findViewById(R.id.button_picture_context);
		buttonSelectInterests = (Button) findViewById(R.id.button_select_interests);

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
					Intent serviceIntent = new Intent(getApplicationContext(),
							LearningService.class);
					startService(serviceIntent);
				} else {
					Intent serviceIntent = new Intent(getApplicationContext(),
							LearningService.class);
					stopService(serviceIntent);
				}
			}
		});

		buttonPictureContext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				// intent.putExtra("SCAN_MODE", "");
				startActivityForResult(intent, 0);
			}
		});

		buttonSelectInterests.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				ArrayList<String> classifiers = ClassifierReader.convertTags(ClassifierReader.readClassifiers(MainActivity.this));
				System.out.println(classifiers.size());
				MultiSelectorDialog msd = new MultiSelectorDialog(getString(R.string.select_interests), classifiers.toArray(new String[classifiers.size()]),
						MainActivity.this);

				msd.getDialogBuilder().setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				msd.getDialogBuilder().setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog ad = msd.getDialogBuilder().create();
				ad.show();
			}
		});

	}

}
