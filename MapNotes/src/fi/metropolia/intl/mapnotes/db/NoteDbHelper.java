package fi.metropolia.intl.mapnotes.db;

import java.sql.Date;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import fi.metropolia.intl.mapnotes.MainActivity;
import fi.metropolia.intl.mapnotes.db.NoteContract.*;
import fi.metropolia.intl.mapnotes.note.Note;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NoteDbHelper extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "notes.db";

	private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    
    // SQL for creating Note table.
    private static final String SQL_CREATE_NOTES =
		"CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
		NoteEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
		NoteEntry.COLUMN_NAME_SUMMARY + TEXT_TYPE + COMMA_SEP +
		NoteEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
		NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP  + INTEGER_TYPE + COMMA_SEP +
		NoteEntry.COLUMN_NAME_LOCATION_ID + INTEGER_TYPE +
		" )";

    // SQL for deleting Note table.
    private static final String SQL_DELETE_NOTES =
    	"DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;

    // SQL for creating Location table.
    private static final String SQL_CREATE_LOCATIONS =
    	"CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
		LocationEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
		LocationEntry.COLUMN_NAME_LOCATION_X + INTEGER_TYPE + COMMA_SEP +
		LocationEntry.COLUMN_NAME_LOCATION_Y + INTEGER_TYPE +
		" )";
    
    // SQL for deleting Location table.
    private static final String SQL_DELETE_LOCATIONS =
    	"DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;
    
    // Runnable for finding/selecting notes.
    private class SelectNotes implements Runnable {
    	private SQLiteDatabase db = null;

		@Override
		public void run() {
			try {
				if (db == null) {
					// Open readable database.
					db = getReadableDatabase();
				}
				
				ArrayList<Note> notes = findNotes();
				// Obtain a message from the UI Handler.
				Message message = mHandler.obtainMessage(MainActivity.HANDLER_MESSAGE_FIND_NOTES);
				message.obj = notes;
				// Send the message to the main thread.
				mHandler.sendMessage(message);
			} catch (Exception e) {
				Log.e("DB", "Unable to SelectNotes. " +
						e.getMessage());
			} finally {
				// If a database connection was successfully opened.
				if (db != null) {
					// Close the database object.
					db.close();
					db = null;
				}
			}
		}
		
		private ArrayList<Note> findNotes() {
			ArrayList<Note> notes = new ArrayList<Note>();
			
			// Create a querybuilder. Needed for joining two tables.
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(NoteEntry.TABLE_NAME +
				" LEFT OUTER JOIN " + LocationEntry.TABLE_NAME + " ON " +
				NoteEntry.COLUMN_NAME_LOCATION_ID + " = " + LocationEntry.COLUMN_FULL_ID);
			
			
			// List of columns that are needed from the database.
			String[] projection = {
				NoteEntry.COLUMN_FULL_ID,
				NoteEntry.COLUMN_NAME_SUMMARY,
				NoteEntry.COLUMN_NAME_DESCRIPTION,
				NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP,
				LocationEntry.COLUMN_NAME_LOCATION_X,
				LocationEntry.COLUMN_NAME_LOCATION_Y
			};
			
			// Where clause.
			String selection = null;
			// Where clause placeholders.
			String[] selectionArgs = null;
			// Group by.
			String groupBy = null;
			// Having.
			String having = null;
			// Order by datetime in descending order (latest should appear first).
			String sortOrder = NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP + " DESC";
			// Limit to the number of returned rows.
			String limit = null;
			
			Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				groupBy, having, sortOrder, limit);
			
			// Move the cursor to the first result. If the return value is false
			// then there are no results.
			if (cursor.moveToFirst() == false) {
				// Return an empty array list.
				return notes;
			}
			
			// Go through all rows and create Note objects.
			while (!cursor.isAfterLast()) {
				String summary = null;
				String description = null;
				long datetimeTimestamp = -1;
				Date datetime = null;
				double locationX = -1;
				double locationY = -1;
				LatLng location = null;
				long databaseId = -1;
				
				// Get summary.
				int summaryIndex = cursor.getColumnIndexOrThrow(
					NoteEntry.COLUMN_NAME_SUMMARY);
				if (cursor.getType(summaryIndex) != Cursor.FIELD_TYPE_NULL) {
					summary = cursor.getString(summaryIndex);
				}
				
				// Get description.
				int descriptionIndex = cursor.getColumnIndexOrThrow(
					NoteEntry.COLUMN_NAME_DESCRIPTION);
				if (cursor.getType(descriptionIndex) != Cursor.FIELD_TYPE_NULL) {
					description = cursor.getString(descriptionIndex);
				}
				
				// Get datetime timestamp.
				int datetimeTsIndex = cursor.getColumnIndexOrThrow(
					NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP);
				if (cursor.getType(datetimeTsIndex) != Cursor.FIELD_TYPE_NULL) {
					datetimeTimestamp = cursor.getLong(datetimeTsIndex);
					datetime = new Date(datetimeTimestamp);
				}
				
				// Get location.
				int locationXIndex = cursor.getColumnIndexOrThrow(
					LocationEntry.COLUMN_NAME_LOCATION_X);
				if (cursor.getType(locationXIndex) != Cursor.FIELD_TYPE_NULL) {
					locationX = cursor.getDouble(locationXIndex);
				}
				int locationYIndex = cursor.getColumnIndexOrThrow(
					LocationEntry.COLUMN_NAME_LOCATION_Y);
				if (cursor.getType(locationYIndex) != Cursor.FIELD_TYPE_NULL) {
					locationY = cursor.getDouble(locationYIndex);
				}
				// If both x and y locations were fetched successfully.
				if (locationX != -1 && locationY != -1) {
					location = new LatLng(locationX, locationY);
				}
				
				// Get Note database id.
				int databaseIdIndex = cursor.getColumnIndexOrThrow(NoteEntry._ID);
				if (cursor.getType(databaseIdIndex) != Cursor.FIELD_TYPE_NULL) {
					databaseId = cursor.getLong(databaseIdIndex);
				}
				// Create a Note object.
				Note note = new Note(description, location, datetime,
						summary, databaseId);
				// Add it to the resulting array list of Notes.
				notes.add(note);
				
				// Move to the next row of the database results.
				cursor.moveToNext();
			}
			
			return notes;
		}
    }
    
    private class SaveNote implements Runnable {
    	private SQLiteDatabase db = null;
    	private Note note;
    	
    	public SaveNote(Note note) {
    		// Store a reference to the note to be saved.
    		this.note = note;
    	}

		@Override
		public void run() {
			try {
				if (db == null) {
					// Open writable database.
					db = getWritableDatabase();
				}
				
				boolean success = saveNoteToDatabase();
				// If the note was saved successfully.
				if (success) {
					// Obtain a message from the UI Handler.
					Message message = mHandler.obtainMessage(MainActivity.HANDLER_MESSAGE_SAVE_NOTE);
					message.obj = note;
					// Send the message to the main thread.
					mHandler.sendMessage(message);
				}
			} catch (Exception e) {
				Log.e("DB", "Unable to SaveNote. " +
						e.getMessage());
			} finally {
				// If a database connection was successfully opened.
				if (db != null) {
					// Close the database object.
					db.close();
					db = null;
				}
			}
		}
		
		private boolean saveNoteToDatabase() {
			// Create a new map of values, where column names are the keys
			ContentValues values = new ContentValues();
			values.put(NoteEntry.COLUMN_NAME_SUMMARY, note.getSummary());
			values.put(NoteEntry.COLUMN_NAME_DESCRIPTION, note.getDescription());
			values.put(NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP, note.getDatetimeTimestamp());
			
			long databaseId = note.getDatabaseId();
			// If this is an old Note.
			if (databaseId != -1) {
				// Update the row in the database, instead of creating a new one.
				String selection = NoteEntry._ID + " = ?";
				String selectionArgs[] = { String.valueOf(databaseId) };
				
				int count = db.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);
				if (count > 0) {
					Log.i("DB", "Updated note with id " + databaseId);
					return true;
				} else {
					Log.e("DB", "Failed to update note with id " + databaseId);
					return false;
				}
			} else {
				// This is a new Note.
				// Insert the new row, returning the primary key value of the new row
				long newRowId;
				newRowId = db.insert(NoteEntry.TABLE_NAME, null, values);
				
				if (newRowId != -1) {
					// Assign the databaseId to the Note object.
					note.setDatabaseId(newRowId);
					Log.i("DB", "Saved note with id " + note.getDatabaseId());
					return true;
				}
				else {
					Log.e("DB", "Failed to save a note.");
					return false;	
				}
			}
		}
    }
    
    private class DeleteNote implements Runnable {
    	private SQLiteDatabase db = null;
    	private long databaseId;
    	
    	public DeleteNote(long noteDatabaseId) {
    		// Store the database id of the note to be deleted.
    		databaseId = noteDatabaseId;
    	}

		@Override
		public void run() {
			try {
				if (db == null) {
					// Open writable database.
					db = getWritableDatabase();
				}
				
				boolean success = deleteNoteFromDatabase();
				// If the note was deleted successfully.
				if (success) {
					// Obtain a message from the UI Handler.
					Message message = mHandler.obtainMessage(MainActivity.HANDLER_MESSAGE_DELETE_NOTE);
					message.obj = new Long(databaseId);
					// Send the message to the main thread.
					mHandler.sendMessage(message);
				}
			} catch (Exception e) {
				Log.e("DB", "Unable to DeleteNote. " +
						e.getMessage());
			} finally {
				// If a database connection was successfully opened.
				if (db != null) {
					// Close the database object.
					db.close();
					db = null;
				}
			}
		}
		
		private boolean deleteNoteFromDatabase() {
			// Attempt to delete this Note's row from the database.
			String selection = NoteEntry._ID + " = ?";
			String selectionArgs[] = { String.valueOf(databaseId) };
			
			int count =db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
			
			if (count > 0) {
				// Some row was deleted.
				Log.i("DB", "Deleted note with id " + databaseId);
				return true;
			} else {
				Log.e("DB", "Failed to delete note with id " + databaseId);
				return false;
			}
		}
    }
    
    private SelectNotes selectNotesRunnable;
    private Handler mHandler;
	
	public NoteDbHelper(Context context, Handler handler) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// Store a reference to the Handler for Thread messages.
		mHandler = handler;
		selectNotesRunnable = new SelectNotes();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_NOTES);
		db.execSQL(SQL_CREATE_LOCATIONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This will discard the data.
		// TODO: Whenever this is needed, figure out how to upgrade properly.
        db.execSQL(SQL_DELETE_NOTES);
        db.execSQL(SQL_DELETE_LOCATIONS);
        onCreate(db);
	}
	
	public void findNotes() {
		// Log.i("StackTrace", String.valueOf(new java.util.Date().getTime()));
		// Log.e("StackTrace", Log.getStackTraceString(new Exception()));
		// Create a new thread and start it. This will request the notes.
		// The results will be returned when they are ready.
		new Thread(selectNotesRunnable).start();
	}
	
	public void saveNote(Note note) {
		// Start a new thread for saving the Note to database.
		new Thread(new SaveNote(note)).start();
	}
	
	public void deleteNote(long databaseId) {
		// Start a new thread that will attempt to delete the note with
		// the specified id from the database.
		new Thread(new DeleteNote(databaseId)).start();
	}
}
