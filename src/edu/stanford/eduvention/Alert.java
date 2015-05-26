package edu.stanford.eduvention;

public class Alert {
	public String type;
	public String fileName;
	public int lineNumber;
	public String content;

	public Alert(String type, String fileName, String content, int lineNumber){
		this.type = type;
		this.content = content;
		this.fileName = fileName;
		this.lineNumber = lineNumber; 
	}

	public Alert(String type, String fileName, String content) {
		this.type = type;
		this.content = content;
		this.fileName = fileName;
		this.lineNumber = -1; 
	}

	public String getWarning() {
		return content;
	}

	public String toString() {
		return String.format("{type: %s, fileName: %s, lineNumber: %d, content: %s}", type, fileName, lineNumber, content);
	}
}