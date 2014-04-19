package fi.metropolia.intl.mapnotes;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NoteListFragment extends ListFragment {
	private String[] descriptions = { "descr1", "test2", "test3", "test4",
			"test5", "test6", "test7", "test8" };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the note_list layout used by this fragment and return it.
		return inflater.inflate(R.layout.note_list, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create an array adapter for filling in the descriptions.
		ArrayAdapter<String> noteDescriptionsAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.note_collapsed, R.id.note_description, descriptions);
		setListAdapter(noteDescriptionsAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Get the dcescription from the pressed item.
		String description = (String) ((TextView)v.findViewById(R.id.note_description)).getText();
		Log.i("Test", "Pressed item with description: " + description);
	}
	
}
