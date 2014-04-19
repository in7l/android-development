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
		if (distance >= 1000) {
			metric = "km";
			distance = distance / 1000;
		}
		String distanceRangeString = "< " + distance + metric;
		distanceTextView.setText(distanceRangeString);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int distance = progress;
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
	
}
