package com.example.lab15_services_and_notifications;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AuthorityClient implements Runnable {
	private Handler sHandler;
	private URL url;
	
	public AuthorityClient(Handler sH, URL url) {
		sHandler = sH;
		this.url = url;
	}

	@Override
	public void run() {
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			StringBuilder siteResponse = new StringBuilder();
			int ch;
			while ((ch = br.read()) != -1) {
				siteResponse.append((char)ch);
			}
			br.close();
			conn.disconnect();
			
			// Obtain a message from the service handler and send a response.
			Message msg = sHandler.obtainMessage();
			msg.what = 0;
			String siteResponseString = siteResponse.toString();
			int imageResourceId = R.drawable.release;
			if (siteResponseString.equals("Send him to prison")) {
				imageResourceId = R.drawable.imprison;
			}
			msg.obj = new AuthorityMessageObject(siteResponseString, imageResourceId);
			sHandler.sendMessage(msg);
		} catch (Exception e) {
			Log.e("TCP", "C: Error", e);
		}
	}

}
