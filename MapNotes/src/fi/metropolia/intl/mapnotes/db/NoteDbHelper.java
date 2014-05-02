package fi.metropolia.intl.mapnotes.db;

import java.sql.Date;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import fi.metropolia.intl.mapnotes.db.NoteContract.*;
import fi.metropolia.intl.mapnotes.note.Note;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;

public class NoteDbHelper extends SQLiteOpenHelper {
	private SelectNotes selectNotesRunnable = new SelectNotes();

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
		LocationEntry.COLUMN_NAME_LOCATION_Y + INTEGER_TYPE + COMMA_SEP +
		" )";
    
    // SQL for deleting Location table.
    private static final String SQL_DELETE_LOCATIONS =
    	"DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;
    
    // Runnable for finding/selecting notes.
    private class SelectNotes implements Runnable {
    	private SQLiteDatabase db = null;

		@Override
		public void run() {
			if (db == null) {
				// Open readable database.
				db = getReadableDatabase();
			}
			
			findNotes();
			
			if (db != null) {
				// Close the database object.
				db.close();
				db = null;
			}
		}
		
		public ArrayList<Note> findNotes() {
			ArrayList<Note> notes = new ArrayList<Note>();
			
			// Create a querybuilder. Needed for joining two tables.
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(NoteEntry.TABLE_NAME +
				" LEFT OUTER JOIN " + LocationEntry.TABLE_NAME + " ON " +
				NoteEntry.COLUMN_NAME_LOCATION_ID + " = " + LocationEntry.COLUMN_FULL_ID);
			
			
			// List of columns that are needed from the database.
			String[] projection = {
				NoteEntry._ID,
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
					datetimeTimestamp = cursor.getInt(datetimeTsIndex);
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
				
				// Create a Note object.
				Note note = new Note(description, location, datetime, summary);
				// Add it to the resulting array list of Notes.
				notes.add(note);
				
				// Move to the next row of the database results.
				cursor.moveToNext();
			}
			
			return notes;
		}
    }
	
	public NoteDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
		// Create a new thread and start it. This will request the notes.
		// The results will be returned when they are ready.
		new Thread(selectNotesRunnable).start();
	}
}
