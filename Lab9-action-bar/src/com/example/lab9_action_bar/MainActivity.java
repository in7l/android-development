package com.example.lab9_action_bar;

import android.os.Bundle;
import android.R.anim;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
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
				toastMessage = getString(R.string.release);
				break;
			case R.id.ImprisonItem:
				toastMessage = getString(R.string.imprison);
				break;
			case android.R.id.home:
				toastMessage = "Home button pressed";
		}
		
		if (toastMessage != null) {
			toast.setText(toastMessage);
			toast.show();
		}
		
		return true;
	}
    
    
}
