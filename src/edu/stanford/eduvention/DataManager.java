package edu.stanford.eduvention;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import edu.stanford.eduvention.views.Alert;

@SuppressWarnings("restriction")
public class DataManager {

	private static IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");
	private static String name;
	private static String sunet;
	private static HttpClient httpClient = HttpClientBuilder.create().build();;

	public static void update() {
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
	     		+ "\"datetime\": \""
	     		+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
	     		+ "\"}";
	     
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
	
	public static void postSnapshot(AlertFile f){
		String snapshotString = "request=" + generateJSONString(f);
		HttpPost request = new HttpPost("http://eduvention-website.herokuapp.com/snapshots/create");
		StringEntity params = null;
		try {
			params = new StringEntity(snapshotString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        request.addHeader("content-type", "application/x-www-form-urlencoded");
        request.setEntity(params);
        HttpResponse response = null;
		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(response.getStatusLine().toString());
	}

	private static String generateJSONString(AlertFile f){
		loadSettings();
		JSONObject j = new JSONObject();
		JSONArray alerts = new JSONArray();
		j.put("assignment_id", 6); //TODO store assignment_id
		j.put("student_id", sunet);
		j.put("snapshot", f.contents);
		j.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		for(Alert alert: f.alerts){
			JSONObject alertObject = new JSONObject();
			alertObject.put("alert_type", alert.type);
			//TODO add line numbers
			alerts.add(alertObject); 
		}
		j.put("alerts", alerts);
		//TODO add filename
		return j.toString();
	}

	public static String generateJSONString(int studentId, int assignmentId, String snapshot, int alert, String datetime, String alertType){
		JSONObject j = new JSONObject();
		j.put("assignment_id", assignmentId);
		j.put("student_id", studentId);
		j.put("snapshot", snapshot);
		j.put("alert", alert);
		j.put("datetime", datetime);
		j.put("alert_type", alertType);
		
		JSONArray a = new JSONArray();
		//a.add()
		return j.toString();
	}
	
	private static void loadSettings() {
		  try {
			prefs.sync();
		  } catch (BackingStoreException e) {
		  }
		  sunet = prefs.get("sunet", "unknown");
		  name= prefs.get("name", "unknown");
	}

}
