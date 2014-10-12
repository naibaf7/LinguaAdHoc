package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.smartapes.linguaadhoc.R;

public class MainActivity extends Activity {

	public final static int REQUEST_CODE_VOICE = 5;

	private Button buttonLearningActivity;
	private ToggleButton buttonLearningService;
	private Button buttonVoiceContext;
	private Button buttonSelectInterests;

	private MultiSelectorDialog multiSelectorDialog;

	private DBAccessHelper dbah;

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

		dbah = new DBAccessHelper(this, "en_de.sqlite");
		dbah.createDB();
		dbah.openDB();

		preconfigureInterests();

		buttonLearningActivity = (Button) findViewById(R.id.button_learning_activity);
		buttonLearningService = (ToggleButton) findViewById(R.id.button_learning_service);
		buttonVoiceContext = (Button) findViewById(R.id.button_voice_context);
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

		buttonVoiceContext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startVoiceRecognitiony();
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

	private void startVoiceRecognitiony() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition!");
		startActivityForResult(intent, REQUEST_CODE_VOICE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_VOICE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (matches.size() > 0) {
				final WordClassifications wcs = new DBQueryHelper(dbah)
						.getClassificationFromToken(matches.get(0));
				if (wcs.getClasses().length > 0) {
					ArrayList selected = new ArrayList();
					for (int i = 0; i < wcs.getClasses().length; i++) {
						selected.add(i);
					}
					final MultiSelectorDialog msd = new MultiSelectorDialog(
							getString(R.string.speech_recog) + ": '"
									+ matches.get(0) + "'", wcs.getClassesHR(),
							MainActivity.this, selected);
					msd.getDialogBuilder().setNegativeButton(
							getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					msd.getDialogBuilder().setPositiveButton(
							getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											getApplicationContext(),
											LearningActivity.class);

									ArrayList selected = msd.getSelectedItems();
									ArrayList<String> contexts = new ArrayList<String>();

									for (Object o : selected) {
										contexts.add(wcs.getClasses()[(Integer) o]);
									}

									intent.putExtra("DirectContext",
											contexts.toArray(new String[] {}));
									startActivity(intent);
								}
							});
					AlertDialog ad = msd.getDialogBuilder().create();
					ad.show();
				} else {
					Toast.makeText(this, getString(R.string.not_found)
							+ ": '" + matches.get(0) + "'", 5000).show();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
