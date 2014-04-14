package com.example.lab4_dog_breed;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	private String[] dogs = { "test1", "test2" };
	private int[] dogImageIds;
	private TextView resultsTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Load the array list of dog breeds and their images.
		//dogs = getResources().getStringArray(R.array.dogs);
		//dogImageIds = getResources().getIntArray(R.array.dogImageIds);
		
		// Load the textview for displaying results.
		resultsTextView = (TextView) findViewById(R.id.Text1);
		
		ArrayAdapter<String> myList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				R.id.tv, dogs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		resultsTextView.setText("Selection: " + dogs[position]);
	}
	
	

}
