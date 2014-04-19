package fi.metropolia.intl.mapnotes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DistanceSelectorFragment extends Fragment implements OnSeekBarChangeListener {
	TextView distanceTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the distance_selector layout used by this fragment.
		View v = inflater.inflate(R.layout.distance_selector, container, false);
		// Get a reference to the distance TextView, which will get its distance value updated.
		distanceTextView = (TextView)v.findViewById(R.id.DistanceTextView);
		// Get a reference to the SeekBar and register a listener for it.
		SeekBar distanceSeekBar = (SeekBar)v.findViewById(R.id.DistanceSeekBar);
		distanceSeekBar.setOnSeekBarChangeListener(this);
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
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int distance = calculateProgressDistance(progress);
		
		// If the SeekBar has been set to the maximum
		// assume no distance limits should be set.
		if (progress == 1000) {
			distance = -1;
		}
		setDistanceRange(distance);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	public int calculateProgressDistance(int progress) {
		int distance = (int) Math.pow(progress, 1.5);
		// Round up to multiples of 5.
		distance = ((int)Math.ceil(distance / 5.0)) * 5;
		// The distance can be minimum 15.
		distance += 15;
		
		return distance;
	}
	
}
