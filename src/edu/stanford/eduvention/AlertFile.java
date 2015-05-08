package edu.stanford.eduvention;

import java.util.ArrayList;

import edu.stanford.eduvention.views.Alert;


public class AlertFile {
	public String name;
	public String contents;
	public Integer lines;
	public ArrayList<Alert> alerts;
	public AlertFile(){
		alerts = new ArrayList<Alert>();
	}
}
