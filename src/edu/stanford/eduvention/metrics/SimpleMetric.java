package edu.stanford.eduvention.metrics;

import java.util.ArrayList;

import edu.stanford.eduvention.AlertFile;
import edu.stanford.eduvention.views.Alert;

public class SimpleMetric implements IMetric {

	@Override
	public ArrayList<Alert> getAlerts(AlertFile file) {
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		String content = getContentString(file);
		if(content == null){
			return null;
		}
		Alert a = new Alert("simple", file.name, content);
		alerts.add(a);
		return alerts;
	}
	private String getContentString(AlertFile file){
		if (file.contents.toLowerCase().contains("e"))
			return "Code contains the letter 'e'";
		else
			return null;
	}

}
