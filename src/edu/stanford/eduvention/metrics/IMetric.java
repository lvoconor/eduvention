package edu.stanford.eduvention.metrics;

import java.util.ArrayList;

import edu.stanford.eduvention.AlertFile;
import edu.stanford.eduvention.views.Alert;

public interface IMetric {
	ArrayList<Alert> getAlerts(AlertFile aFile);
}
