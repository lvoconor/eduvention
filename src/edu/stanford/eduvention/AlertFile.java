package edu.stanford.eduvention;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;

import edu.stanford.eduvention.views.Alert;
import parser.SourceCodeAnalytics;
import stanford.exception.ErrorException;
import stanford.java.parser.SourceCodeParser;
public class AlertFile {
	public IFile file;
	public String name;
	public String contents;
	public Integer lines;
	public ArrayList<Alert> alerts;
	public boolean isValid;

	public AlertFile(IFile file, String name, String contents) {
		alerts = new ArrayList<Alert>();
		this.file = file;
		this.name = name;
		this.contents = contents;
		this.lines = getNumLines(contents);
		this.isValid = checkValidity();
	}
	public String toString() {
		return String.format("{name: %s, lines: %d, alerts: %s}", name, lines, alerts.toString());
	}
	public boolean checkValidity(){
		byte[] bytes = contents.getBytes();
        InputStream inStream =  new ByteArrayInputStream(bytes);
        try{
            SourceCodeParser s = new SourceCodeParser(inStream);
        }
        catch(ErrorException e){
        	return false;
        }
        return true;
	}
	private int getNumLines(String code) {
		Matcher m = Pattern.compile("(\n)|(\r)|(\r\n)").matcher(code);
		Integer lines = 1;
		while (m.find()) {
		    lines ++;
		}
		return lines;
	}
}