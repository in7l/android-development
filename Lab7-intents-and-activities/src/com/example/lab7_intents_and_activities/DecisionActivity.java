package com.example.lab7_intents_and_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DecisionActivity extends Activity implements OnClickListener {
	private String default_message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);
		
		// Get the prisoner name and update the question.
		String prisoner_name = getIntent().getStringExtra("prisoner_name");
		TextView tv = (TextView)findViewById(R.id.tv2);
		default_message = getString(R.string.make_a_decision);
		tv.setText(default_message + " " + prisoner_name);
		
		// Register listeners for the buttons.
		Button imprisonButton = (Button)findViewById(R.id.ImprisonButton);
		Button releaseButton = (Button)findViewById(R.id.ReleaseButton);
		imprisonButton.setOnClickListener(this);
		releaseButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		boolean pris = true;
		
		// Check if this person should be imprisoned or released.
		switch (v.getId()) {
			case R.id.ImprisonButton:
				pris = true;
				break;
			case R.id.ReleaseButton:
				pris = false;
				break;
		}
		
		Intent res = new Intent();
		res.putExtra("imprison", pris);
		setResult(Activity.RESULT_OK, res);
		finish();
	}

}
