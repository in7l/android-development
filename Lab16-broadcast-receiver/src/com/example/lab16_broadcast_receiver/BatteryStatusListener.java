package com.example.lab16_broadcast_receiver;

public interface BatteryStatusListener {
	public void setHealth(String health);
	public void setLevel(String level);
	public void setStatus(String status);
	public void setPlugged(String plugged);
	public void setTechnology(String technology);
	public void setTemperature(String temperature);
	public void setVoltage(String voltage);
}
