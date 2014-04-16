package com.example.lab11_external_storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Environment;
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
	private final static String JSON_FNAME = "jsonCounter";
	private final static String RELEASED_PROPERTY = "releasedCount";
	private final static String IMPRISONED_PROPERTY = "imprisonedCount";
	private JSONObject jsonCounter = null;
	private String jsonString = null;
	private Toast toast;

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
		
		// Read the jsonCounter.
		try {
			loadJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		switch (item.getItemId()) {
			case R.id.ReleaseItem:
				try {
					// Get how many times the person has been released from jail.
					int released_count = jsonCounter.getInt(RELEASED_PROPERTY);
					// Increment by 1.
					released_count++;
					
					// Store the new count.
					jsonCounter.put(RELEASED_PROPERTY, released_count);
					saveJson();
					// Display a toast message with the current value.
					toastMessage = getString(R.string.release) + " "
							+ released_count + " times";
				} catch (Exception e1) {
					toastMessage = "Failed to update property " + RELEASED_PROPERTY;
				}
				
				
				break;
			case R.id.ImprisonItem:
				try {
					// Get how many times the person has been put to jail.
					int imprisoned_count = jsonCounter.getInt(IMPRISONED_PROPERTY);
					// Increment by 1.
					imprisoned_count++;

					// Store the new count.
					jsonCounter.put(IMPRISONED_PROPERTY, imprisoned_count);
					saveJson();
					// Display a toast message with the current value.
					toastMessage = getString(R.string.imprison) + " "
							+ imprisoned_count + " times";
				} catch (Exception e) {
					toastMessage = "Failed to update property " + IMPRISONED_PROPERTY;
				}
				
				break;
			case android.R.id.home:
				try {
					jsonCounter.put(RELEASED_PROPERTY, 0);
					jsonCounter.put(IMPRISONED_PROPERTY, 0);
					saveJson();
					toastMessage = "Criminal history cleared";
				}
				catch (Exception e) {
					toastMessage = "Failed to reset properties.";
				}
		}
		
		if (toastMessage != null) {
			toast.setText(toastMessage);
			toast.show();
		}
		
		return true;
	}
    
	
	private void loadJson() throws IOException, JSONException {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(getExternalFilesDir(null), JSON_FNAME);
			BufferedReader buf = new BufferedReader(new FileReader(file));
			int content;
			char ch;
			StringBuilder fileContent = new StringBuilder(50);
			
			while ((content = buf.read()) != -1) {
				fileContent.append((char)content);
			}
			
			buf.close();
			
			jsonString = fileContent.toString();
			jsonCounter = new JSONObject(jsonString);
		}
	}
	
	private void saveJson() throws IOException {
		jsonString = jsonCounter.toString();
		
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(getExternalFilesDir(null), JSON_FNAME);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bufW = new BufferedWriter(fWriter);
			bufW.write(jsonString);
			bufW.close();
		}
	}
    
}
