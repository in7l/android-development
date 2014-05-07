package fi.metropolia.intl.mapnotes;

import fi.metropolia.intl.mapnotes.note.NoteListListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DistanceSelectorFragment extends Fragment implements OnSeekBarChangeListener {
	// The exponent has been obtained by the following equation:
	// (maxSeekBarValue - 1) ^ x = maxDistance - minDistance
	public final static double DATE_SELECTOR_EXPONENT = 2.3546;
	private TextView distanceTextView;
	private SeekBar distanceSeekBar;
	private DistanceSelectorListener mListener;
	private int mDistance = -1;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Attempt to save a reference to the activity
		// which should implement DistanceSelectorListener.
		try {
			mListener = (DistanceSelectorListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DistanceSelectorListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the distance_selector layout used by this fragment.
		View v = inflater.inflate(R.layout.distance_selector, container, false);
		// Get a reference to the SeekBar and register a listener for it.
		distanceSeekBar = (SeekBar)v.findViewById(R.id.DistanceSeekBar);
		distanceSeekBar.setOnSeekBarChangeListener(this);
		// Get a reference to the distance TextView, which will get its distance value updated.
		distanceTextView = (TextView)v.findViewById(R.id.DistanceTextView);
		// Update the initial distance.
		setDistanceFromSeekBar(distanceSeekBar.getProgress());
		// Notify the listener about the initial distance.
		mListener.setDistance(mDistance);
		
		// Return the inflated layout.
		return v;
	}
	
	private void setDistanceRange(int distance) {
		String metric = "m";
		String distanceString = String.valueOf(distance);
		
		// If the distance is more at least 1km
		// some different notation will be used.s
		if (distance >= 1000) {
			metric = "km";
			double distanceInKm = distance / 1000.0;
			distanceString = String.format("%.1f", distanceInKm);
		}
		String distanceRangeString = "< " + distanceString + metric;
		
		if (distance < 0) {
			// Negative distance signifies infinity,
			// i.e. no distance limitation.
			distanceRangeString = getString(R.string.infinity);
		}
		
		distanceTextView.setText(distanceRangeString);
		
		// Store the last displayed distance.
		mDistance = distance;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		setDistanceFromSeekBar(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// Execute time-consuming business logic
		// once the user has stopped dragging the SeekBar.
		Log.i("DistanceSelector", "register changes");
		// Notify the DistanceSelectorListener
		// that the distance has changed.
		mListener.setDistance(mDistance);
	}
	
	private int calculateProgressDistance(int progress) {
		int distance = (int) Math.pow(progress, DATE_SELECTOR_EXPONENT);
		// The distance can be minimum 15.
		distance += 15;
		// Round up to multiples of 5.
		distance = ((int)Math.ceil(distance / 5.0)) * 5;
		
		return distance;
	}
	
	public void updateDistanceFromSeekBar() {
		int progress = distanceSeekBar.getProgress();
		setDistanceFromSeekBar(progress);
	}
	
	private void setDistanceFromSeekBar(int progress) {
		int distance = calculateProgressDistance(progress);
		
		// If the SeekBar has been set to the maximum
		// assume no distance limits should be set.
		if (progress == distanceSeekBar.getMax()) {
			distance = -1;
		}
		setDistanceRange(distance);
	}
	
}
