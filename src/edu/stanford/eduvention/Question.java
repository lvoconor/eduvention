package edu.stanford.eduvention;

public class Question {

	private String role;
	private String message;
	private String filename;
	private int lineNumber;
	
	public Question(String role, String message, String filename, int lineNumber) {
		super();
		this.role = role;
		this.message = message;
		this.filename = filename;
		this.lineNumber = lineNumber;
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
	@Override
	public String toString() {
		return role + ": " + message;
	}	
}
