package com.example.lab16_broadcast_receiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements BatteryStatusListener {
	private TextView healthTextView;
	private TextView levelTextView;
	private TextView statusTextView;
	private TextView pluggedTextView;
	private TextView technologyTextView;
	private TextView temperatureTextView;
	private TextView voltageTextView;
	private TextView lastUpdatedTextView;
	private BroadcastReceiver batteryBroadcastReceiver = new BatteryBroadcastReceiver();
	private boolean receiverRegistered = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get references to all text views that will need to be updated.
		healthTextView = (TextView)findViewById(R.id.health);
		levelTextView = (TextView)findViewById(R.id.level);
		statusTextView = (TextView)findViewById(R.id.status);
		pluggedTextView = (TextView)findViewById(R.id.plugged);
		technologyTextView = (TextView)findViewById(R.id.technology);
		temperatureTextView = (TextView)findViewById(R.id.temperature);
		voltageTextView = (TextView)findViewById(R.id.voltage);
		lastUpdatedTextView = (TextView)findViewById(R.id.last_updated);
	}
	
		

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getBatteryStatus(intent);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(batteryBroadcastReceiver);
		receiverRegistered = false;
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!receiverRegistered) {
			// ACTION_BATTERY_CHANGED cannot be received
			// through components declared in manifests, only by explicitly
			// registering for it.
			registerReceiver(batteryBroadcastReceiver,
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			receiverRegistered = true;
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void setHealth(String health) {
		healthTextView.setText(health);
	}

	@Override
	public void setLevel(float level) {
		float levelPercentage = level * 100;
		String levelString = String.valueOf(levelPercentage) + "%";
		levelTextView.setText(levelString);
	}

	@Override
	public void setStatus(String status) {
		statusTextView.setText(status);
	}

	@Override
	public void setPlugged(String plugged) {
		pluggedTextView.setText(plugged);
	}

	@Override
	public void setTechnology(String technology) {
		technologyTextView.setText(technology);
	}

	@Override
	public void setTemperature(int temperature) {
		float floatTemp = temperature / (float) 10;
		String temperatureString = String.valueOf(floatTemp) + "°C";
		temperatureTextView.setText(temperatureString);
	}

	@Override
	public void setVoltage(int voltage) {
		float voltageInVolts = voltage / (float) 1000;
		String voltageString = String.valueOf(voltageInVolts) + "V";
		voltageTextView.setText(voltageString);
	}

	@Override
	public void getBatteryStatus(Intent intent) {
		setHealth(intent.getStringExtra(BatteryStatusListener.HEALTH_STRING));
		setLevel(intent.getFloatExtra(BatteryStatusListener.LEVEL_STRING, 0));
		setStatus(intent.getStringExtra(BatteryStatusListener.STATUS_STRING));
		setPlugged(intent.getStringExtra(BatteryStatusListener.PLUGGED_STRING));
		setTechnology(intent.getStringExtra(BatteryStatusListener.TECHNOLOGY_STRING));
		setTemperature(intent.getIntExtra(BatteryStatusListener.TEMPERATURE_STRING, 0));
		setVoltage(intent.getIntExtra(BatteryStatusListener.VOLTAGE_STRING, 0));
		setLastUpdated();
	}


	public void setLastUpdated() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		lastUpdatedTextView.setText(dateFormat.format(date));
	}

}
