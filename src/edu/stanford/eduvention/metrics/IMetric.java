package edu.stanford.eduvention.metrics;

import edu.stanford.eduvention.AlertFile;
import edu.stanford.eduvention.views.Alert;

public interface IMetric {
	Alert getAlert(AlertFile aFile);
}
