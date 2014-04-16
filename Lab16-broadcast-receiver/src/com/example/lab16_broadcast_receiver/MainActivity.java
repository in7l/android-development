package com.example.lab16_broadcast_receiver;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity implements BatteryStatusListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void setHealth(String health) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLevel(String level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(String status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlugged(String plugged) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTechnology(String technology) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTemperature(String temperature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVoltage(String voltage) {
		// TODO Auto-generated method stub
		
	}

}
