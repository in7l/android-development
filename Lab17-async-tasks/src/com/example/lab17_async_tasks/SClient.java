package com.example.lab17_async_tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SClient extends AsyncTask<URL, Void, String> {
	private SClientListener uiListener;
	
	public SClient(SClientListener listener) {
		// Store a reference to the activity (listener).
		uiListener = listener;
	}

	@Override
	protected String doInBackground(URL... params) {
		String sourceString = "Loading source file failed.";
		try {
			URL url = params[0];
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			StringBuilder siteSource = new StringBuilder();
			int ch;
			while ((ch = br.read()) != -1) {
				siteSource.append((char)ch);
			}
			br.close();
			conn.disconnect();
			
			sourceString = siteSource.toString();
		} catch (Exception e) {
			Log.e("TCP", "C: Error", e);
		}
		
		return sourceString;
	}

	@Override
	protected void onPostExecute(String result) {
		// Update the source code text.
		uiListener.setSourceCodeText(result);
		// Dismiss the progress dialog.
		uiListener.showProgressDialog(false);
	}
}
