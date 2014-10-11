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

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" --> [");
		for (int i = 0; i < classificators.length - 1; i++) {
			sb.append(classificators[i]);
			sb.append(", ");
		}
		if (classificators.length > 0) {
			sb.append(classificators[classificators.length - 1]);
		}
		sb.append("]");
		return sb.toString();
	}
}
