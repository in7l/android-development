package fi.metropolia.intl.mapnotes.note;

import java.util.ArrayList;

import fi.metropolia.intl.mapnotes.IdValueAdapter;

public interface NoteListListener {
	public void addNote();
	public void openNote(Note note);
	public void editNote(Note note);
	public void deleteNote(Note note);
	public void requestNoteListUpdate();
}
