package fi.metropolia.intl.mapnotes;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NoteListFragment extends ListFragment {
	private NoteListListener mListener;
	private ArrayList<Note> notes;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Attempt to save a reference to the activity
		// which should implement NoteListener.
		try {
			mListener = (NoteListListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement NoteListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the note_list layout used by this fragment and return it.
		return inflater.inflate(R.layout.note_list, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create an ArrayList of Note objects.
		notes = new ArrayList<Note>();
		
		String longDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed interdum volutpat purus a vulputate. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin vulputate interdum tortor, a pulvinar massa malesuada a. Sed egestas mauris nunc, vel varius nisl sagittis eu. In elementum scelerisque tellus, ut sodales metus pharetra sed. Sed lorem nisl, mollis et commodo et, congue eget est. Aliquam commodo ultricies rhoncus. Ut lacus metus, rutrum hendrerit euismod tristique, pellentesque in urna. Vivamus dignissim, eros ac feugiat dictum, neque metus tempus felis, a pretium ligula quam sit amet dui. Nullam congue lorem vitae metus adipiscing consectetur.\n\nPhasellus in laoreet purus. Nunc ac dignissim mauris. Proin pharetra ipsum sed nunc venenatis elementum. Nunc luctus cursus sem ac condimentum. Proin et ante auctor, cursus libero sit amet, molestie nisl. Proin ac faucibus orci. Aenean lacinia erat tristique, tristique sapien ac, facilisis diam. Sed molestie justo a varius condimentum. In dapibus lacus in tincidunt luctus. In at dui id ante dignissim consequat ac in nulla. Pellentesque in augue ac odio malesuada accumsan. Proin elementum velit in mattis bibendum. Nam vitae imperdiet neque. Nulla eu purus velit.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Sed interdum volutpat purus a vulputate. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin vulputate interdum tortor, a pulvinar massa malesuada a. Sed egestas mauris nunc, vel varius nisl sagittis eu. In elementum scelerisque tellus, ut sodales metus pharetra sed. Sed lorem nisl, mollis et commodo et, congue eget est. Aliquam commodo ultricies rhoncus. Ut lacus metus, rutrum hendrerit euismod tristique, pellentesque in urna. Vivamus dignissim, eros ac feugiat dictum, neque metus tempus felis, a pretium ligula quam sit amet dui. Nullam congue lorem vitae metus adipiscing consectetur.\n\nPhasellus in laoreet purus. Nunc ac dignissim mauris. Proin pharetra ipsum sed nunc venenatis elementum. Nunc luctus cursus sem ac condimentum. Proin et ante auctor, cursus libero sit amet, molestie nisl. Proin ac faucibus orci. Aenean lacinia erat tristique, tristique sapien ac, facilisis diam. Sed molestie justo a varius condimentum. In dapibus lacus in tincidunt luctus. In at dui id ante dignissim consequat ac in nulla. Pellentesque in augue ac odio malesuada accumsan. Proin elementum velit in mattis bibendum. Nam vitae imperdiet neque. Nulla eu purus velit.";
		
		// Add a few test notes.
		notes.add(new Note(longDescription, null, null, "Summary1"));
		notes.add(new Note("Descr2", null, null, "Summary2"));
		notes.add(new Note("Descr3", null, null, null));
		
		// Create an adapter for filling in the note elements in the list.
		IdValueAdapter noteAdapter = new IdValueAdapter(getActivity(),
				R.layout.note_collapsed, Note.getNoteListAsIdValueMap(notes));
		setListAdapter(noteAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Get the description from the pressed item.
		Log.i("Note", "Pressed item on position: " + position);
		mListener.openNote(notes.get(position));
	}
	
}
