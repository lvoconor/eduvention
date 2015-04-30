package edu.stanford.eduvention;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
public class DataManager {

	public static void main(String[] args) {
		HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 
	    try {
	        HttpPost request = new HttpPost("http://eduvention-website.herokuapp.com/snapshot/create");
	        
	     String jsonString = "request="
	     		+ "{\"assignment_id\":\"6\", "
	     		+ "\"student_id\":\"5\","
	     		+ "\"snapshot\": \"class Karel\","
	     		+ "\"alert\": \"1\","
	     		+ "\"datetime\": \"01/01/2015\"}";
	     
	        StringEntity params =new StringEntity(jsonString);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
	        System.out.println(response.getStatusLine().toString());
	        // handle response here...
	    }catch (Exception ex) {
	        // handle exception here
	    } finally {
	        httpClient.getConnectionManager().shutdown();
	    }
	}
	public static String generateJSONString(int studentId, int assignmentId, String snapshot, int alert, String datetime){
		JSONObject j = new JSONObject();
		j.put("assignment_id", assignmentId);
		j.put("student_id", studentId);
		j.put("snapshot", snapshot);
		j.put("alert", alert);
		j.put("datetime", datetime);
		return j.toString();

	}
	

}