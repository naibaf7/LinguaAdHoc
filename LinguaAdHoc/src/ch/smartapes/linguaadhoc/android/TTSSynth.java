package ch.smartapes.linguaadhoc.android;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class TTSSynth implements OnInitListener {

	private TextToSpeech tts;
	private Context context;
	private boolean init;

	private List<String> texts;

	TTSSynth(Context context, float speechRate, float pitch, Locale loc) {
		this.context = context;
		tts = new TextToSpeech(context, this);
		tts.setSpeechRate(speechRate);
		tts.setLanguage(loc);
		tts.setPitch(pitch);
		texts = new LinkedList<String>();
	}

	public boolean getQueueStatus()
	{
		return texts.isEmpty();
	}
	
	public boolean speak(String text) {
		boolean queueStatus = false;
		if (texts.size() > 0) {
			queueStatus = true;
		}
		if (text != null) {
			texts.add(text);
		}
		if (init) {
			for (String t : texts) {
				tts.speak(t, TextToSpeech.QUEUE_ADD, null);
			}
			texts = new LinkedList<String>();
		}
		return queueStatus;
	}

	@Override
	public void onInit(int status) {
		init = true;
		speak(null);
	}

}
