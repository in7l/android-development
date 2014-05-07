package fi.metropolia.intl.mapnotes.db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.android.gms.internal.ig;
import com.google.android.gms.maps.model.LatLng;

import fi.metropolia.intl.mapnotes.MainActivity;
import fi.metropolia.intl.mapnotes.db.NoteContract.*;
import fi.metropolia.intl.mapnotes.note.Note;
import android.R.integer;
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

	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "notes.db";

	private static final String INTEGER_TYPE = " INTEGER";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	// SQL for creating Note table.
	private static final String SQL_CREATE_NOTES = "CREATE TABLE "
			+ NoteEntry.TABLE_NAME + " (" + NoteEntry._ID + INTEGER_TYPE
			+ " PRIMARY KEY" + COMMA_SEP + NoteEntry.COLUMN_NAME_SUMMARY
			+ TEXT_TYPE + COMMA_SEP + NoteEntry.COLUMN_NAME_DESCRIPTION
			+ TEXT_TYPE + COMMA_SEP + NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP
			+ INTEGER_TYPE + COMMA_SEP + NoteEntry.COLUMN_NAME_LOCATION_ID
			+ INTEGER_TYPE + " )";

	// SQL for deleting Note table.
	private static final String SQL_DELETE_NOTES = "DROP TABLE IF EXISTS "
			+ NoteEntry.TABLE_NAME;

	// SQL for creating Location table.
	private static final String SQL_CREATE_LOCATIONS = "CREATE TABLE "
			+ LocationEntry.TABLE_NAME + " (" + LocationEntry._ID
			+ INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP
			+ LocationEntry.COLUMN_NAME_LOCATION_X + INTEGER_TYPE + COMMA_SEP
			+ LocationEntry.COLUMN_NAME_LOCATION_Y + INTEGER_TYPE + " )";

	// SQL for deleting Location table.
	private static final String SQL_DELETE_LOCATIONS = "DROP TABLE IF EXISTS "
			+ LocationEntry.TABLE_NAME;
	
	public static final int DELETE_ALL_ID = -2;

	// Runnable for finding/selecting notes.
	private class SelectNotes implements Runnable {
		private SQLiteDatabase db = null;
		private int distanceInMeters = -1;
		private LatLng currentLocation;

		public SelectNotes(int distanceInMeters, LatLng currentLocation) {
			this.distanceInMeters = distanceInMeters;
			this.currentLocation = currentLocation;
		}

		@Override
		public void run() {
			try {
				if (db == null) {
					// Open readable database.
					db = getReadableDatabase();
				}

				ArrayList<Note> notes = findNotes();
				// If this thread does not need to be interrupted earlier than expected.
				if (notes != null) {
					// Obtain a message from the UI Handler.
					Message message = mHandler
							.obtainMessage(MainActivity.HANDLER_MESSAGE_FIND_NOTES);
					message.obj = notes;
					// Send the message to the main thread.
					mHandler.sendMessage(message);	
				}
			} catch (Exception e) {
				Log.e("DB", "Unable to SelectNotes. " + e.getMessage(), e);
			} finally {
				markRunnableFinished(this);
			}
		}

		private ArrayList<Note> findNotes() throws InterruptedException {
			ArrayList<Note> notes = new ArrayList<Note>();

			// Create a querybuilder. Needed for joining two tables.
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(NoteEntry.TABLE_NAME + " LEFT OUTER JOIN "
					+ LocationEntry.TABLE_NAME + " ON "
					+ NoteEntry.COLUMN_NAME_LOCATION_ID + " = "
					+ LocationEntry.COLUMN_FULL_ID);

			// List of columns that are needed from the database.
			String[] projection = { NoteEntry.COLUMN_FULL_ID,
					NoteEntry.COLUMN_NAME_SUMMARY,
					NoteEntry.COLUMN_NAME_DESCRIPTION,
					NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP,
					LocationEntry.COLUMN_FULL_ID + " AS LOCATION_ID",
					LocationEntry.COLUMN_NAME_LOCATION_X,
					LocationEntry.COLUMN_NAME_LOCATION_Y };

			// Where clause.
			String selection = null;
			// Where clause placeholders.
			String[] selectionArgs = null;
			// Group by.
			String groupBy = null;
			// Having.
			String having = null;
			// Order by datetime in descending order (latest should appear
			// first).
			String sortOrder = NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP
					+ " DESC";
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
			do {
				// If it was requested that the database helper be closed.
				if (closeRequested) {
					// Stop finding notes.
					return null;
				}
				
				// First get the location to verify if this note is within
				// range.
				double locationX = -1;
				double locationY = -1;
				LatLng noteLocation = null;
				float distanceToCurrentLocation = -1;
				long locationDatabaseId = -1;

				int locationXIndex = cursor
						.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_X);
				if (cursor.getType(locationXIndex) != Cursor.FIELD_TYPE_NULL) {
					locationX = cursor.getDouble(locationXIndex);
				}
				int locationYIndex = cursor
						.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_Y);
				if (cursor.getType(locationYIndex) != Cursor.FIELD_TYPE_NULL) {
					locationY = cursor.getDouble(locationYIndex);
				}
				// If both x and y locations were fetched successfully.
				if (locationX != -1 && locationY != -1) {
					noteLocation = new LatLng(locationX, locationY);
				}

				// Attempt to get the location database id.
				int locationDatabaseIdIndex = cursor
						.getColumnIndexOrThrow("LOCATION_ID");
				if (cursor.getType(locationDatabaseIdIndex) != Cursor.FIELD_TYPE_NULL) {
					locationDatabaseId = cursor
							.getLong(locationDatabaseIdIndex);
					// If the location database id appears to be valid
					// but no noteLocation was fetched (perhaps due to some
					// invalid data).
					if (locationDatabaseId != -1 && noteLocation == null) {
						Log.e("DB", "Found location database id "
								+ locationDatabaseId
								+ " referring to an invalid location.");
						// Mark that the location database id should be -1.
						locationDatabaseId = -1;
					}
				}

				// If both the current location and the note location are known.
				if (currentLocation != null && noteLocation != null) {
					// Calculate the distance between the current location
					// and the location of the note.
					float[] results = new float[1];
					Location.distanceBetween(currentLocation.latitude,
							currentLocation.longitude, noteLocation.latitude,
							noteLocation.longitude, results);
					distanceToCurrentLocation = results[0];

					Log.i("DB", "Found a note whose distance from "
							+ "the current location is "
							+ distanceToCurrentLocation);
				}

				// If there is a distance filter active.
				if (distanceInMeters != -1) {
					// If the distance to the current location cannot be
					// determined.
					// OR
					// If the distance to the current location is greater than
					// the distance filter limit.
					if (distanceToCurrentLocation == -1
							|| distanceToCurrentLocation >= distanceInMeters) {
						// This note should not be shown.
						continue;
					}
				}

				String summary = null;
				String description = null;
				long datetimeTimestamp = -1;
				Date datetime = null;
				long databaseId = -1;

				// Get summary.
				int summaryIndex = cursor
						.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_SUMMARY);
				if (cursor.getType(summaryIndex) != Cursor.FIELD_TYPE_NULL) {
					summary = cursor.getString(summaryIndex);
				}

				// Get description.
				int descriptionIndex = cursor
						.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_DESCRIPTION);
				if (cursor.getType(descriptionIndex) != Cursor.FIELD_TYPE_NULL) {
					description = cursor.getString(descriptionIndex);
				}

				// Get datetime timestamp.
				int datetimeTsIndex = cursor
						.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP);
				if (cursor.getType(datetimeTsIndex) != Cursor.FIELD_TYPE_NULL) {
					datetimeTimestamp = cursor.getLong(datetimeTsIndex);
					datetime = new Date(datetimeTimestamp);
				}

				// Get Note database id.
				int databaseIdIndex = cursor
						.getColumnIndexOrThrow(NoteEntry._ID);
				if (cursor.getType(databaseIdIndex) != Cursor.FIELD_TYPE_NULL) {
					databaseId = cursor.getLong(databaseIdIndex);
				}
				// Create a Note object.
				Note note = new Note(description, noteLocation, datetime,
						summary, databaseId, locationDatabaseId);
				// Set the calculated distance to the current location.
				note.setDistanceToCurrentLocation(distanceToCurrentLocation);
				// Add it to the resulting array list of Notes.
				notes.add(note);
			} while (cursor.moveToNext());

			cursor.close();

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
					Message message = mHandler
							.obtainMessage(MainActivity.HANDLER_MESSAGE_SAVE_NOTE);
					message.obj = note;
					// Send the message to the main thread.
					mHandler.sendMessage(message);
				}
			} catch (Exception e) {
				Log.e("DB", "Unable to SaveNote. " + e.getMessage());
			} finally {
				markRunnableFinished(this);
			}
		}

		private boolean saveNoteToDatabase() {
			LocationDatabaseHelper locationDbHelper = null;

			// Create a new map of values, where column names are the keys
			ContentValues values = new ContentValues();
			values.put(NoteEntry.COLUMN_NAME_SUMMARY, note.getSummary());
			values.put(NoteEntry.COLUMN_NAME_DESCRIPTION, note.getDescription());
			values.put(NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP,
					note.getDatetimeTimestamp());

			// Check if this note specifies a location.
			LatLng noteLocation = note.getLocation();
			if (noteLocation != null) {
				// The note specifies a location.
				// Check if there is some known location database id.
				if (note.getLocationDatabaseId() == -1) {
					// No known location database id stored in this Note.

					// Create a helper object for working with location data in
					// database.
					locationDbHelper = new LocationDatabaseHelper(db,
							noteLocation);
					// Check if such location exists in the database.
					long locationDatabaseId = locationDbHelper
							.findLocationFromDatabase();
					// If no such location exists in the database.
					if (locationDatabaseId == -1) {
						// Save it to db.
						locationDatabaseId = locationDbHelper
								.saveLocationToDatabase();
					}

					// At this point if the location is known to be saved
					// successfully to db
					if (locationDatabaseId != -1) {
						Log.i("DB", "Found/created location with database id "
								+ locationDatabaseId);
						// add a reference to it in the note table.
						values.put(NoteEntry.COLUMN_NAME_LOCATION_ID,
								locationDatabaseId);
						// Store the location database id in the Note.
						note.setLocationDatabaseId(locationDatabaseId);
					} else {
						Log.e("DB", "Failed to save location to database.");
					}
				}
			}

			long databaseId = note.getDatabaseId();
			// If this is an old Note.
			if (databaseId != -1) {
				// Update the row in the database, instead of creating a new
				// one.
				String selection = NoteEntry._ID + " = ?";
				String selectionArgs[] = { String.valueOf(databaseId) };

				int count = db.update(NoteEntry.TABLE_NAME, values, selection,
						selectionArgs);
				if (count > 0) {
					Log.i("DB", "Updated note with id " + databaseId);
					return true;
				} else {
					Log.e("DB", "Failed to update note with id " + databaseId);
					return false;
				}
			} else {
				// This is a new Note.
				// Insert the new row, returning the primary key value of the
				// new row
				long newRowId;
				newRowId = db.insert(NoteEntry.TABLE_NAME, null, values);

				if (newRowId != -1) {
					// Assign the databaseId to the Note object.
					note.setDatabaseId(newRowId);
					Log.i("DB", "Saved note with id " + note.getDatabaseId());
					return true;
				} else {
					Log.e("DB", "Failed to save note.");

					// If some database operation was performed for this Note's
					// location.
					if (locationDbHelper != null) {
						// If the location was stored in the database but there
						// are
						// no notes referring to it.
						if (locationDbHelper.findNumberOfNotesUsingLocation() == 0) {
							// Delete the location from the database.
							boolean deleted = locationDbHelper
									.deleteLocationFromDatabase();
							if (deleted) {
								Log.i("DB",
										"Deleted unused location from database.");
							} else {
								Log.i("DB",
										"Failed to deleted unused location from database.");
							}
						}
					}
					return false;
				}
			}
		}
	}

	private class DeleteNote implements Runnable {
		private SQLiteDatabase db = null;
		private long databaseId;
		private long locationDatabaseId;

		public DeleteNote(long noteDatabaseId) {
			this(noteDatabaseId, -1);
		}

		public DeleteNote(long noteDatabaseId, long locationDatabaseId) {
			// Store the database id of the note to be deleted.
			databaseId = noteDatabaseId;
			// Store the location database id of the note's location.
			this.locationDatabaseId = locationDatabaseId;
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
					Message message = mHandler
							.obtainMessage(MainActivity.HANDLER_MESSAGE_DELETE_NOTE);
					message.obj = new Long(databaseId);
					// Send the message to the main thread.
					mHandler.sendMessage(message);
				}
			} catch (Exception e) {
				Log.e("DB", "Unable to DeleteNote. " + e.getMessage());
			} finally {
				markRunnableFinished(this);
			}
		}

		private boolean deleteNoteFromDatabase() {
			// If 'delete all' operation was requested.
			if (databaseId == DELETE_ALL_ID) {
				// Delete all locations.
				LocationDatabaseHelper locationDatabaseHelper = new LocationDatabaseHelper(
						db, DELETE_ALL_ID);
				locationDatabaseHelper.deleteLocationFromDatabase();
				
				int count = db.delete(NoteEntry.TABLE_NAME, "1", null);
				if (count > 0) {
					Log.i("DB", "Deleted all " + count + " notes from database.");
					return true;
				} else {
					Log.w("DB", "No notes deleted from database. Perhaps it is empty.");
					return false;
				}
			}
			
			// Attempt to delete this Note's row from the database.
			String selection = NoteEntry._ID + " = ?";
			String selectionArgs[] = { String.valueOf(databaseId) };

			int count = db.delete(NoteEntry.TABLE_NAME, selection,
					selectionArgs);

			if (count > 0) {
				// Some row was deleted.
				Log.i("DB", "Deleted note with id " + databaseId);

				// If this note had a location saved to database.
				if (locationDatabaseId != -1) {
					LocationDatabaseHelper locationDatabaseHelper = new LocationDatabaseHelper(
							db, locationDatabaseId);
					// If there are no more notes using this location.
					if (locationDatabaseHelper.findNumberOfNotesUsingLocation() == 0) {
						// Delete the location from database.
						boolean deleted = locationDatabaseHelper
								.deleteLocationFromDatabase();
						if (deleted) {
							Log.i("DB",
									"Deleted unused location from database.");
						} else {
							Log.i("DB",
									"Failed to deleted unused location from database.");
						}
					}
				}
				return true;
			} else {
				Log.e("DB", "Failed to delete note with id " + databaseId);
				return false;
			}
		}
	}

	/**
	 * Helper class for finding/saving/deleting location data from database.
	 * 
	 * NOTE: These methods should never be called from the UI thread directly.
	 */
	private class LocationDatabaseHelper {
		private SQLiteDatabase db;
		private LatLng location;
		private long locationDatabaseId = -1;

		public LocationDatabaseHelper(SQLiteDatabase db, long locationDatabaseId) {
			if (db == null) {
				throw new IllegalArgumentException(
						"SQLiteDatabase object cannot be null.");
			} else if (locationDatabaseId == -1) {
				throw new IllegalArgumentException(
						"Location database id cannot be -1.");
			}
			this.db = db;
			this.locationDatabaseId = locationDatabaseId;
		}

		public LocationDatabaseHelper(SQLiteDatabase db, LatLng location) {
			if (db == null) {
				throw new IllegalArgumentException(
						"SQLiteDatabase object cannot be null.");
			} else if (location == null) {
				throw new IllegalArgumentException(
						"LatLng object cannot be null.");
			}

			this.db = db;
			this.location = location;
		}

		/**
		 * @return The location database id on success, or -1 on failure.
		 */
		public long findLocationFromDatabase() {
			String[] projection = { LocationEntry._ID };
			String selection = null;
			String[] selectionArgs = null;

			if (locationDatabaseId == -1) {
				// Find based on location field.
				selection = LocationEntry.COLUMN_NAME_LOCATION_X + " = ?"
						+ " AND " + LocationEntry.COLUMN_NAME_LOCATION_Y
						+ " = ?";
				selectionArgs = new String[] {
						String.valueOf(location.latitude),
						String.valueOf(location.longitude) };
			} else {
				// Find based on location database id field.
				selection = LocationEntry._ID + " = ?";
				selectionArgs = new String[] { String
						.valueOf(locationDatabaseId) };
			}

			String limitString = "1";

			Cursor cursor = db.query(LocationEntry.TABLE_NAME, projection,
					selection, selectionArgs, null, null, null, limitString);

			long databaseId = -1;

			// If there was some result found
			if (cursor.moveToFirst()) {
				int locationIdColumnIndex = cursor
						.getColumnIndexOrThrow(LocationEntry._ID);
				if (cursor.getType(locationIdColumnIndex) != Cursor.FIELD_TYPE_NULL) {
					databaseId = cursor.getLong(locationIdColumnIndex);
				}
			}

			cursor.close();

			return databaseId;
		}

		/**
		 * @return The location database id on success, or -1 on failure.
		 */
		public long saveLocationToDatabase() {
			// If only the location database id is known.
			if (locationDatabaseId != -1) {
				// Nothing can be saved to database.
				Log.e("DB", "Nothing can be saved to database. "
						+ "Only database id " + locationDatabaseId
						+ " specified.");
				return -1;
			}

			ContentValues locationValues = new ContentValues();
			locationValues.put(LocationEntry.COLUMN_NAME_LOCATION_X,
					location.latitude);
			locationValues.put(LocationEntry.COLUMN_NAME_LOCATION_Y,
					location.longitude);

			long locationRowId;
			locationRowId = db.insert(LocationEntry.TABLE_NAME, null,
					locationValues);
			if (locationRowId != -1) {
				Log.i("DB", "Saved location with id " + locationRowId);
			} else {
				Log.e("DB", "Failed to save location to database.");
			}
			return locationRowId;
		}

		public boolean deleteLocationFromDatabase() {
			long databaseIdToBeDeleted = -1;
			if (locationDatabaseId == -1) {
				// Delete based on location field.
				databaseIdToBeDeleted = findLocationFromDatabase();
			} else {
				// Delete based on location database id field.
				databaseIdToBeDeleted = locationDatabaseId;
			}

			// If there is something to be deleted from database.
			if (databaseIdToBeDeleted != -1) {
				// If it was requested that all locations be deleted from database.
				if (databaseIdToBeDeleted == DELETE_ALL_ID) {
					int count = db.delete(LocationEntry.TABLE_NAME, "1", null);
					if (count > 0) {
						Log.i("DB", "Deleted all " + count + " locations from database.");
						return true;
					} else {
						Log.w("DB", "No locations deleted from database. Perhaps it is empty.");
						return false;
					}
				}
				
				// Attempt to delete this Location's row from the database.
				String selection = LocationEntry._ID + " = ?";
				String selectionArgs[] = { String
						.valueOf(databaseIdToBeDeleted) };

				int count = db.delete(LocationEntry.TABLE_NAME, selection,
						selectionArgs);

				if (count > 0) {
					// Some row was deleted.
					Log.i("DB", "Deleted location with id "
							+ databaseIdToBeDeleted);
					return true;
				} else {
					Log.e("DB", "Failed to delete location with id "
							+ databaseIdToBeDeleted);
					return false;
				}
			}

			// Nothing was deleted.
			return false;
		}

		/**
		 * @return -1 if no such location exists in the database, or otherwise
		 *         the number of notes that refer to this location.
		 */
		public int findNumberOfNotesUsingLocation() {
			long searchedLocationDatabaseId = findLocationFromDatabase();
			// No such location exists in the database.
			if (searchedLocationDatabaseId == -1) {
				return -1;
			}

			// Find the number of Notes with this location id.
			String[] projection = { NoteEntry.COLUMN_NAME_LOCATION_ID };
			String selection = NoteEntry.COLUMN_NAME_LOCATION_ID + " = ?";
			String[] selectionArgs = { String
					.valueOf(searchedLocationDatabaseId) };
			Cursor cursor = db.query(NoteEntry.TABLE_NAME, projection,
					selection, selectionArgs, null, null, null);

			int numberOfNotes = cursor.getCount();
			cursor.close();

			return numberOfNotes;
		}
	}

	private static NoteDbHelper mInstance = null;
	private Handler mHandler;
	private Set<Runnable> activeRunnables;
	private boolean closeRequested = false;
	
	public static NoteDbHelper getInstance(Context context, Handler handler) {
		 /** 
         * Use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activity's
         * context.
         */
        if (mInstance == null) {
            mInstance = new NoteDbHelper(context.getApplicationContext(), handler);
        }
        else {
        	// If it was requested that the database instance be closed
        	// and now the instance is fetched again, then it should not be closed.
        	mInstance.closeRequested = false;
        	Log.i("DB", "Marked that DB helper should not be closed.");
        }
        return mInstance;
	}

	private NoteDbHelper(Context context, Handler handler) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// Store a reference to the Handler for Thread messages.
		mHandler = handler;
		activeRunnables = new HashSet<Runnable>();
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

	/**
	 * @param distanceInMeters
	 *            If -1 no filtering will be applied.
	 * @param currentLocation
	 *            If null no filtering will be applied.
	 */
	public void findNotes(int distanceInMeters, LatLng currentLocation) {
		// Create a new thread and start it. This will request the notes.
		// The results will be returned when they are ready.
		Runnable r = new SelectNotes(distanceInMeters, currentLocation);
		activeRunnables.add(r);
		new Thread(r).start();
	}

	public void saveNote(Note note) {
		// Start a new thread for saving the Note to database.
		Runnable r = new SaveNote(note);
		activeRunnables.add(r);
		new Thread(r).start();
	}

	public void deleteNote(long databaseId, long locationDatabaseId) {
		// Start a new thread that will attempt to delete the note with
		// the specified id from the database.
		Runnable r = new DeleteNote(databaseId, locationDatabaseId);
		activeRunnables.add(r);
		new Thread(r).start();
	}
	
	public void markRunnableFinished(Runnable r) {
		// Remove this runnable from the set of active runnables.
		activeRunnables.remove(r);
		// If there are no more active runnables.
		if (activeRunnables.size() == 0) {
			// If it was requested that this database helper be closed.
			if (closeRequested) {
				// Close this database helper.
				closeSingleton();
			}
		}
	}

	public void removeHandlerCallbacks() {
		for (Runnable r : activeRunnables) {
			mHandler.removeCallbacks(r);
		}
	}
	
	public void requestClose() {
		// Mark that it was requested that this database helper be closed.
		closeRequested = true;
		Log.i("DB", "Requested DB helper close.");
		// If there are no active runnables, close the DB helper right away.
		if (activeRunnables.size() == 0) {
			closeSingleton();
		}
	}
	
	public void closeSingleton() {
		this.close();
		mInstance = null;
		Log.i("DB", "Closed DB helper.");
	}
}
