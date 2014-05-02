package fi.metropolia.intl.mapnotes.map;

import fi.metropolia.intl.mapnotes.R;
import fi.metropolia.intl.mapnotes.R.id;
import fi.metropolia.intl.mapnotes.R.layout;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class ToggleMapFragment extends Fragment implements OnClickListener {
	private ToggleMapListener mListener;
	private ImageButton toggleMapButton;
	// At first the map is not shown.
	private boolean show = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Attempt to save a reference to the activity
		// which should implement ToggleMapListener.
		try {
			mListener = (ToggleMapListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ToggleMapListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the toggle_map layout used by this fragment.
		View v = inflater.inflate(R.layout.toggle_map, container, false);
		// Register listener with the button.
		toggleMapButton = (ImageButton)v.findViewById(R.id.ShowHideMapButton);
		toggleMapButton.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ShowHideMapButton) {
			// Invert the show map value.
			show = !show;
			// Notify the ToggleMapListener about the change.
			mListener.showMap(show);
		}
	}
}
