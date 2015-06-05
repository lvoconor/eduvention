package edu.stanford.eduvention;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.bind.DatatypeConverter;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;

public class NetworkManager {

	private IEclipsePreferences prefs;
	private String sunet;
	private Object[] questions = {"Connect to the Internet and save your file to get your questions."};
	private long lastUpdate = 0;
	private static final int MIN_CHANGE_INTERVAL = 3000;

	private static final String GET_QUESTION_URL = "http://eduvention-website.herokuapp.com/get_comments?sunet="; // Append SUNet
	private static final String POST_QUESTION_URL = "http://eduvention-website.herokuapp.com/question/create";
	private static final String POST_SNAPSHOT_URL = "http://eduvention-website.herokuapp.com/snapshots/create";
	private static final String ENCODING = "US-ASCII";

	public NetworkManager() {
		prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");
		downloadQuestionsAsync();
	}

	public Object[] getQuestions() {
		if (lastUpdate > System.currentTimeMillis() - MIN_CHANGE_INTERVAL)
			return questions;
		lastUpdate = System.currentTimeMillis();
		downloadQuestionsAsync();
		return questions;
	}

	private void setQuestions(final Object[] questions) {
		this.questions = questions;
	}
	
	private void downloadQuestionsAsync() {
		Display.getDefault().asyncExec(new Runnable() {
	    	public void run() {
	    		loadSettings();
	    		URL url;
	    		try {
	    			url = new URL(GET_QUESTION_URL+sunet);
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
	    		cox.setDoInput(true);
	    		cox.setInstanceFollowRedirects(false);
	    		try {
	    			cox.setRequestMethod("GET");
	    		} catch (ProtocolException e1) {
	    			e1.printStackTrace();
	    			return;
	    		}
	    		cox.setRequestProperty("charset", "utf-8");
	    		cox.setUseCaches(false);
	    		String response = "";
	    		try {
	    			String line;
	    			BufferedReader rd = new BufferedReader(new InputStreamReader(cox.getInputStream()));
	    	         while ((line = rd.readLine()) != null) {
	    	            response = response + " " + line;
	    	         }
	    	         rd.close();
	    		} catch (Exception e) {
	    		}
	    		try {
	    			System.out.println(cox.getResponseCode());
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    		if (response == null || response.equals("")) {
	    			return;
	    		}
				ArrayList<Question> questions = new ArrayList<Question>();
	    		JsonReader jsonReader = Json.createReader(new StringReader(response));
	    		JsonArray arr = jsonReader.readArray();
	    		jsonReader.close();
	    		for(int i = 0; i < arr.size(); i++){
	    			// TODO: Enable getting file info from JSON
	    			JsonObject jobj = arr.getJsonObject(i);
	    			if (jobj == null) {
	    				continue;
	    			}
	    			String role = arr.getJsonObject(i).getString("type");
	    			String poster = role.equals("student") ? "You" : role.substring(0, 1).toUpperCase() + role.substring(1);
	    			String message = arr.getJsonObject(i).getString("message");
	    			String filename = arr.getJsonObject(i).getString("filename");
	    			int lineNumber = arr.getJsonObject(i).getInt("line_number");
	    			String timestamp = arr.getJsonObject(i).getString("created_at");
	    			Question question = new Question(poster, message, filename, lineNumber, timestamp);
	    			questions.add(question);
	            }
	    		setQuestions(questions.toArray());
    		}
	    });
	}
	
	/*
	 * Sources:
	 * http://stackoverflow.com/questions/4205980/java-sending-http-parameters
	 * -via-post-method-easily
	 */
	public void postSnapshot(AlertFile f) {
		loadSettings();
		// Generate POST request body
		String json = generateJSONString(f);
		if (json == null)
			return;
		String snapshotString = "request=" + json;
		byte[] postData;
		try {
			postData = snapshotString.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e3) {
			e3.printStackTrace();
			return;
		}
		int postDataLength = postData.length;
		URL url;
		try {
			url = new URL(POST_SNAPSHOT_URL);
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
		cox.setDoInput(true);
		cox.setInstanceFollowRedirects(false);
		try {
			cox.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			e1.printStackTrace();
			return;
		}
		cox.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		cox.setRequestProperty("charset", "utf-8");
		cox.setRequestProperty("Content-Length",
				Integer.toString(postDataLength));
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

	private String generateJSONString(AlertFile f) {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder alertBuilder = factory.createArrayBuilder();
		for (Alert alert : f.alerts) {
			alertBuilder.add(factory.createObjectBuilder()
					.add("alert_type", alert.type)
					.add("line_number", alert.lineNumber));
		}
		JsonArray alerts = alertBuilder.build();
		String contents;
		try {

			String raw_contents = DatatypeConverter
					.printBase64Binary(f.contents.getBytes(ENCODING));
			contents = URLEncoder.encode(raw_contents, ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		JsonObject j = factory
				.createObjectBuilder()
				.add("assignment_id", 6)
				// TODO store assignment_id
				.add("student_id", sunet)
				.add("snapshot", contents)
				.add("datetime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date())).add("alerts", alerts)
				.add("filename", f.name).build();

		return j.toString();
	}

	public void postQuestion(String question, String filename, int line_number) {
		loadSettings();
		// Generate POST request body
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		String contents;
		try {
			String raw_contents = DatatypeConverter.printBase64Binary(question
					.getBytes(ENCODING));
			contents = URLEncoder.encode(raw_contents, ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		String json = factory
				.createObjectBuilder()
				.add("assignment_id", 6)
				// TODO store assignment_id
				.add("student_id", sunet)
				.add("filename", filename)
				.add("line_number", line_number)
				.add("question", contents)
				.add("datetime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date())).build().toString();
		if (json == null)
			return;
		String questionString = "request=" + json;
		byte[] postData;
		try {
			postData = questionString.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e3) {
			e3.printStackTrace();
			return;
		}
		int postDataLength = postData.length;
		URL url;
		try {
			url = new URL(POST_QUESTION_URL);
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
		cox.setDoInput(true);
		cox.setInstanceFollowRedirects(false);
		try {
			cox.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			e1.printStackTrace();
			return;
		}
		cox.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		cox.setRequestProperty("charset", "utf-8");
		cox.setRequestProperty("Content-Length",
				Integer.toString(postDataLength));
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
		downloadQuestionsAsync();
	}

	private void loadSettings() {
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			return;
		}
		sunet = prefs.get("sunet", "unknown");
	}
}
