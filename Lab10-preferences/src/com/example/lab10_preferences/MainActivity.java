package com.example.lab10_preferences;

import android.os.Bundle;
import android.R.anim;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String PREF = "JailHistory";
	private static final String RELEASED_TIMES_KEY = "released_count";
	private static final String IMPRISONED_TIMES_KEY = "imprisoned_count";
	private Toast toast;
	SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Create a single Toast object that will display different messages.
        Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		toast = Toast.makeText(context, "", duration);
		
		// Enable home button.
		ActionBar bar = getActionBar();
		bar.setHomeButtonEnabled(true);
		
		// Get the shared preferences object.
		pref = getSharedPreferences(PREF, Activity.MODE_PRIVATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String toastMessage = null;
		Editor prefEditor = null;
		switch (item.getItemId()) {
			case R.id.ReleaseItem:
				// Get how many times the person has been released from jail.
				int released_count = pref.getInt(RELEASED_TIMES_KEY, 0);
				// Increment by 1.
				released_count++;
				// Store the new count.
				prefEditor = pref.edit();
				prefEditor.putInt(RELEASED_TIMES_KEY, released_count);
				// Store the preference.
				prefEditor.commit();
				// Display a toast message with the current value.
				toastMessage = getString(R.string.release) + " "
						+ released_count + " times";
				break;
			case R.id.ImprisonItem:
				// Get how many times the person has been put to jail.
				int imprisoned_count = pref.getInt(IMPRISONED_TIMES_KEY, 0);
				// Increment by 1.
				imprisoned_count++;
				// Store the new count.
				prefEditor = pref.edit();
				prefEditor.putInt(IMPRISONED_TIMES_KEY, imprisoned_count);
				// Store the preference.
				prefEditor.commit();
				// Display a toast message with the current value.
				toastMessage = getString(R.string.imprison) + " "
						+ imprisoned_count + " times";
				
				break;
			case android.R.id.home:
				prefEditor = pref.edit();
				prefEditor.putInt(RELEASED_TIMES_KEY, 0);
				prefEditor.putInt(IMPRISONED_TIMES_KEY, 0);
				prefEditor.commit();
				toastMessage = "Criminal history cleared";
		}
		
		if (toastMessage != null) {
			toast.setText(toastMessage);
			toast.show();
		}
		
		return true;
	}
    
    
}
