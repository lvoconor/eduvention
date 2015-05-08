package edu.stanford.eduvention.metrics;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import edu.stanford.eduvention.AlertFile;
import edu.stanford.eduvention.views.Alert;
import parser.SourceCodeAnalytics;
import stanford.exception.ErrorException;

public class DecompMetric implements IMetric {

	private static final Integer MAX_AVG_SIZE = 20;
	
	
	@Override
	public Alert getAlert(AlertFile aFile) {
		String content = getContentString(aFile);
		if(content == null){
			return null;
		}
		Alert a = new Alert("decomposition", aFile.name, content);
		return a;	
	}
	
	private String getContentString(AlertFile aFile) {
		double avgSize = getAverageMethodLength(aFile.contents);
		if ((avgSize > -1) && (avgSize > MAX_AVG_SIZE)) {
			return "Your average method size is " + avgSize + " lines. Consider decomposing your functions.";
		} else {
			return null;
		}
	}
	
	private static double getAverageMethodLength(String code){
		SourceCodeAnalytics sourceCodeAnalytics = null;
        byte[] bytes = code.getBytes();
        InputStream inStream =  new ByteArrayInputStream(bytes);
        try {
            sourceCodeAnalytics = new SourceCodeAnalytics(inStream);   
        }
        catch (ErrorException error) {
        	return -2;
        }
        List<Integer> methodLengths = sourceCodeAnalytics.getMethodLengths();
        List<String> methodNames = sourceCodeAnalytics.getMethodNames();
        
        //Iterate over method lengths and add to total
        int totalMethodLines = 0;
        for (Integer methodLength : methodLengths) {
            totalMethodLines += methodLength;
        }
        double avgMethodLength = (double)totalMethodLines/methodNames.size();
        return avgMethodLength;
	}
}
