package com.example.lab6_alert_dialog;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements AlertListener {
	private TextView dialogTextView;
	private String message;
	
	private OnClickListener buttonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Button button = (Button)v;
			// Store the text of the clicked button.
			message = button.getText().toString();
			
			DialogFragment alertFragment = new AlertFragment();
			alertFragment.show(getFragmentManager(), "AlertDialogFragment");
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Get the TextView which will be modified when the dialog buttons are clicked.
        dialogTextView = (TextView) findViewById(R.id.TextViewUserTitle);
		
		// Register listeners with the buttons.
		Button b1 = (Button)findViewById(R.id.ButtonImprison);
		Button b2 = (Button)findViewById(R.id.ButtonRelease);
		b1.setOnClickListener(buttonOnClickListener);
		b2.setOnClickListener(buttonOnClickListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onPosClick() {
		if (message.equalsIgnoreCase("Send to prison")) {
			dialogTextView.setText("Joe was sent to prison");
		}
		else if (message.equalsIgnoreCase("Release")) {
			dialogTextView.setText("Joe was released from prison");
		}
		else {
			dialogTextView.setText(R.string.user_title);
		}
	}

	@Override
	public void onCancelClick() {
		if (message.equalsIgnoreCase("Send to prison")) {
			dialogTextView.setText("Joe was not sent to prison");
		}
		else if (message.equalsIgnoreCase("Release")) {
			dialogTextView.setText("Joe was not released from prison");
		}
		else {
			dialogTextView.setText(R.string.user_title);
		}
	}

	@Override
	public String getMessage() {
		// Return the message of the clicked button.
		return message;
	}
	
	
}
