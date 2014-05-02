package fi.metropolia.intl.mapnotes.note;

public interface NoteListListener {
	public void openNote(Note note);
	public void editNote(Note note);
	public void deleteNote(Note note);
}
