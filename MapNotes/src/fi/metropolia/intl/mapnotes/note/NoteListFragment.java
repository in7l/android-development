package fi.metropolia.intl.mapnotes.note;

import java.util.ArrayList;
import java.util.Map;

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
	private ArrayList<Note> notes = null;
	private ActionMode.Callback mActionModeCallback = new NoteListActionModeCallback(this);
	private ActionMode mActionMode = null;
	private int actionModeItemIndex = -1;
	IdValueAdapter noteAdapter = null;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Request from the activity to update the list of notes.
		mListener.requestNoteListUpdate();

		// Set the background color of the selected item.
		getListView().setSelector(R.color.Beige);
		
		// Register an OnItemLongClickListener for the ListView.
		getListView().setOnItemLongClickListener(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Attempt to save a reference to the activity
		// which should implement NoteListListener.
		try {
			mListener = (NoteListListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement NoteListListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// This fragment has an options menu (action bar).
		setHasOptionsMenu(true);
		// Inflate the note_list layout used by this fragment and return it.
		return inflater.inflate(R.layout.note_list, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create an empty list of notes.
		notes = new ArrayList<Note>();
		
		// Create an adapter for filling in the note elements in the list.
		noteAdapter = new IdValueAdapter(getActivity(),
				R.layout.note_collapsed, Note.getNoteListAsIdValueMap(notes));
		setListAdapter(noteAdapter);
	}
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.note_list_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add:
				mListener.addNote(null);
				break;
			case R.id.clear_all:
				mListener.clearAllDatabaseData();
				break;
			default:
				return false;
		}
		
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Dismiss the contextual action bar if it was started.
		if (mActionMode != null) {
			mActionMode.finish();
		}

		// Get the position from the pressed item.
		// Log.i("Note", "Pressed item on position: " + position);
		
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
	
	public void setNotes(ArrayList<Note> n) {
		// Log.i("NoteList", "Setting " + n.size() + " notes.");
		notes = n;
		// Generate the ListView adapter data in another thread,
		// as that might be quite resource-consuming.
		(new Thread() {
			@Override
			public void run() {
				// Get an IdValueMap of the notes.
				final ArrayList<Map<Integer, String>> idValueMap = Note.getNoteListAsIdValueMap(notes);
				
				// Once creating the IdValueMap is done,
				// do the changes on the UI thread.
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// Notify about data change only once.
						noteAdapter.setNotifyOnChange(false);
						noteAdapter.clear();
						noteAdapter.addAll(idValueMap);
						noteAdapter.notifyDataSetChanged();
						// Log.i("NoteList", "Setting notes done.");
					}
				});
			}
		}).start();
	}
	
}
