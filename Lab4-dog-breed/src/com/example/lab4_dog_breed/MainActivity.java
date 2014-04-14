package com.example.lab4_dog_breed;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	private String[] dogs;
	private int[] dogImageIds = {  R.drawable.border_terrier, R.drawable.alaskan_malamute,
			R.drawable.finnish_spitz, R.drawable.portuguese_water_dog, R.drawable.rottweiler,
			R.drawable.beagle, R.drawable.greyhound, R.drawable.dachshund};
	private TextView resultsTextView;
	private ImageView resultsImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Load the array list of dog breeds.
		dogs = getResources().getStringArray(R.array.dogs);
		
		// Get the TextView and ImageView for displaying the selected options.
		resultsTextView = (TextView) findViewById(R.id.Text1);
		resultsImageView = (ImageView) findViewById(R.id.Image1);
		
		ArrayAdapter<String> myList = new ArrayAdapter<String>(this, R.layout.listitem,
				 R.id.tv, dogs);
		setListAdapter(myList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Update the text selection
		resultsTextView.setText("Selection: " + dogs[position]);
		// Update the image.
		resultsImageView.setImageResource(dogImageIds[position]);
	}
	
	

}
