package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import ch.smartapes.linguaadhoc.R;
import ch.smartapes.linguaadhoc.android.POIFetcherTask.POIFetchListener;

public class LearningActivity extends Activity implements POIFetchListener {

	private String language1 = "en";
	private String language2 = "de";

	private LocationContext locc;

	private float pitch1 = 1.0f;
	private float speed1 = 1.0f;

	private float pitch2 = 1.0f;
	private float speed2 = 1.0f;

	private ImageButton buttonNotLearned;
	private ImageButton buttonFavourite;
	private ImageButton buttonFlip;
	private ImageButton buttonSpeak;
	private ImageButton buttonLearned;

	private TextView textCount;
	private TextView textTitle;
	private TextView textText;

	private boolean flipSide = false;

	private TTSSynth tts1;
	private TTSSynth tts2;

	private int currentPos;
	private WordPair current;
	private List<WordPair> wordPairs;

	private DBAccessHelper dbah;

	private ProgressDialog progressDialog;

	private ArrayList<String> actualContexts;

	private MultiSelectorDialog multiSelectorDialog;

	private boolean directMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning);

		try {
			Intent intent = getIntent();
			String[] directContext = intent.getExtras().getStringArray(
					"DirectContext");
			if (directContext != null) {
				directMode = true;
				actualContexts = new ArrayList<String>();
				for (int i = 0; i < directContext.length; i++) {
					actualContexts.add(directContext[i]);
				}
			}
		} catch (Exception e) {
		}

		locc = new LocationContext(this);

		dbah = new DBAccessHelper(this, "en_de.sqlite");
		dbah.createDB();
		dbah.openDB();

		tts1 = new TTSSynth(this, speed1, pitch1, new Locale(language1));
		tts2 = new TTSSynth(this, speed2, pitch2, new Locale(language2));

		textTitle = (TextView) findViewById(R.id.learning_title);
		textText = (TextView) findViewById(R.id.learning_text);
		textCount = (TextView) findViewById(R.id.learning_count);

		buttonNotLearned = (ImageButton) findViewById(R.id.button_not_learned);
		buttonFavourite = (ImageButton) findViewById(R.id.button_favourite);
		buttonFlip = (ImageButton) findViewById(R.id.button_flip);
		buttonSpeak = (ImageButton) findViewById(R.id.button_speak);
		buttonLearned = (ImageButton) findViewById(R.id.button_learned);

		buttonFlip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flipSide = !flipSide;
				if (flipSide) {
					textText.setText(current.getLanguage2());
				} else {
					textText.setText(current.getLanguage1());
				}
			}
		});

		buttonSpeak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (current != null) {
					if (flipSide) {
						tts2.speak(current.getLanguage2());
					} else {
						tts1.speak(current.getLanguage1());
					}
				}
			}
		});

		buttonNotLearned.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				advance();
			}
		});

		buttonLearned.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				advance();
			}
		});

		buttonFavourite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		newWordPairs();
	}

	private void advance() {
		flipSide = false;
		currentPos++;
		if (wordPairs == null || currentPos >= wordPairs.size()) {
			newWordPairs();

		} else {
			current = wordPairs.get(currentPos);
			textTitle.setText(getString(R.string.topic) + ": "
					+ current.getTopic());
			textText.setText(current.getLanguage1());
			textCount.setText(String.valueOf(currentPos + 1) + "/"
					+ String.valueOf(wordPairs.size()));
		}
	}

	private void newWordPairs() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getString(R.string.loading));
		progressDialog.setMessage(getResources().getString(R.string.wait_load));
		progressDialog.setCancelable(false);
		progressDialog.show();

		if (!directMode) {
			Location loc = locc.getLoc();

			POIFetcherTask task = new POIFetcherTask();

			SharedPreferences spf = getSharedPreferences("InterestPrefs", 0);
			ArrayList<String> list = ClassifierReader.readClassifiers(this);
			StringBuilder interestBuilder = new StringBuilder();
			for (String listEntry : list) {
				if (spf.getBoolean(listEntry, true)) {
					interestBuilder.append(listEntry + "|");
				}
			}

			String interest = interestBuilder.toString();

			if (interest.length() > 0) {
				interest = interest.substring(0, interest.length() - 1);

				task.addListener(this);
				task.execute(new String[] { String.valueOf(loc.getLatitude()),
						String.valueOf(loc.getLongitude()), "100", interest });
			} else {
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		} else {
			fetchPairs(actualContexts);
			progressDialog.cancel();
		}

	}

	@Override
	public void poisReady(List<WordCriteria> wcl) {
		List<String> contexts = new ArrayList<String>();
		for (WordCriteria wc : wcl) {
			for (int i = 0; i < wc.getClassificators().length; i++) {
				String cont = wc.getClassificators()[i];
				if (!contexts.contains(cont)) {
					contexts.add(cont);
				}
			}
		}

		ArrayList selected = new ArrayList();

		actualContexts = new ArrayList();

		SharedPreferences spf = getSharedPreferences("InterestPrefs", 0);

		int i = 0;
		for (String context : contexts) {
			if (spf.contains(context)) {
				actualContexts.add(context);
				selected.add(i);
				i++;
			}
		}

		multiSelectorDialog = new MultiSelectorDialog(
				getString(R.string.select_interests), ClassifierReader
						.convertTags(actualContexts).toArray(new String[] {}),
				LearningActivity.this, selected);

		multiSelectorDialog.getDialogBuilder().setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						updateInterests();
					}
				});
		multiSelectorDialog.getDialogBuilder().setNegativeButton(
				R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						fetchPairs(actualContexts);
					}
				});
		AlertDialog ad = multiSelectorDialog.getDialogBuilder().create();
		ad.show();

		progressDialog.cancel();

	}

	private void updateInterests() {
		ArrayList selected = multiSelectorDialog.getSelectedItems();
		ArrayList<String> contextsNew = new ArrayList<String>();
		for (Object o : selected) {
			contextsNew.add(actualContexts.get((Integer) o));
		}
		fetchPairs(contextsNew);
	}

	private void fetchPairs(List<String> contexts) {
		try {
			DBQueryHelper dbqh = new DBQueryHelper(dbah);

			currentPos = -1;
			wordPairs = dbqh
					.getWordPairs(contexts.toArray(new String[] {}), 30);
			if (wordPairs.size() > 0) {
				advance();
			}
		} catch (Exception e) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

}
