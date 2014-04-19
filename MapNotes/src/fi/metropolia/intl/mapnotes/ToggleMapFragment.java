package fi.metropolia.intl.mapnotes;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ToggleMapFragment extends Fragment implements OnClickListener {
	private ToggleMapListener mListener;
	private Button toggleMapButton;
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
		toggleMapButton = (Button)v.findViewById(R.id.ShowHideMapButton);
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
			// Update the views associated with the current Fragment.
			toggleFragmentViews(show);
		}
	}
	
	private void toggleFragmentViews(boolean show) {
		// Toggle views of this fragment depending on
		// whether the map is shown or not.
		int arrowDrawableId = 0;
		if (show) {
			// If the map is shown, this Fragment's views should indicate
			// an option to hide it.
			arrowDrawableId = R.drawable.arrow_down_small;
		} else {
			arrowDrawableId = R.drawable.arrow_up_small;
		}
		// Set drawableLeft and drawableRight.
		toggleMapButton.setCompoundDrawablesWithIntrinsicBounds(arrowDrawableId, 0, arrowDrawableId, 0);
	}
}
