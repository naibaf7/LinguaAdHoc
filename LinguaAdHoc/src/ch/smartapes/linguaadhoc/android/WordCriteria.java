package ch.smartapes.linguaadhoc.android;

public class WordCriteria {

	private String name;
	private String[] classificators;

	public WordCriteria(String name, String[] classificators) {
		this.name = name;
		this.classificators = classificators;
	}

	public String getName() {
		return name;
	}

	public String[] getClassificators() {
		return classificators;
	}

}
