package com.example.lab15_services_and_notifications;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SimServ extends Service {
	private URL url;
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				String notificationMessage = ((AuthorityMessageObject)msg.obj).getMessage();
				int imageResourceId = ((AuthorityMessageObject)msg.obj).getImageResourceId();
				sendNotification(notificationMessage, imageResourceId);
			}
			stopSelf();
		}
	};
	
	
	
	@Override
	public void onCreate() {
		String urlString = getString(R.string.authority_service_url);
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			Log.e("MalformedUrl", e.getMessage(), e);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AuthorityClient ac = new AuthorityClient(myHandler, url);
		Thread thread = new Thread(ac);
		thread.start();
		
		// If the process is killed while it's running, no need to restart.
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void sendNotification(String message, int imageResourceId) {
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.authority);
		Notification.Builder mBuilder = new Notification.Builder(this);
		mBuilder.setSmallIcon(imageResourceId);
		mBuilder.setContentTitle(getString(R.string.notification_title));
		mBuilder.setContentText(message);
		mBuilder.setLargeIcon(bitmap);
		Notification notification = mBuilder.build();
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
		
	}

}
