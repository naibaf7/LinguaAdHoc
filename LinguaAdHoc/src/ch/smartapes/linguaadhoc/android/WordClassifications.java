package ch.smartapes.linguaadhoc.android;

public class WordClassifications {

	private String[] classes;
	private String[] classesHR;

	public WordClassifications(String[] classes, String[] classesHR) {
		this.classes = classes;
		this.classesHR = classesHR;
	}

	public String[] getClasses() {
		return classes;
	}

	public String[] getClassesHR() {
		return classesHR;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < classes.length - 1; i++) {
			sb.append("(");
			sb.append(classes[i]);
			sb.append(", ");
			sb.append(classesHR[i]);
			sb.append(")");
			sb.append(", ");
		}
		if (classes.length > 0) {
			sb.append("(");
			sb.append(classes[classes.length - 1]);
			sb.append(", ");
			sb.append(classesHR[classes.length - 1]);
			sb.append(")");
		}
		sb.append("]");
		return sb.toString();
	}

	public String toStringHR() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < classes.length - 1; i++) {
			sb.append(classesHR[i]);
			sb.append(", ");
		}
		if (classes.length > 0) {
			sb.append(classesHR[classes.length - 1]);
		}
		return sb.toString();
	}

}
