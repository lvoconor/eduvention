package edu.stanford.eduvention.metrics;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import edu.stanford.eduvention.AlertFile;
import parser.SourceCodeAnalytics;

public class CommentMetric implements IMetric {

	private static final Double COMMENT_FRAC = .10;
	
	@Override
	public String getAlert(AlertFile aFile) {
		int numComments = getNumComments(aFile.contents);
		if (numComments > -1 && numComments < COMMENT_FRAC * aFile.lines) {
			return "Less than " + (COMMENT_FRAC * 100) + "% of lines in this file are comments.";
		} else {
			return null;
		}
	}

	private static int getNumComments(String code) {
        byte[] bytes = code.getBytes();
        InputStream inStream =  new ByteArrayInputStream(bytes);
        try {
        	SourceCodeAnalytics sourceCodeAnalytics = new SourceCodeAnalytics(inStream);
            return sourceCodeAnalytics.getNumberOfCommentLines();
        }
        catch (Exception e) {
        	return -1;
        }
	}
}