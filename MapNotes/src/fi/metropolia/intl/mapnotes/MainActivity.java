package fi.metropolia.intl.mapnotes;

import android.os.Bundle;
import android.R.animator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity implements ToggleMapListener, NoteListListener {
	FragmentManager fragmentManager;
	private View mapContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Store a reference to the fragment manager.
		fragmentManager = getFragmentManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void showMap(boolean show) {
		if (show) {
			// The map should be shown.
			Log.i("Map", "Show map");
			
			// Create the map fragment.
			Fragment mapFragment = new MapFragment();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			// Set animation for the transaction.
			transaction.setCustomAnimations(animator.fade_in, animator.fade_out);
			// Add the fragment to the MapContainer.
			transaction.add(R.id.MapContainer, mapFragment);
			// Commit the changes.
			transaction.commit();
			
			// Update the height and weight of the mapContainer.
			updateMapContainerProperties(show);
		} else {
			// The map should be hidden.
			Log.i("Map", "Hide map");
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			// Set animation for the transaction.
			transaction.setCustomAnimations(animator.fade_in, animator.fade_out);
			// Remove the fragment from MapContainer.
			transaction.remove(fragmentManager.findFragmentById(R.id.MapContainer));
			transaction.commit();
			// Update the height and weight of the mapContainer.
			updateMapContainerProperties(show);
		}
	}
	
	private void updateMapContainerProperties(boolean showMap) {
		if (mapContainer == null) {
			// Get a reference to the MapContainer item in the main layout.
			mapContainer = findViewById(R.id.MapContainer);
		}
		
		if (showMap) {
			// Set the layout weight of the MapContainer to 1.0.
			mapContainer.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		} else {
			// The mapContainer should be hidden.
			// Set the MapContainer layout weight to 0.
			mapContainer.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT, 0f));
		}
	}

	@Override
	public void openNote(Note note) {
		Log.i("Note", "Got note with description: " + note.getDescriptionString());
		// Make a new intent to start a NoteViewActivity.
		Intent intent = new Intent(this, NoteViewActivity.class);
		// Pass the serialized Note to that activity.
		intent.putExtra(Note.NOTE_BUNDLE_KEY, note);
		// Start the activity.
		startActivity(intent);
	}

}
