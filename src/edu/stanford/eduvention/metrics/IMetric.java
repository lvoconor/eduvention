package edu.stanford.eduvention.metrics;

import java.util.ArrayList;

import stanford.exception.ErrorException;
import edu.stanford.eduvention.Alert;
import edu.stanford.eduvention.AlertFile;

public interface IMetric {
	ArrayList<Alert> getAlerts(AlertFile aFile) throws ErrorException;
}
