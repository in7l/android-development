package fi.metropolia.intl.mapnotes.note;

import java.util.ArrayList;

import fi.metropolia.intl.mapnotes.IdValueAdapter;
import fi.metropolia.intl.mapnotes.R;
import fi.metropolia.intl.mapnotes.R.layout;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnLongClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class NoteListFragment extends ListFragment implements OnItemLongClickListener,
		NoteListActionModeCallbackListener {
	private NoteListListener mListener;
	private ArrayList<Note> notes;
	private ActionMode.Callback mActionModeCallback = new NoteListActionModeCallback(this);
	private ActionMode mActionMode = null;
	private int actionModeItemIndex = -1;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
		// Set the background color of the selected item.
		getListView().setSelector(R.color.Beige);
		
		// Register an OnItemLongClickListener for the ListView.
		getListView().setOnItemLongClickListener(this);
	}

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
		// Dismiss the contextual action bar if it was started.
		if (mActionMode != null) {
			mActionMode.finish();
		}

		// Get the position from the pressed item.
		Log.i("Note", "Pressed item on position: " + position);
		
		mListener.openNote(notes.get(position));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// Log.i("Note", "onLongClick");
		if (mActionMode != null) {
			return false;
		}
		
		// Mark for which item the CAB will be started.
		actionModeItemIndex = position;
		// Start the Contextual Action Bar using mActionModeCallback.
        mActionMode = getActivity().startActionMode(mActionModeCallback);
        getListView().setItemChecked(position, true);
        return true;
	}
	
	@Override
	public void editNote() {
		// This method is called from contextual action mode.
		// Get the position of the last item for which contextual action mode
		// was started.
		// Make sure there is a correctly saved index of the item for which
		// contextual action mode was started.
		if (actionModeItemIndex != -1) {
			Note note = notes.get(actionModeItemIndex);
			// Mark that the action for this item will be performed (only once).
			actionModeItemIndex = -1;
			mListener.editNote(note);
		}
	}

	@Override
	public void deleteNote() {
		// This method is called from contextual action mode.
		// Get the position of the last item for which contextual action mode
		// was started.
		// Make sure there is a correctly saved index of the item for which
		// contextual action mode was started.
		if (actionModeItemIndex != -1) {
			Note note = notes.get(actionModeItemIndex);
			// Mark that the action for this item will be performed (only once).
			actionModeItemIndex = -1;
			mListener.deleteNote(note);
		}
	}

	@Override
	public void destroyActionMode() {
		mActionMode = null;
	}
	
}
