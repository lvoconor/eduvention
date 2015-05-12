package edu.stanford.eduvention;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

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

	private IEclipsePreferences prefs;
	private String name;
	private String sunet;
	private HttpClient httpClient;
	private long lastUpdate;
	
	public DataManager() {
		prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");
		httpClient = HttpClientBuilder.create().build();
		lastUpdate = System.currentTimeMillis() / 1000L;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(long t) {
		lastUpdate = t;
	}
	
	public void update() {
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
	
	public void postSnapshot(AlertFile f) {
		loadSettings();
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

	private String generateJSONString(AlertFile f){
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder alertBuilder = factory.createArrayBuilder();
		for(Alert alert: f.alerts){
			alertBuilder.add(factory.createObjectBuilder().add("alert_type", alert.type));
			//TODO add line numbers
		}
		JsonArray alerts = alertBuilder.build();

		JsonObject j = factory.createObjectBuilder()
			.add("assignment_id", 6) //TODO store assignment_id
			.add("student_id", sunet)
			.add("snapshot", f.contents)
			.add("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
			.add("alerts", alerts)
			.build();
		//TODO add filename
		
		return j.toString();
	}

	public String generateJSONString(int studentId, int assignmentId, String snapshot, int alert, String datetime, String alertType) {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObject j = factory.createObjectBuilder()
				.add("assignment_id", assignmentId)
				.add("student_id", studentId)
				.add("snapshot", snapshot)
				.add("datetime", datetime)
				.add("alerts", alertType)
				.build();

		return j.toString();
	}
	
	private void loadSettings() {
		  try {
			prefs.sync();
		  } catch (BackingStoreException e) {
		  }
		  sunet = prefs.get("sunet", "unknown");
		  name= prefs.get("name", "unknown");
	}
}
