package com.example.lab12_content_provider;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		displayContacts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	private void displayContacts() {
		TextView contactsTextView = (TextView)findViewById(R.id.ContactsTextView);
		StringBuilder contactStringBuilder = new StringBuilder("Contacts:\n");
		
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(
					cursor.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor.getString(
					cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			contactStringBuilder.append("Name: ").append(name);
			
			// Browse through the emails
			Cursor emailCursor = getContentResolver().query(
					ContactsContract.Data.CONTENT_URI,
					null,
					ContactsContract.Data.CONTACT_ID + "=" + contactId
					+ " AND "
					+ ContactsContract.Data.MIMETYPE + "='"
					+ Email.CONTENT_ITEM_TYPE + "'",
					null,
					null);
			
			ArrayList<String> emails = new ArrayList<String>();
			while (emailCursor.moveToNext()) {
				emails.add(emailCursor.getString(
						emailCursor.getColumnIndex(ContactsContract.Data.DATA1)));
			}
			emailCursor.close();
			
			if (emails.size() == 0) {
				contactStringBuilder.append(" No emails");
			}
			else {
				for (String email : emails) {
					contactStringBuilder.append(" email: ").append(email);
				}
			}
			contactStringBuilder.append("\n");
		}
		cursor.close();
		
		contactsTextView.setText(contactStringBuilder.toString());
	}
}
