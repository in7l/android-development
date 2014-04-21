package fi.metropolia.intl.mapnotes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.location.Location;

public class Note {
	private String summary = null;
	private String description = null;
	private Location location = null;
	private Date datetime;
	
	/* Constructors */
	public Note() {
		this(null);	
	}

	public Note(String description) {
		this(description, null);
	}

	public Note(String description, Location location) {
		this(description, location, new Date());
	}
	
	public Note(String description, Location location, Date datetime) {
		this(description, location, datetime, null);
	}

	public Note(String description, Location location,
			Date datetime, String summary) {
		this.summary = summary;
		this.description = description;
		this.location = location;
		if (datetime == null) {
			datetime = new Date();
		}
		this.datetime = datetime;
	}
	
	/**
	 * @return Map<Integer, String> where the key is an id for a View
	 * such as TextView and the String is the value that should be
	 * assigned to that element. 
	 */
	public Map<Integer, String> getIdValueMap() {
		// Create a map of Integer-String value pairs
		// representing a View id and its corresponding text.
		Map<Integer, String> idValueMap = new HashMap<Integer, String>(4);
		idValueMap.put(R.id.note_summary, getSummaryString());
		idValueMap.put(R.id.note_description, getDescriptionString());
		idValueMap.put(R.id.note_datetime, getDatetimeString());
		idValueMap.put(R.id.note_distance, getDistanceString());
		 
		return idValueMap;
	}
	
	public String getSummaryString() {
		// If the summary is null return empty string.
		String summaryString = (summary != null) ? summary : "";
		return summaryString;
	}
	
	public String getDescriptionString() {
		// If the description is null, return empty string.
		String descriptionString = (description != null) ? description : "";
		return descriptionString;
	}
	
	public String getDatetimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		String datetimeString = sdf.format(datetime);
		return datetimeString;
	}
	
	public String getDistanceString() {
		return "< 20m";
	}
	
	/**
	 * Creates a new ArrayList holding viewId - textValue pair map
	 * from an ArrayList of Note objects
	 */
	public static ArrayList<Map<Integer, String>> getNoteListAsIdValueMap(
			ArrayList<Note> noteList) {
		ArrayList noteIdValueMapList = new ArrayList<Map<Integer, String>>(noteList.size());
		for (Note note : noteList) {
			noteIdValueMapList.add(note.getIdValueMap());
		}
		
		return noteIdValueMapList;
	}
}
