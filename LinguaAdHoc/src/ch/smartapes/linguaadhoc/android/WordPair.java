package ch.smartapes.linguaadhoc.android;

public class WordPair {

	private String language1;
	private String language2;
	private String topic;

	WordPair(String language1, String language2, String topic) {
		this.language1 = language1;
		this.language2 = language2;
		this.topic = topic;
	}

	public String toString() {
		return topic + ": " + language1 + " <--> " + language2;
	}

	public String getLanguage1() {
		return language1;
	}

	public String getLanguage2() {
		return language2;
	}
	
	public String getTopic() {
		return topic;
	}

}
