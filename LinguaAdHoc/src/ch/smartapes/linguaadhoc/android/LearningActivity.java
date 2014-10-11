package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning);

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

		Location loc = locc.getLoc();

		POIFetcherTask task = new POIFetcherTask();

		task.addListener(this);
		task.execute(new String[] { String.valueOf(loc.getLatitude()),
				String.valueOf(loc.getLongitude()), "100" });

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

		DBQueryHelper dbqh = new DBQueryHelper(dbah);

		WordClassifications wordClassifications = dbqh
				.getClassifications(contexts.toArray(new String[] {}));

		currentPos = -1;
		wordPairs = dbqh.getWordPairs(contexts.toArray(new String[] {}), 30);
		textTitle.setText(getString(R.string.topics) + ": "
				+ wordClassifications.toStringHR());
		if (wordPairs.size() > 0) {
			advance();
		}
		progressDialog.cancel();

	}

}
