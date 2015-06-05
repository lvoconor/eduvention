package edu.stanford.eduvention;

public class Question {

	private String role;
	private String message;
	private String filename;
	private int lineNumber;
	private String timestamp;
	
	public Question(String role, String message, String filename, int lineNumber, String timestamp) {
		super();
		this.role = role;
		this.message = message;
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.timestamp = timestamp;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}	
	@Override
	public String toString() {
		return role + ": " + message;
	}	
}
