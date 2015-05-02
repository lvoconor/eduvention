package edu.stanford.eduvention;

import net.sf.json.JSONObject;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class DataManager {

	private IEclipsePreferences prefs;
	private String name;
	private String sunet;
	HttpClient httpClient;
	
	public DataManager() {
		prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");
		httpClient = HttpClientBuilder.create().build();
	}
	
	public void main() {
		loadSettings();
	    try {
	        HttpPost request = new HttpPost("http://eduvention-website.herokuapp.com/snapshots/create");
	        
	        String jsonString = "request="
	     		+ "{\"assignment_id\":\"6\", "
	     		+ "\"student_id\":\""
	     		+ sunet
	     		+ "\","
	     		+ "\"snapshot\": \"class Karel\","
	     		+ "\"alert\": \"1\","
	     		+ "\"datetime\": \"01/01/2015\"}";
	     
	        StringEntity params = new StringEntity(jsonString);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
	        System.out.println(response.getStatusLine().toString());
	        // handle response here...
	    }catch (Exception ex) {
	        // handle exception here
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
	
	private void loadSettings() {
		  try {
			prefs.sync();
		  } catch (BackingStoreException e) {
		  }
		  this.sunet = prefs.get("sunet", "unknown");
		  this.name= prefs.get("name", "unknown");
	}

}
