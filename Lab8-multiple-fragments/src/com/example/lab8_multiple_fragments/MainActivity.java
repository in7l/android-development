package com.example.lab8_multiple_fragments;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends Activity implements DecisionRequestListener, DecisionMakeListener {
	private String requestMessage;
	private boolean decision;
	private FragmentManager fragmentManager = null;
	RequestDecisionFragment requestFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		requestMessage = getString(R.string.question) + " "
				+ getString(R.string.person_name) + "?";
		
		createRequestFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public String getRequestMessage() {
		return requestMessage;
	}

	@Override
	public void setDecision(boolean decision) {
		this.decision = decision;
		// When the decision is made, replace the fragments.
		// First get the new message to be displayed.
		String resultMessage;
		if (decision) {
			resultMessage = "Send " + getString(R.string.person_name) + " to prison.";
		}
		else {
			resultMessage = "Relase " + getString(R.string.person_name) + " from prison.";
		}
		requestFragment.setMessage(resultMessage);
		// Return to the previous state (the one before replacing the fragments).
		fragmentManager.popBackStackImmediate();
	}

	/**
	 * Helper function called when creating the activity.
	 */
	public void createRequestFragment() {
		// Get the fragment manager.
		fragmentManager = getFragmentManager();
		// Make a fragment transaction.
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		// Create a RequestDecisionFragment.
		requestFragment = new RequestDecisionFragment();
		requestFragment.setMessage(requestMessage);
		fragmentTransaction.add(R.id.fragment_container, requestFragment);
		fragmentTransaction.commit();		
	}

	@Override
	public void requestDecision() {
		// When the decision is requested, replace the fragments.
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		// Create the decision make fragment.
		DecisionFragment decisionFragment = new DecisionFragment();
		fragmentTransaction.replace(R.id.fragment_container, decisionFragment);
		// Store the previous fragment to the back stack.
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
}
