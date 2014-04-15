package com.example.lab7_intents_and_activities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private final int GET_DECISION_RES = 1;
	private TextView decisionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button decisionButton = (Button) findViewById(R.id.AskDecisionButton);
		decisionButton.setOnClickListener(this);
		decisionTextView = (TextView)findViewById(R.id.tv1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Intent myIntent = new Intent(this, DecisionActivity.class);
		myIntent.putExtra("prisoner_name", getString(R.string.prisoner_name));
		startActivityForResult(myIntent, GET_DECISION_RES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check if this is a response to the correct intent.
		if (requestCode == GET_DECISION_RES) {
			if (resultCode == Activity.RESULT_OK) {
				boolean imprison = data.getBooleanExtra("imprison", true);
				String decisionString;
				if (imprison) {
					decisionString = getString(R.string.send_to_prison);
				}
				else {
					decisionString = getString(R.string.release_from_prison);
				}
				decisionTextView.setText(decisionString);
			}
		}
	}
}
