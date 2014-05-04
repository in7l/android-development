package fi.metropolia.intl.mapnotes.note;

import com.google.android.gms.maps.model.LatLng;

public interface NoteEditListener {
	public void updateNote(Note note);
	public void createNote(Note note);
}
