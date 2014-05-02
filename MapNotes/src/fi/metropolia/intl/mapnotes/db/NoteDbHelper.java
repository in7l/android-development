package fi.metropolia.intl.mapnotes.db;

import fi.metropolia.intl.mapnotes.db.NoteContract.LocationEntry;
import fi.metropolia.intl.mapnotes.db.NoteContract.NoteEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "notes.db";

	private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    
    private static final String SQL_CREATE_NOTES =
		"CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
		NoteEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
		NoteEntry.COLUMN_NAME_SUMMARY + TEXT_TYPE + COMMA_SEP +
		NoteEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
		NoteEntry.COLUMN_NAME_DATETIME_TIMESTAMP  + INTEGER_TYPE + COMMA_SEP +
		NoteEntry.COLUMN_NAME_LOCATION_ID + INTEGER_TYPE +
		" )";

    private static final String SQL_DELETE_NOTES =
    	"DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;
    
    private static final String SQL_CREATE_LOCATIONS =
    	"CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
		LocationEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
		LocationEntry.COLUMN_NAME_LOCATION_X + INTEGER_TYPE + COMMA_SEP +
		LocationEntry.COLUMN_NAME_LOCATION_Y + INTEGER_TYPE + COMMA_SEP +
		" )";
    
    private static final String SQL_DELETE_LOCATIONS =
    	"DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;
	
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
		// TODO: Whenever this is needed, figure out how to upgrade.
        db.execSQL(SQL_DELETE_NOTES);
        db.execSQL(SQL_DELETE_LOCATIONS);
        onCreate(db);
	}

}
