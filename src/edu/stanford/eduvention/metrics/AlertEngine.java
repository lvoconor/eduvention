package edu.stanford.eduvention.metrics;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import parser.SourceCodeAnalytics;
import stanford.exception.ErrorException;

/*
 * AlertEngine class contains methods for computing metrics used for determining whether to display an alert
 */
public class AlertEngine {
	
	public static void main(String[] args) {
        int numComments = AlertEngine.getNumComments(SOURCE_CODE_EXAMPLE);
        double avgMethodLength = AlertEngine.getAverageMethodLength(SOURCE_CODE_EXAMPLE);
        System.out.println("numcomments: " + numComments);
        System.out.println("AVG METHOD LENGTH: " + avgMethodLength);

        
    }
	private static String SOURCE_CODE_EXAMPLE = 
		      "public class HelloWorld\n"
		    + "{\n"
		    + "    /**\n" 
		    + "     * Javadoc comment line 1\n" 
		    + "     * Javadoc comment line 2\n" 
		    + "     */\n" 
		    + "    public static void main(String[] args) {\n" 
		    + "        // Inline comment\n" 
		    + "        System.out.println(\"Hello world\");\n" 
		    + "        System.out.println(\"Hello world\");\n" 
		    + "    }\n" 
		    + "}";
	
	
	//Counts the number of lines of comments in a String of Java code 
	public static int getNumComments(String code){
		SourceCodeAnalytics sourceCodeAnalytics = null;
        byte[] bytes = code.getBytes();
        InputStream inStream =  new ByteArrayInputStream(bytes);;
        try {
            sourceCodeAnalytics = new SourceCodeAnalytics(inStream);
       
        }
        catch (ErrorException error) {
            System.out.println(error.getMessage());
        }
        return sourceCodeAnalytics.getNumberOfCommentLines();
	} 	
	
	//Counts the average number of lines inside of each Java method 
	public static double getAverageMethodLength(String code){
		SourceCodeAnalytics sourceCodeAnalytics = null;
        byte[] bytes = code.getBytes();
        InputStream inStream =  new ByteArrayInputStream(bytes);
        try {
            sourceCodeAnalytics = new SourceCodeAnalytics(inStream);   
        }
        catch (ErrorException error) {
            System.out.println(error.getMessage());
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
