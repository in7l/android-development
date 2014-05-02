package fi.metropolia.intl.mapnotes.note;

import fi.metropolia.intl.mapnotes.R;
import fi.metropolia.intl.mapnotes.R.id;
import fi.metropolia.intl.mapnotes.R.layout;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

public class NoteViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_action_container);
		
		// Display an up/back arrow for the home action bar button.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Get the extras from the intent that started this activity.
		Bundle extras = getIntent().getExtras();
		// Get the Note object from the extras.
		Note note = (Note)extras.getSerializable(Note.NOTE_BUNDLE_KEY);
		// Get the summary from this Note.
		String noteSummary = note.getSummaryString();
		// If the note summary is not empty, update the activity title.
		if (noteSummary != "") {
			setTitle(noteSummary);
		}
		
		// Create a NoteViewFragment.
		NoteViewFragment fragment = new NoteViewFragment();
		// Pass the extras to that fragment.
		fragment.setArguments(extras);
		// Add the NoteViewFragment to its container in this activity's layout.
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.NoteActionContainer, fragment);
		fragmentTransaction.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	// Stop the current activity and go back to the previous one.
	    	onBackPressed();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
