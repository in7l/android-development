package com.example.lab17_async_tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SClient implements Runnable {
	private Handler ui;
	
	public SClient(Handler handler) {
		// Store handler to UI thread.
		ui = handler;
	}

	@Override
	public void run() {
		try {
			URL url = new URL("http://www.google.com");
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			StringBuilder siteSource = new StringBuilder();
			int ch;
			while ((ch = br.read()) != -1) {
				siteSource.append((char)ch);
			}
			
			// Obtain a message from the ui handler and send a respone.
			Message msg = ui.obtainMessage();
			msg.what = 0;
			msg.obj = siteSource.toString();
			ui.sendMessage(msg);
		} catch (Exception e) {
			Log.e("TCP", "C: Error", e);
		}
	}

}
