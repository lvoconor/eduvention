package edu.stanford.eduvention.metrics;

import edu.stanford.eduvention.AlertFile;
import edu.stanford.eduvention.views.Alert;

public class SimpleMetric implements IMetric {

	@Override
	public Alert getAlert(AlertFile file) {
		String content = getContentString(file);
		if(content == null){
			return null;
		}
		Alert a = new Alert("simple", file.name, content);
		return a;
	}
	private String getContentString(AlertFile file){
		if (file.contents.toLowerCase().contains("e"))
			return "Code contains the letter 'e'";
		else
			return null;
	}

}
