package edu.stanford.eduvention.metrics;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import parser.Method;
import stanford.exception.ErrorException;
import stanford.java.parser.SourceCodeParser;
import edu.stanford.eduvention.Alert;
import edu.stanford.eduvention.AlertFile;

public class DecompMetric implements IMetric  {
	public static final Integer MAX_METHOD_LENGTH = 20;
	@Override
	public ArrayList<Alert> getAlerts(AlertFile aFile) {
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		SourceCodeParser s = null;
		byte[] bytes = null;
		try {
			bytes = aFile.contents.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream is = new ByteArrayInputStream(bytes);
		try {
			s = new SourceCodeParser(is);
		} catch (ErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Method> methods = (ArrayList<Method>) s.getMethods();
		for( Method m : methods){
			String alertContent = getAlertContentString(m);
			if(alertContent == null){
				return null;
			}
			Alert a = new Alert("decomposition-method", aFile.name, alertContent, m.getBeginLine());
			alerts.add(a);
		}
		return alerts;
	}
	
	private String getAlertContentString(Method m){
		if(m.getNumOfBodyLines() > MAX_METHOD_LENGTH){
			return "Method '"+ m.getName() + "' is " + m.getNumOfBodyLines() + " lines. Consider decomposing.";
		}
		else{
			return null;
		}
	}

	

}
