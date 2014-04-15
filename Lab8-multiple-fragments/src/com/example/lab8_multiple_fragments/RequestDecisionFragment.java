package com.example.lab8_multiple_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RequestDecisionFragment extends Fragment implements OnClickListener {
	private DecisionRequestListener mListener;
	private String message = "";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (DecisionRequestListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement DecisionRequestListener");
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
		View fragmentView = inflater.inflate(R.layout.request_decision, container, false);
		// Get the text view for the request message.
		TextView requestMessageTextView = (TextView)fragmentView.findViewById(R.id.requestMessage);
		requestMessageTextView.setText(message);
		
		// Register listener with the button.
		Button requestDecisionButton = (Button) fragmentView.findViewById(R.id.RequestDecisionButton);
		requestDecisionButton.setOnClickListener(this);
		
		return fragmentView;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.RequestDecisionButton) {
			mListener.requestDecision();
		}
	}
}
