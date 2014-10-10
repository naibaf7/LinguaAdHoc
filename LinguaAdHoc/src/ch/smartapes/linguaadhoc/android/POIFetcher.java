package ch.smartapes.linguaadhoc.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class POIFetcher extends AsyncTask<String, Void, String> {

	private static final String LOG_TAG = "POIFetcher";

	@Override
	protected String doInBackground(String... params) {

		String param;
		if (params.length == 0) {
			param = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
					+ "json?location=" + lat + "," + lng
					+ "&radius=1000&sensor=true"
					+ "&types=food|bar|store|museum|art_gallery"
					+ "&key=your_key_here";
		}

		StringBuilder places = new StringBuilder();

		HttpClient hc = new DefaultHttpClient();

		try {
			HttpClient hc = new DefaultHttpClient();
			HttpResponse httpResponse = hc.execute(request);
			StatusLine sl = httpResponse.getStatusLine();
			if (sl.getStatusCode() == 200) {
				HttpEntity he = httpResponse.getEntity();
				InputStreamReader isr = new InputStreamReader(he.getContent());
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					places.append(line);
				}
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, e.toString());
			return null;
		}

		return places.toString();
	}

}
