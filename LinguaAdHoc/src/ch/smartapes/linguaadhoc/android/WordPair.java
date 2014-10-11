package ch.smartapes.linguaadhoc.android;

public class WordPair {

	private String language1;
	private String language2;

	WordPair(String language1, String language2) {
		this.language1 = language1;
		this.language2 = language2;
	}

	public String toString() {
		return language1 + " <--> " + language2;
	}

	public String getLanguage1() {
		return language1;
	}

	public String getLanguage2() {
		return language2;
	}

}
