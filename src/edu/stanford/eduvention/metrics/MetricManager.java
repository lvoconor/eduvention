package edu.stanford.eduvention.metrics;

import java.util.ArrayList;
import java.util.Random;

public class MetricManager {
	public Object[] get() {
		ArrayList<String> alerts = new ArrayList<String>();
		
		/* Alert 1: check for 'a' */
		IMetric simpleEngine = new SimpleEngine();
		Random r = new Random();
		String warning = simpleEngine.getAlert("" + (char)(r.nextInt(2) + 'a'));
		if (warning != null) {
			alerts.add(warning);
		}

		/* Add note if there are no alerts */
		if (alerts.size() == 0) {
			alerts.add("No alerts!");
		}
		
		/* Convert ArrayList to array and return. */
		String[] ret = new String[alerts.size()];
		alerts.toArray(ret);
		return ret;
	}
}
