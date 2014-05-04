package fi.metropolia.intl.mapnotes.note;

import com.google.android.gms.internal.m;

import fi.metropolia.intl.mapnotes.R;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class NoteEditActivity extends Activity implements NoteEditListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_action_container);
		
		// Display an up/back arrow for the home action bar button.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Get the intent that started this activity.
		Intent intent = getIntent();
		// Get the Note object from the extras.
		Note note = (Note)intent.getSerializableExtra(Note.NOTE_BUNDLE_KEY);
		if (note != null) {
			// Get the summary from this Note.
			String noteSummary = note.getSummaryString();
			// If the note summary is not empty, update the activity title.
			if (noteSummary != "") {
				setTitle("Edit: " + noteSummary);
			}
		}
		else {
			// No Note object passed in the extras.
			setTitle("Create note.");
		}
		
		// Create a NoteEditFragment.
		NoteEditFragment fragment = new NoteEditFragment();
		Bundle extras = intent.getExtras();
		// If some extras were added.
		if (extras != null) {
			// Pass the extras to that fragment.
			fragment.setArguments(extras);
		}
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

	@Override
	public void updateNote(Note note) {
		Log.i("Note", "Update note.");
		Intent resultIntent = new Intent();
		// Add the edited Note object to the extras.
		resultIntent.putExtra(Note.NOTE_BUNDLE_KEY, note);
		// Pass the result to the activity that started this one.
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	@Override
	public void createNote(Note note) {
		Log.i("Note", "Create note.");
		Intent resultIntent = new Intent();
		// Add the created Note object to the extras.
		resultIntent.putExtra(Note.NOTE_BUNDLE_KEY, note);
		// Pass the result to the activity that started this one.
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}
