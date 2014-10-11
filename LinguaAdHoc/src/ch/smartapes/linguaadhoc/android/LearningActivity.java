package ch.smartapes.linguaadhoc.android;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ch.smartapes.linguaadhoc.R;

public class LearningActivity extends Activity {

	private String language1 = "en";
	private String language2 = "de";

	private float pitch1 = 1.0f;
	private float speed1 = 1.0f;

	private float pitch2 = 1.0f;
	private float speed2 = 1.0f;

	private Button buttonNotLearned;
	private Button buttonFavourite;
	private Button buttonFlip;
	private Button buttonSpeak;
	private Button buttonLearned;

	private TextView textCount;
	private TextView textTitle;
	private TextView textText;

	private boolean flipSide = false;

	private TTSSynth tts1;
	private TTSSynth tts2;

	private int currentPos;
	private WordPair current;
	private WordClassifications wordClassifications;
	private List<WordPair> wordPairs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning);

		tts1 = new TTSSynth(this, speed1, pitch1, new Locale(language1));
		tts2 = new TTSSynth(this, speed2, pitch2, new Locale(language2));

		textTitle = (TextView) findViewById(R.id.learning_title);
		textText = (TextView) findViewById(R.id.learning_text);
		textCount = (TextView) findViewById(R.id.learning_count);

		buttonNotLearned = (Button) findViewById(R.id.button_not_learned);
		buttonFavourite = (Button) findViewById(R.id.button_favourite);
		buttonFlip = (Button) findViewById(R.id.button_flip);
		buttonSpeak = (Button) findViewById(R.id.button_speak);
		buttonLearned = (Button) findViewById(R.id.button_learned);

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

		newWordPairs();
		advance();
	}

	private void advance() {
		flipSide = false;
		currentPos++;
		if (currentPos >= wordPairs.size()) {
			newWordPairs();
			advance();
		} else {
			current = wordPairs.get(currentPos);
			textText.setText(current.getLanguage1());
			textCount.setText(String.valueOf(currentPos + 1) + "/"
					+ String.valueOf(wordPairs.size()));
		}
	}

	private void newWordPairs() {
		currentPos = -1;
		wordPairs = new LinkedList<WordPair>();
		wordPairs.add(new WordPair("Hello", "Hallo"));
		wordPairs.add(new WordPair("Church", "Kirche"));
		wordClassifications = new WordClassifications(new String[] { "testing",
				"fun" }, new String[] { "Testing", "Fun" });
		textTitle.setText(getString(R.string.topics) + ": "
				+ wordClassifications.toStringHR());
	}

}
