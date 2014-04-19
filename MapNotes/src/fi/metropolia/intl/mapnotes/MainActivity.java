package fi.metropolia.intl.mapnotes;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity implements ToggleMapListener {
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
			// Add the fragment to the MapContainer.
			transaction.add(R.id.MapContainer, mapFragment);
			// Store the changes to backstack so that user can revert them
			// by pressing back.
			transaction.addToBackStack(null);
			// Commit the changes.
			transaction.commit();
			
			// Update the height and weight of the mapContainer.
			updateMapContainerProperties(show);
		} else {
			// The map should be hidden.
			Log.i("Map", "Hide map");
			// Update the height and weight of the mapContainer.
			updateMapContainerProperties(show);
			fragmentManager.popBackStack();
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
			mapContainer.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f));
		}
	}

}
