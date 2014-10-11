package ch.smartapes.linguaadhoc.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class POIFetcherTask extends AsyncTask<String, Void, Void> {

	private static final String LOG_TAG = POIFetcherTask.class.getSimpleName();

	List<WordCriteria> wcl;

	private List<POIFetchListener> listenerList = new ArrayList<POIFetchListener>();

	public void addListener(POIFetchListener listener) {
		listenerList.add(listener);
	}

	public interface POIFetchListener {
		public void poisReady(List<WordCriteria> wcl);
	}

	public void poisReady() {
		for (POIFetchListener listener : listenerList) {
			listener.poisReady(wcl);
		}
	}

	@Override
	protected Void doInBackground(String... params) {

		wcl = new ArrayList<WordCriteria>();

		String param = null;
		if (params.length == 4) {
			param = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
					+ "json?location=" + params[0] + "," + params[1]
					+ "&radius=" + params[2] + "&sensor=true" + "&types="
					+ params[3]
					+ "&key=AIzaSyC7WzPDnLgGyRnFlw9UAW_hPNiIGtHMBrw";
		} else if (params.length == 3) {
			param = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
					+ "json?location=" + params[0] + "," + params[1]
					+ "&radius=" + params[2] + "&sensor=true"
					+ "&key=AIzaSyC7WzPDnLgGyRnFlw9UAW_hPNiIGtHMBrw";
		} else {
			Log.d(LOG_TAG, "Not enough parameters specified!");
			return null;
		}

		StringBuilder places = new StringBuilder();
		try {
			URL url = new URL(param);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				places.append(line);
			}
			JSONObject jsonObj = new JSONObject(places.toString());
			JSONArray jsonArray = jsonObj.getJSONArray("results");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject place = jsonArray.getJSONObject(i);
				JSONArray types = place.getJSONArray("types");
				String name = place.getString("name");

				String[] classificators = new String[types.length()];
				for (int j = 0; j < types.length(); j++) {
					classificators[j] = types.getString(j);
				}

				WordCriteria wc = new WordCriteria(name, classificators);
				wcl.add(wc);
			}

		} catch (Exception e) {
			Log.d(LOG_TAG, e.toString());
			return null;
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(Void param)
	{
		poisReady();
	}

}
