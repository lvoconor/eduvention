package edu.stanford.eduvention.metrics;

import edu.stanford.eduvention.AlertFile;

public class SimpleMetric implements IMetric {

	@Override
	public String getAlert(AlertFile code) {
		if (code.contents.toLowerCase().contains("e"))
			return "Code contains the letter 'e'";
		else
			return null;
	}

}
