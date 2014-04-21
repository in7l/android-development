package fi.metropolia.intl.mapnotes;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class NoteViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_view_container);
		
		// Get the extras from the intent that started this activity.
		Bundle extras = getIntent().getExtras();
		
		// Create a NoteViewFragment.
		NoteViewFragment fragment = new NoteViewFragment();
		// Pass the extras to that fragment.
		fragment.setArguments(extras);
		// Add the NoteViewFragment to its container in this activity's layout.
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.NoteViewContainer, fragment);
		fragmentTransaction.commit();
	}
}
