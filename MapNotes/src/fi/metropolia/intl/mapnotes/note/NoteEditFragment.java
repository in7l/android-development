package fi.metropolia.intl.mapnotes.note;

import fi.metropolia.intl.mapnotes.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class NoteEditFragment extends Fragment {
	private Note note;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for editing a note.
		View v = inflater.inflate(R.layout.note_edit, container, false);
		
		// Get arguments passed to this fragment.
		Bundle bundle = getArguments();
		note = (Note)bundle.getSerializable(Note.NOTE_BUNDLE_KEY);
		
		// If a note object was passed when creating this Fragment
		// assign the note properties to their corresponding Views.
		if (note != null) {
			// Find EditText views.
			EditText summaryEditText = (EditText)v.findViewById(R.id.note_summary);
			EditText descriptionEditText = (EditText)v.findViewById(R.id.note_description);
			
			// Update the TextViews' texts based on the note properties.
			summaryEditText.setText(note.getSummaryString());
			descriptionEditText.setText(note.getDescriptionString());
		}
		
		return v;
	}
}
