package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

	private MultiSelectorDialog multiSelectorDialog;

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

		preconfigureInterests();

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
				intent.putExtra("SCAN_MODE", "TEXT");
				startActivityForResult(intent, 0);
			}
		});

		buttonSelectInterests.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final ArrayList<String> tags = ClassifierReader
						.readClassifiers(MainActivity.this);
				ArrayList<String> classifiers = ClassifierReader
						.convertTags(tags);

				SharedPreferences spf = getSharedPreferences("InterestPrefs", 0);

				ArrayList selected = new ArrayList();

				int i = 0;
				for (String tag : tags) {
					if (spf.getBoolean(tag, true)) {
						selected.add(i);
					}
					i++;
				}

				multiSelectorDialog = new MultiSelectorDialog(
						getString(R.string.select_interests), classifiers
								.toArray(new String[classifiers.size()]),
						MainActivity.this, selected);

				multiSelectorDialog.getDialogBuilder().setPositiveButton(
						R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								updateInterests(tags);
							}
						});
				multiSelectorDialog.getDialogBuilder().setNegativeButton(
						R.string.none, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								selectNoInterests(tags);
							}
						});
				multiSelectorDialog.getDialogBuilder().setNeutralButton(
						R.string.all, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								selectAllInterest(tags);
							}
						});
				AlertDialog ad = multiSelectorDialog.getDialogBuilder()
						.create();
				ad.show();
			}
		});
	}

	private void selectAllInterest(ArrayList<String> tags) {
		SharedPreferences spf = getSharedPreferences("InterestPrefs", 0);
		Editor edit = spf.edit();
		for (String tag : tags) {
			edit.putBoolean(tag, true);
		}
		edit.commit();
	}

	private void selectNoInterests(ArrayList<String> tags) {
		SharedPreferences spf = getSharedPreferences("InterestPrefs", 0);
		Editor edit = spf.edit();
		for (String tag : tags) {
			edit.putBoolean(tag, false);
		}
		edit.commit();
	}

	private void updateInterests(ArrayList<String> tags) {
		SharedPreferences spf = getSharedPreferences("InterestPrefs", 0);
		Editor edit = spf.edit();
		for (String tag : tags) {
			edit.putBoolean(tag, false);
		}
		for (Object o : multiSelectorDialog.getSelectedItems()) {
			edit.putBoolean(tags.get((Integer) o), true);
		}
		edit.commit();
	}

	private void preconfigureInterests() {
		final ArrayList<String> tags = ClassifierReader
				.readClassifiers(MainActivity.this);

		SharedPreferences spf = getSharedPreferences("InterestPrefs", 0);
		Editor edit = spf.edit();

		for (String tag : tags) {
			if (!spf.contains(tag)) {
				edit.putBoolean(tag, true);
			}
		}
		edit.commit();
	}

}
