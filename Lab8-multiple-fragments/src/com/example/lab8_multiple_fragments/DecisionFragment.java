package com.example.lab8_multiple_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DecisionFragment extends Fragment implements OnClickListener {
	private DecisionMakeListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (DecisionMakeListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement DecisionMakeListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment.
		View fragmentView = inflater.inflate(R.layout.make_decision, container, false);
		
		// Register listener with the buttons.
		Button imprisonButton = (Button) fragmentView.findViewById(R.id.ImprisonButton);
		Button releaseButton = (Button) fragmentView.findViewById(R.id.ReleaseButton);
		imprisonButton.setOnClickListener(this);
		releaseButton.setOnClickListener(this);
		
		return fragmentView;
	}

	@Override
	public void onClick(View v) {
		boolean imprison = true;
		
		switch (v.getId()) {
			case R.id.ImprisonButton:
				imprison = true;
				break;
			case R.id.ReleaseButton:
				imprison = false;
				break;
		}
		
		mListener.setDecision(imprison);
	}
	
	
}
