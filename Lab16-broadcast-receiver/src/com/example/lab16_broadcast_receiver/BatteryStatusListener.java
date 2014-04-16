package com.example.lab16_broadcast_receiver;

import android.content.Intent;

public interface BatteryStatusListener {
	public final static String HEALTH_STRING = "health";
	public final static String LEVEL_STRING = "level";
	public final static String STATUS_STRING = "status";
	public final static String PLUGGED_STRING = "plugged";
	public final static String TECHNOLOGY_STRING = "technology";
	public final static String TEMPERATURE_STRING = "temperature";
	public final static String VOLTAGE_STRING = "voltage";
	
	
	public void getBatteryStatus(Intent intent);
	public void setHealth(String health);
	public void setLevel(float level);
	public void setStatus(String status);
	public void setPlugged(String plugged);
	public void setTechnology(String technology);
	public void setTemperature(int temperature);
	public void setVoltage(int voltage);
}
