package com.example.lab9_action_bar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Toast toast;
	private OnClickListener buttonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Button button = (Button)v;
			// Get the text of the clicked button.
			CharSequence text = button.getText();
			// Set the text to the toast object.
			toast.setText(text);
			// Show the toast.
			toast.show();
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Create a sinle Toast object that will display different messages.
        Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		toast = Toast.makeText(context, "", duration);
		
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
    
}
