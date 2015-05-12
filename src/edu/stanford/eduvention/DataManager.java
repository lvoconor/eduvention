package edu.stanford.eduvention;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import edu.stanford.eduvention.views.Alert;

public class DataManager {

	private IEclipsePreferences prefs;
	private String name;
	private String sunet;
	private long lastUpdate;

	public DataManager() {
		prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");
		lastUpdate = System.currentTimeMillis() / 1000L;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long t) {
		lastUpdate = t;
	}

	/* Sources:
	 * http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
	 */
	public void postSnapshot(AlertFile f) {
		loadSettings();
		// Generate POST request body
		String snapshotString = "request=" + generateJSONString(f);
		byte[] postData = snapshotString.getBytes(Charset.forName("UTF-8"));
		int postDataLength = postData.length;
		URL url;
		try {
			url = new URL("http://eduvention-website.herokuapp.com/snapshots/create");
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
			return;
		}
		HttpURLConnection cox;
		try {
			cox = (HttpURLConnection) url.openConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}           
		cox.setDoOutput(true);
		cox.setDoInput (true);
		cox.setInstanceFollowRedirects( false );
		try {
			cox.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			e1.printStackTrace();
			return;
		}
		cox.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		cox.setRequestProperty("charset", "utf-8");
		cox.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		cox.setUseCaches(false);
		try {
			DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
			wr.write(postData);
			wr.close();
		} catch (Exception e) {
		}
		
        try {
			System.out.println(cox.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	private void loadSettings() {
		  try {
			prefs.sync();
		  } catch (BackingStoreException e) {
		  }
		  sunet = prefs.get("sunet", "unknown");
		  name= prefs.get("name", "unknown");
	}
}
