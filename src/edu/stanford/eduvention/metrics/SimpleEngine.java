package edu.stanford.eduvention.metrics;

public class SimpleEngine implements IMetric {

	@Override
	public String getAlert(String code) {
		if (code.toLowerCase().contains("a"))
			return "Code contains the letter 'a'";
		else
			return null;
	}

}
