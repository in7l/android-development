package fi.metropolia.intl.mapnotes.note;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import fi.metropolia.intl.mapnotes.MainActivity;
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
	private static final String STATE_NOTE = "note";
	private static final String STATE_CREATE_NOTE = "create_note";
	private static final String STATE_NOTE_LOCATION = "note_location";
	
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
	// The note location if a specific one was selected before navigating to this fragment.
	private LatLng noteLocation;

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
		updateDatetimeCheckBox = (CheckBox) v.findViewById(R.id.UpdateDatetime);
		
		if (savedInstanceState != null) {
			createNote = savedInstanceState.getBoolean(STATE_CREATE_NOTE, false);
			note = (Note) savedInstanceState.getSerializable(STATE_NOTE);
			noteLocation = savedInstanceState.getParcelable(STATE_NOTE_LOCATION);
		}
		else {
			
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
				
				// Attempt to get location for this note.
				noteLocation = bundle.getParcelable(MainActivity.NOTE_LOCATION_BUNDLE_KEY);
			}
			
			// If a note object was passed when creating this Fragment,
			// i.e. if this is 'Edit Note' action rather than 'Add Note'.
			if (!createNote) {
				// Update the TextViews' texts based on the note properties.
				summaryEditText.setText(note.getSummaryString());
				descriptionEditText.setText(note.getDescriptionString());
				// The save current location checkbox should be unchecked when editing notes.
				saveCurrentLocationCheckBox.setChecked(false);
			}
			else {
				// This is a new node.
				// Do not show the 'update datetime' checkbox.
				updateDatetimeCheckBox.setVisibility(View.GONE);
				// If a location was specified for this note,
				// do not show the save current location checkbox.
				if (noteLocation != null) {
					saveCurrentLocationCheckBox.setVisibility(View.GONE);
				}
			}
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
				// Log.i("Note", "Note save.");
				saveNoteFromViews();
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_CREATE_NOTE, createNote);
		outState.putSerializable(STATE_NOTE, note);
		if (noteLocation != null) {
			outState.putParcelable(STATE_NOTE_LOCATION, noteLocation);
		}
		
		super.onSaveInstanceState(outState);
	}

	public void saveNoteFromViews() {
		// Gather information from the Views.
		String summary = summaryEditText.getText().toString();
		if (summary.isEmpty()) {
			summary = null;
		}
		String description = descriptionEditText.getText().toString();
		if (description.isEmpty()) {
			description = null;
		}
		// Check if the user marked that the current location
		// should be used for this note.
		boolean useCurrentLocation = saveCurrentLocationCheckBox.isChecked();
		
		if (createNote) {
			// Create a new Note object.
			// Always use the current time when creating new notes.
			Date datetime = new Date();
			LatLng location = null;
			
			note = new Note(description, location, datetime, summary);
			
			// If this note should be created at a specific location (not the current one).
			if (noteLocation != null) {
				note.setLocation(noteLocation);
			}
			// If the note does not have a specified location.
			else {
				// Mark whether the current location should be used for the note.
				note.setUseCurrentLocation(useCurrentLocation);
			}
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
