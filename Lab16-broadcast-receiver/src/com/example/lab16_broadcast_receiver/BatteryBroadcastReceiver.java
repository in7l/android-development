package com.example.lab16_broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Fetch different data.
		
		// Health.
		int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
		String healthString;
		switch (health) {
			case BatteryManager.BATTERY_HEALTH_COLD:
				healthString = "Cold";
				break;
			case BatteryManager.BATTERY_HEALTH_DEAD:
				healthString = "Dead";
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:
				healthString = "Good";
				break;
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				healthString = "Over Voltage";
				break;
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				healthString = "Overheat";
				break;
			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				healthString = "Unspecified Failure";
				break;
			default:
				healthString = "Unknown";
				break;
		}
		
		// Level.
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float) scale;
		
		
		// Status.
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		String statusString;
		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
			statusString = "charging";
		} else {
			statusString = "not charging";
		}
		
		// Plugged.
		int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		String pluggedString;
		if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
			pluggedString = "USB";
		} else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
			pluggedString = "AC";
		} else if (chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
			pluggedString = "Wireless";
		} else {
			pluggedString = "No";
		}
		
		// Technology.
		String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
		
		// Temperature.
		int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
		
		// Voltage.
		int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
		
		
		
		// Send the fetched data back to the activity.
		Intent batteryListenerIntent = new Intent(context, MainActivity.class);
		batteryListenerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		batteryListenerIntent.putExtra(BatteryStatusListener.HEALTH_STRING, healthString);
		batteryListenerIntent.putExtra(BatteryStatusListener.LEVEL_STRING, batteryPct);
		batteryListenerIntent.putExtra(BatteryStatusListener.STATUS_STRING, statusString);
		batteryListenerIntent.putExtra(BatteryStatusListener.PLUGGED_STRING, pluggedString);
		batteryListenerIntent.putExtra(BatteryStatusListener.TECHNOLOGY_STRING, technology);
		batteryListenerIntent.putExtra(BatteryStatusListener.TEMPERATURE_STRING, temperature);
		batteryListenerIntent.putExtra(BatteryStatusListener.VOLTAGE_STRING, voltage);
		
		
		context.startActivity(batteryListenerIntent);
		
		
	}

}
