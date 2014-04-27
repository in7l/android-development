package fi.metropolia.intl.mapnotes;

import java.util.Map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoteViewFragment extends Fragment {
	private Note note;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for viewing a note.
		View v = inflater.inflate(R.layout.note_view, container, false);
		
		// Get arguments passed to this fragment.
		Bundle bundle = getArguments();
		note = (Note)bundle.getSerializable(Note.NOTE_BUNDLE_KEY);
		
		// If a note object was passed when creating this Fragment
		// assign the note properties to their corresponding Views.
		if (note != null) {
			// Find TextViews.
			TextView datetimeTextView = (TextView)v.findViewById(R.id.note_datetime);
			TextView descriptionTextView = (TextView)v.findViewById(R.id.note_description);
			
			// Update the TextViews' texts based on the note properties.
			datetimeTextView.setText(note.getDatetimeString());
			descriptionTextView.setText(note.getDescriptionString());
		}
		
		return v;
	}
	
	
	
}
