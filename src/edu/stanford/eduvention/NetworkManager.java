package edu.stanford.eduvention;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.xml.bind.DatatypeConverter;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class NetworkManager {

	private IEclipsePreferences prefs;
	private String name;
	private String sunet;

	private static final String ENCODING = "US-ASCII";

	public NetworkManager() {
		prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");
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
			url = new URL(
					"http://eduvention-website.herokuapp.com/snapshots/create");
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

	public void postQuestion(String question) {
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
			url = new URL(
					"http://eduvention-website.herokuapp.com/question/create");
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

	private void loadSettings() {
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			return;
		}
		sunet = prefs.get("sunet", "unknown");
		name = prefs.get("name", "unknown");
	}
}
