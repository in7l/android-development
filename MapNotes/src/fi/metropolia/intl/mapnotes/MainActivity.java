package fi.metropolia.intl.mapnotes;

import java.util.ArrayList;
import java.util.Date;

import fi.metropolia.intl.mapnotes.db.NoteDbHelper;
import fi.metropolia.intl.mapnotes.map.*;
import fi.metropolia.intl.mapnotes.note.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.animator;
import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity implements ToggleMapListener, NoteListListener {
	public static final int HANDLER_MESSAGE_FIND_NOTES = 0;
	public static final int HANDLER_MESSAGE_SAVE_NOTE = 1;
	public static final int HANDLER_MESSAGE_DELETE_NOTE = 2;
	public static final int ACTIVITY_ADD_NOTE = 0;
	public static final int ACTIVITY_EDIT_NOTE = 1;
	
	private NoteDbHelper dbHelper;
	private FragmentManager fragmentManager;
	private View mapContainer;
	private ArrayList<Note> notes = null;
	private IdValueAdapter noteAdapter = null;
	
	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HANDLER_MESSAGE_FIND_NOTES:
					// Update the list of Note objects.
					notes = (ArrayList<Note>)msg.obj;
					Log.i("Note", "Found " + notes.size() + " notes.");
					// Update the data in the ListView.
					updateNoteList();
					break;
				case HANDLER_MESSAGE_SAVE_NOTE:
					// A note has been saved to the database.
					Note note = (Note)msg.obj;
					Log.i("Note", "Saved note with description: " +
						note.getDescriptionString());
					// Request the list of notes to be updated.
					requestNoteListUpdate();
					break;
				case HANDLER_MESSAGE_DELETE_NOTE:
					// A note has been deleted from the database.
					Long deletedNoteDatabaseId = (Long)msg.obj;
					Log.i("Note", "Deleted note with database id: " +
						deletedNoteDatabaseId);
					// Request the list of notes to be updated.
					requestNoteListUpdate();
					break;
				default:
					super.handleMessage(msg);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Store a reference to the fragment manager.
		fragmentManager = getFragmentManager();
		
		// Create a SQLiteOpenHelper.
		dbHelper = new NoteDbHelper(this, uiHandler);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// TODO: Enable this once Settings are needed.
		// getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case ACTIVITY_ADD_NOTE:
				if (resultCode == Activity.RESULT_OK) {
					Note note = (Note) data.getSerializableExtra(Note.NOTE_BUNDLE_KEY);
					if (note != null) {
						// A new note has been created.
						// Save it to the database.
						persistNote(note);
					}
				}
				break;
			case ACTIVITY_EDIT_NOTE:
				if (resultCode == Activity.RESULT_OK) {
					// Get the note that has been edited.
					Note note = (Note) data.getSerializableExtra(Note.NOTE_BUNDLE_KEY);
					if (note != null) {
						// Save the changes to the database.
						persistNote(note);
					}
				}
				break;
		}
	}

	@Override
	public void showMap(boolean show) {
		if (show) {
			// The map should be shown.
			Log.i("Map", "Show map");
			
			// Create the map fragment.
			Fragment mapFragment = new MapFragment();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			// Set animation for the transaction.
			transaction.setCustomAnimations(animator.fade_in, animator.fade_out);
			// Add the fragment to the MapContainer.
			transaction.add(R.id.MapContainer, mapFragment);
			// Commit the changes.
			transaction.commit();
			
			// Update the height and weight of the mapContainer.
			updateMapContainerProperties(show);
		} else {
			// The map should be hidden.
			Log.i("Map", "Hide map");
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			// Set animation for the transaction.
			transaction.setCustomAnimations(animator.fade_in, animator.fade_out);
			// Remove the fragment from MapContainer.
			transaction.remove(fragmentManager.findFragmentById(R.id.MapContainer));
			transaction.commit();
			// Update the height and weight of the mapContainer.
			updateMapContainerProperties(show);
		}
	}
	
	private void updateMapContainerProperties(boolean showMap) {
		if (mapContainer == null) {
			// Get a reference to the MapContainer item in the main layout.
			mapContainer = findViewById(R.id.MapContainer);
		}
		
		if (showMap) {
			// Set the layout weight of the MapContainer to 1.0.
			mapContainer.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		} else {
			// The mapContainer should be hidden.
			// Set the MapContainer layout weight to 0.
			mapContainer.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT, 0f));
		}
	}
	@Override
	public void addNote() {
		Log.i("Note", "Add note.");
		// Make a new intent to start a NoteEditActivity.
		Intent intent = new Intent(this, NoteEditActivity.class);
		// Start the activity.
		startActivityForResult(intent, ACTIVITY_ADD_NOTE);
	}

	@Override
	public void openNote(Note note) {
		Log.i("Note", "Opening note with description: " + note.getDescriptionString());
		// Make a new intent to start a NoteViewActivity.
		Intent intent = new Intent(this, NoteViewActivity.class);
		// Pass the serialized Note to that activity.
		intent.putExtra(Note.NOTE_BUNDLE_KEY, note);
		// Start the activity.
		startActivity(intent);
	}

	@Override
	public void editNote(Note note) {
		Log.i("Note", "Editing note with description: " + note.getDescriptionString());
		// Make a new intent to start a NoteEditActivity.
		Intent intent = new Intent(this, NoteEditActivity.class);
		// Pass the serialized Note to that activity.
		intent.putExtra(Note.NOTE_BUNDLE_KEY, note);
		// Start the activity.
		startActivityForResult(intent, ACTIVITY_EDIT_NOTE);
	}

	@Override
	public void deleteNote(Note note) {
		Log.i("Note", "Deleting note with description: " + note.getDescriptionString());
		// Request the Note with this database id to be deleted from the database.
		long databaseId = note.getDatabaseId();
		dbHelper.deleteNote(databaseId);
	}

	@Override
	public void requestNoteListUpdate() {
		// Request the list of notes to be updated.
		// The results will be received when this is done.
		dbHelper.findNotes();
		Log.i("Note", "Requested findNotes() from dbHelper.");
	}

	/**
	 * Saves a Note object to the database.
	 */
	public void persistNote(Note note) {
		dbHelper.saveNote(note);
	}
	
	public void updateNoteList() {
		Log.i("NoteList", "Updating note list.");
		// Log.i("StackTrace", String.valueOf(new Date().getTime()));
		// Log.e("StackTrace", Log.getStackTraceString(new Exception()));
		NoteListFragment noteListFragment =
			(NoteListFragment) fragmentManager.findFragmentById(
				R.id.NoteListContainer);
		noteListFragment.setNotes(notes);
	}
}
