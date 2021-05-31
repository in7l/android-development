package fi.metropolia.intl.mapnotes.db;

import android.provider.BaseColumns;

public class NoteContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public NoteContract() {}
	
	/* Inner class that defines the Note table contents */
	public static abstract class NoteEntry implements BaseColumns {
		public static final String TABLE_NAME = "note";
		public static final String COLUMN_FULL_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_SUMMARY = "summary";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_DATETIME_TIMESTAMP = "datetime_ts";
		public static final String COLUMN_NAME_LOCATION_ID = "location_id";
	}
	
	/* Inner class that defines the Location table contents */
	public static abstract class LocationEntry implements BaseColumns {
		public static final String TABLE_NAME = "location";
		public static final String COLUMN_FULL_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_LOCATION_X = "location_x";
		public static final String COLUMN_NAME_LOCATION_Y = "location_y";
	}
	
}
