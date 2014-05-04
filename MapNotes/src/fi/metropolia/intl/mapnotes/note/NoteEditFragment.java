package fi.metropolia.intl.mapnotes.note;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import fi.metropolia.intl.mapnotes.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class NoteEditFragment extends Fragment {
	private EditText summaryEditText;
	private EditText descriptionEditText;
	// The checkbox should be shown only when editing a Note.
	private CheckBox updateDatetimeCheckBox = null;
	// The checkbox should be unchecked by default when editing a Note.
	private CheckBox saveCurrentLocationCheckBox = null;
	private Note note;
	private NoteEditListener mListener;
	// A flag that marks if this fragment is used for creating or editing a Note.
	private boolean createNote = true;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Attempt to save a reference to the activity
		// which should implement NoteEditListener.
		try {
			mListener = (NoteEditListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement NoteEditListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for editing a note.
		View v = inflater.inflate(R.layout.note_edit, container, false);
		
		// This fragment has an options menu (action bar).
		setHasOptionsMenu(true);
		
		// Find EditText views.
		summaryEditText = (EditText)v.findViewById(R.id.note_summary);
		descriptionEditText = (EditText)v.findViewById(R.id.note_description);
		// Find a CheckBox view.
		saveCurrentLocationCheckBox = (CheckBox)v.findViewById(R.id.UseLocation);
		
		// Get arguments passed to this fragment.
		Bundle bundle = getArguments();
		if (bundle != null) {
			note = (Note)bundle.getSerializable(Note.NOTE_BUNDLE_KEY);
			// If there is a passed Note object, then this means
			// the current fragment is used for editing it,
			// instead of creating a new one.
			if (note != null) {
				createNote = false;
			}
		}
		
		// If a note object was passed when creating this Fragment,
		// i.e. if this is 'Edit Note' action rather than 'Add Note'.
		if (!createNote) {
			// Update the TextViews' texts based on the note properties.
			summaryEditText.setText(note.getSummaryString());
			descriptionEditText.setText(note.getDescriptionString());
			// Show the 'update datetime' checkbox.
			updateDatetimeCheckBox = (CheckBox) v.findViewById(R.id.UpdateDatetime);
			updateDatetimeCheckBox.setVisibility(View.VISIBLE);
			// The save current location checkbox should be unchecked when editing notes.
			saveCurrentLocationCheckBox.setChecked(false);
		}
		
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.note_edit_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				Log.i("Note", "Note save.");
				saveNoteFromViews();
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	public void saveNoteFromViews() {
		// Gather information from the Views.
		String summary = summaryEditText.getText().toString();
		String description = descriptionEditText.getText().toString();
		// Check if the user marked that the current location
		// should be used for this note.
		boolean useCurrentLocation = saveCurrentLocationCheckBox.isChecked();
		
		if (createNote) {
			// Create a new Note object.
			// Always use the current time when creating new notes.
			Date datetime = new Date();
			LatLng location = null;
			note = new Note(description, location, datetime, summary);
			// Mark whether the current location should be used for the note.
			note.setUseCurrentLocation(useCurrentLocation);
			mListener.createNote(note);
		} else {
			// If there is already a Note object, update its fields.
			note.setSummary(summary);
			note.setDescription(description);
			// Mark whether the current location should be used for the note.
			note.setUseCurrentLocation(useCurrentLocation);
			// If the user marked that the datetime of this Note should be updated.
			if (updateDatetimeCheckBox.isChecked()) {
				// Set the current time to the Note.
				Date datetime = new Date();
				note.setDatetime(datetime);
			}
			mListener.updateNote(note);
		}
	}
}
