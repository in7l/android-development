package fi.metropolia.intl.mapnotes.note;

import fi.metropolia.intl.mapnotes.R;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class NoteListActionModeCallback implements ActionMode.Callback {
	private NoteListActionModeCallbackListener mListener;
	
	public NoteListActionModeCallback(NoteListActionModeCallbackListener listener) {
		// Register a listener for this callback.
		// This will help in destroying the action mode and in determining
		// what action has been selected. This is necessary,
		// because the current class is not implemented as an inner class.
		mListener = listener;
	}
	
	// Called when the action mode is created; startActionMode() was called
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		// Inflate a menu resource providing context menu items
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.note_context_menu, menu);
		return true;
	}
	
	// Called each time the action mode is shown. Always called after onCreateActionMode, but
	// may be called multiple times if the mode is invalidated.
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false; // Return false if nothing is done
	}
	
	// Called when the user selects a contextual menu item
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit:
			// Log.i("NoteContext", "Edit note.");
			mListener.editNote();
			mode.finish(); // Action picked, so close the CAB
			return true;
			case R.id.delete:
			// Log.i("NoteContext", "Delete note.");
			mListener.deleteNote();
			mode.finish(); // Action picked, so close the CAB
			return true;
			default:
			return false;
		}
	}
	
	// Called when the user exits the action mode
	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mListener.destroyActionMode();
	}
}
