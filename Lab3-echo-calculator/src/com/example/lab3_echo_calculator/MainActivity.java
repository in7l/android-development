package com.example.lab3_echo_calculator;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ArrayList<Button> calculatorButtons;
	private TextView calculatorDisplayTextView;
	private View.OnClickListener calculatorOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// This listener should be used with buttons.
			Button button = (Button) v;
			String buttonText = button.getText().toString();
			calculatorDisplayTextView.setText(buttonText);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get the calculator display element.
		calculatorDisplayTextView = (TextView) findViewById(R.id.TextViewCalculatorDisplay);
		
		// Fetch all calculator buttons and store them in the array list.
		calculatorButtons = new ArrayList<Button>(10);
		calculatorButtons.add((Button)findViewById(R.id.Button0));
		calculatorButtons.add((Button)findViewById(R.id.Button1));
		calculatorButtons.add((Button)findViewById(R.id.Button2));
		calculatorButtons.add((Button)findViewById(R.id.Button3));
		calculatorButtons.add((Button)findViewById(R.id.Button4));
		calculatorButtons.add((Button)findViewById(R.id.Button5));
		calculatorButtons.add((Button)findViewById(R.id.Button6));
		calculatorButtons.add((Button)findViewById(R.id.Button7));
		calculatorButtons.add((Button)findViewById(R.id.Button8));
		calculatorButtons.add((Button)findViewById(R.id.Button9));
		
		// Register calculator echo listener for all of these buttons.
		for (Button button : calculatorButtons) {
			button.setOnClickListener(calculatorOnClickListener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
