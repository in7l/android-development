package fi.metropolia.intl.mapnotes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.android.gms.ads.a;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fi.metropolia.intl.mapnotes.db.NoteDbHelper;
import fi.metropolia.intl.mapnotes.map.*;
import fi.metropolia.intl.mapnotes.note.*;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.animator;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity implements ToggleMapListener,
NoteListListener, DistanceSelectorListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener, OnInfoWindowClickListener, OnCheckedChangeListener, OnMapClickListener {
	public static final int HANDLER_MESSAGE_FIND_NOTES = 0;
	public static final int HANDLER_MESSAGE_SAVE_NOTE = 1;
	public static final int HANDLER_MESSAGE_DELETE_NOTE = 2;
	public static final int ACTIVITY_ADD_NOTE = 0;
	public static final int ACTIVITY_EDIT_NOTE = 1;
	public static final String CURRENT_LOCATION_BUNDLE_KEY = "current_location";
	public static final String NOTE_LOCATION_BUNDLE_KEY = "note_location";
	
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 60;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL =
	MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 30;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL =
	MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	/*
	* Define a request code to send to Google Play services
	* This code is returned in Activity.onActivityResult
	*/
	private final static int
	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	/* Variables for saving and restoring state. */
	private static final String STATE_NOTE_DISTANCE = "note_distance";
	private static final String STATE_MAP_VISIBLE = "map_visible";
	private static final String STATE_CURRENT_LOCATION = "current_location";
	
	private NoteDbHelper dbHelper;
	private FragmentManager fragmentManager;
	private MapFragment mapFragment;
	private View mapContainer;
	private ArrayList<Note> notes = null;
	private IdValueAdapter noteAdapter = null;
	private GoogleMap mMap = null;
	private LocationClient mLocationClient;
	// Global variable to hold the current location
	private LatLng mCurrentLocation;
	// Define an object that holds accuracy and frequency parameters
	private LocationRequest mLocationRequest;
	private boolean mUpdatesRequested = false;
	private SharedPreferences mPrefs;
	private Editor mEditor;
	private Circle noteRadiusCircle;
	// A map that holds map markers and their corresponding notes.
	private Map<Marker, Note> mapMarkers;
	private Marker currentLocationMarker;
	private int noteDistanceInMeters = -1;
	private boolean mapVisible = false;
	
	private Handler uiHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HANDLER_MESSAGE_FIND_NOTES:
				// Update the list of Note objects.
				notes = (ArrayList<Note>)msg.obj;
				// Log.i("Note", "Found " + notes.size() + " notes.");
				// Update the data in the ListView.
				updateNoteList();
				// Update the current location and the notes shown on the map.
				showCurrentLocationOnMap();
				break;
				case HANDLER_MESSAGE_SAVE_NOTE:
				// A note has been saved to the database.
				Note note = (Note)msg.obj;
				// Log.i("Note", "Saved note with description: " +
				//	note.getDescriptionString());
				// Request the list of notes to be updated.
				requestNoteListUpdate();
				break;
				case HANDLER_MESSAGE_DELETE_NOTE:
				// A note has been deleted from the database.
				Long deletedNoteDatabaseId = (Long)msg.obj;
				// Log.i("Note", "Deleted note with database id: " +
				//	deletedNoteDatabaseId);
				// Request the list of notes to be updated.
				requestNoteListUpdate();
				break;
				default:
				super.handleMessage(msg);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Store a reference to the fragment manager.
		fragmentManager = getFragmentManager();
		
		// Open the shared preferences
		mPrefs = getSharedPreferences("SharedPreferences",
		Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();
		
		// Things concerning location APIs.
		
		/*
		* Create a new location client, using the enclosing class to
		* handle callbacks.
		*/
		mLocationClient = new LocationClient(this, this, this);
		// Check if location updates have been requested.
		checkLocationUpdatesRequested();
		// Set the initial state of the location update checkbox.
		CheckBox updateLocationCheckBox = (CheckBox)findViewById(R.id.UpdateLocationCheckBox);
		updateLocationCheckBox.setChecked(mUpdatesRequested);
		// Register listener for the checkbox.
		updateLocationCheckBox.setOnCheckedChangeListener(this);
		
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(
		LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval.
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval.
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	}
	
	
	/*
	* Called when the Activity becomes visible.
	*/
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
		
		// Create a SQLiteOpenHelper if necessary.
		if (dbHelper == null) {
			dbHelper = NoteDbHelper.getInstance(this, uiHandler);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkLocationUpdatesRequested();
	}
	
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		int noteDistance = savedInstanceState.getInt(STATE_NOTE_DISTANCE, -1);
		boolean showMap = savedInstanceState.getBoolean(STATE_MAP_VISIBLE, false);
		mCurrentLocation = savedInstanceState.getParcelable(STATE_CURRENT_LOCATION);
		
		setDistance(noteDistance);
		showMap(showMap);
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_NOTE_DISTANCE, noteDistanceInMeters);
		outState.putBoolean(STATE_MAP_VISIBLE, mapVisible);
		outState.putParcelable(STATE_CURRENT_LOCATION, mCurrentLocation);
		
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	protected void onPause() {
		// Save the current setting for updates
		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
		mEditor.commit();       
		super.onPause();
	}
	
	
	/*
	* Called when the Activity is no longer visible.
	*/
	@Override
	protected void onStop() {
		// If the client is connected
		if (mLocationClient.isConnected()) {
			/*
			* Remove location updates for a listener.
			* The current Activity is the listener, so
			* the argument is "this".
			*/
			mLocationClient.removeLocationUpdates(this);
		}
		/*
		* After disconnect() is called, the client is
		* considered "dead".
		*/
		mLocationClient.disconnect();
		
		// Close the database helper.
		if (dbHelper != null) {
			dbHelper.removeHandlerCallbacks();
			// Request this database helper to be closed
			// once all of its runnables are no longer active.
			dbHelper.requestClose();
			dbHelper = null;
		}
		
		super.onStop();
	}
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// TODO: Enable this once Settings are needed.
		// getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case ACTIVITY_ADD_NOTE:
			if (resultCode == Activity.RESULT_OK) {
				Note note = (Note) data.getSerializableExtra(Note.NOTE_BUNDLE_KEY);
				if (note != null) {
					// Since the current method could be called before onStart()
					// Create a SQLiteOpenHelper if necessary.
					if (dbHelper == null) {
						dbHelper = NoteDbHelper.getInstance(this, uiHandler);
					}
					// A new note has been created.
					// Save it to the database.
					persistNote(note);
				}
			}
			break;
			case ACTIVITY_EDIT_NOTE:
			if (resultCode == Activity.RESULT_OK) {
				// Get the note that has been edited.
				Note note = (Note) data.getSerializableExtra(Note.NOTE_BUNDLE_KEY);
				if (note != null) {
					// Since the current method could be called before onStart()
					// Create a SQLiteOpenHelper if necessary.
					if (dbHelper == null) {
						dbHelper = NoteDbHelper.getInstance(this, uiHandler);
					}
					// Save the changes to the database.
					persistNote(note);
				}
			}
			break;
			case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			* If the result code is Activity.RESULT_OK, try
			* to connect again
			*/
			switch (resultCode) {
				case Activity.RESULT_OK :
				/*
				* Try the request again
				*/
				break;
			}
			break;
		}
	}
	
	@Override
	public void addNote(LatLng noteLocation) {
		// Log.i("Note", "Add note.");
		// Make a new intent to start a NoteEditActivity.
		Intent intent = new Intent(this, NoteEditActivity.class);
		if (noteLocation != null) {
			// This note should be created at a specific location.
			intent.putExtra(NOTE_LOCATION_BUNDLE_KEY, noteLocation);
		}
		// Start the activity.
		startActivityForResult(intent, ACTIVITY_ADD_NOTE);
	}
	
	@Override
	public void openNote(Note note) {
		// Log.i("Note", "Opening note with description: " + note.getDescriptionString());
		// Make a new intent to start a NoteViewActivity.
		Intent intent = new Intent(this, NoteViewActivity.class);
		// Pass the serialized Note to that activity.
		intent.putExtra(Note.NOTE_BUNDLE_KEY, note);
		// Start the activity.
		startActivity(intent);
	}
	
	@Override
	public void editNote(Note note) {
		// Log.i("Note", "Editing note with description: " + note.getDescriptionString());
		// Make a new intent to start a NoteEditActivity.
		Intent intent = new Intent(this, NoteEditActivity.class);
		// Pass the serialized Note to that activity.
		intent.putExtra(Note.NOTE_BUNDLE_KEY, note);
		// Start the activity.
		startActivityForResult(intent, ACTIVITY_EDIT_NOTE);
	}
	
	@Override
	public void deleteNote(Note note) {
		// Log.i("Note", "Deleting note with description: " + note.getDescriptionString());
		// Request the Note with this database id to be deleted from the database.
		long databaseId = note.getDatabaseId();
		long locationDatabaseId = note.getLocationDatabaseId();
		dbHelper.deleteNote(databaseId, locationDatabaseId);
	}
	
	@Override
	public void clearAllDatabaseData() {
		// Show a dialog prompting the user to confirm this decision.
		new AlertDialog.Builder(this)
		.setTitle(getString(R.string.clear_all_title))
		.setMessage(getString(R.string.clear_all_message))
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				onConfirmDatabaseClear();
			}
		})
		.setNegativeButton(android.R.string.no, null).show();
	}
	
	
	
	private void onConfirmDatabaseClear() {
		// The user confirmed that the data should be cleared.
		// Log.i("DB", "Clearing all database data.");
		dbHelper.deleteNote(NoteDbHelper.DELETE_ALL_ID, NoteDbHelper.DELETE_ALL_ID);
	}
	
	@Override
	public void requestNoteListUpdate() {
		if (dbHelper != null) {
			// Request the list of notes to be updated.
			// The results will be received when this is done.
			dbHelper.findNotes(noteDistanceInMeters, mCurrentLocation);
			// Log.i("Note", "Requested findNotes() from dbHelper.");
		}
	}
	
	/**
	* Saves a Note object to the database.
	*/
	private void persistNote(Note note) {
		// If the note should use the current location,
		// then assign it to the object.
		if (note.usesCurrentLocation()) {
			if (mCurrentLocation != null) {
				note.setLocation(mCurrentLocation);
			}
			else {
				Toast.makeText(this, R.string.current_location_not_saved,
				Toast.LENGTH_LONG).show();
			}
		}
		dbHelper.saveNote(note);
	}
	
	private void updateNoteList() {
		// Log.i("NoteList", "Updating note list.");
		
		NoteListFragment noteListFragment =
		(NoteListFragment) fragmentManager.findFragmentById(
		R.id.NoteListContainer);
		if (noteListFragment != null) {
			noteListFragment.setNotes(notes);
		}
	}
	
	@Override
	public void showMap(boolean show) {
		// If the state did not really change.
		if (mapVisible == show) {
			return;
		}
		
		// Save the new state.
		mapVisible = show;
		
		if (show) {
			// The map should be shown.
			// Log.i("Map", "Show map");
			
			// Create the map fragment.
			if (mapFragment == null) {
				GoogleMapOptions options = new GoogleMapOptions();
				options.mapType(GoogleMap.MAP_TYPE_HYBRID)
				.compassEnabled(false)
				.rotateGesturesEnabled(false)
				.tiltGesturesEnabled(false);
				mapFragment = MapFragment.newInstance(options);
			}
			
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			// Set animation for the transaction.
			transaction.setCustomAnimations(animator.fade_in, animator.fade_out);
			// Add the fragment to the MapContainer.
			transaction.add(R.id.MapContainer, mapFragment);
			// Commit the changes.
			transaction.commit();
			// Execute the transaction immediately.
			// This is needed so the GoogleMap object is instantiated.
			fragmentManager.executePendingTransactions();
			
			// Update the mapContainer properties.
			updateMapContainerProperties(show);
			
			setUpMapIfNeeded();
			showCurrentLocationOnMap();
			zoomToCurrentLocationOnMap();
		} else {
			// The map should be hidden.
			// Log.i("Map", "Hide map");
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			// Set animation for the transaction.
			transaction.setCustomAnimations(animator.fade_in, animator.fade_out);
			// Remove the fragment from MapContainer.
			transaction.remove(fragmentManager.findFragmentById(R.id.MapContainer));
			transaction.commit();
			// Update the height and weight of the mapContainer.
			updateMapContainerProperties(show);
			
			// The map should be setup again next time the map fragment is shown.
			mMap = null;
			// Clear also other data that was displayed on the map.
			noteRadiusCircle = null;
			mapMarkers = null;
		}
	}
	
	private void updateMapContainerProperties(boolean showMap) {
		if (mapContainer == null) {
			// Get a reference to the MapContainer item in the main layout.
			mapContainer = findViewById(R.id.MapContainer);
		}
		
		// Adjust the MapContainer visibility.
		if (showMap) {
			mapContainer.setVisibility(View.VISIBLE);
		} else {
			mapContainer.setVisibility(View.GONE);
		}
	}
	
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null && mapFragment != null) {
			mMap = mapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				mMap.setOnInfoWindowClickListener(this);
				mMap.setOnMapClickListener(this);
				mapMarkers = new HashMap<Marker, Note>();
			}
		}
	}
	
	private void showCurrentLocationOnMap() {
		if (mMap != null) {
			// Clear all elements previously drawn on the map, if any.
			clearMapElements();
			
			if (mCurrentLocation != null) {
				// Create a marker for the map showing the current location.
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(mCurrentLocation);
				markerOptions.title(getString(R.string.current_position));
				// Change the marker color.
				markerOptions.icon(
				BitmapDescriptorFactory.defaultMarker(
				BitmapDescriptorFactory.HUE_AZURE));
				currentLocationMarker = mMap.addMarker(markerOptions);
				currentLocationMarker.showInfoWindow();
				
				// Add a circle.
				CircleOptions circleOptions = new CircleOptions();
				circleOptions.center(mCurrentLocation);
				circleOptions.strokeColor(R.color.AliceBlue);
				circleOptions.fillColor(R.color.Aqua);
				noteRadiusCircle = mMap.addCircle(circleOptions);
				// Adjust some properties of the circle depending on the current distance.
				adjustMapCircle();
			}
			
			// Add markers for all the Notes within range
			// or all notes if no range is specified.
			adjustMapNotes();
		}
	}
	
	private void zoomToCurrentLocationOnMap() {
		if (mMap != null && mCurrentLocation != null) {
			// Zoom to the current location
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15);
			mMap.animateCamera(update);
		}
	}
	
	private void clearMapElements() {
		if (mMap != null) {
			// Clear the note markers.
			clearMapNoteMarkers();
			// Clear the note radius circle.
			clearMapNoteRadiusCircle();
			// Clear the current location marker.
			clearMapCurrentLocationMarker();
		}
	}
	
	
	private void clearMapCurrentLocationMarker() {
		if (currentLocationMarker != null) {
			currentLocationMarker.remove();
			currentLocationMarker = null;
		}
	}
	
	
	private void clearMapNoteRadiusCircle() {
		if (noteRadiusCircle != null) {
			noteRadiusCircle.remove();
			noteRadiusCircle = null;
		}
	}
	
	
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;
		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	
	private boolean servicesConnected() {
		int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (errorCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
			return false;
		}
		
		return true;
	}
	
	/*
	* Called by Location Services when the request to connect the
	* client finishes successfully. At this point, you can
	* request the current location or start periodic updates
	*/
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status.
		// Toast.makeText(this, getString(R.string.location_services_connected), Toast.LENGTH_SHORT).show();
		// If already requested, start periodic updates
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
			
			// Attempt to get the last known location immediately.
			getInitialLocation();
		}
	}
	
	private void getInitialLocation() {
		// mCurrentLocation = new LatLng(19.866185, -155.594087);
		
		// Attempt to get the last known location.
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation != null) {
			mCurrentLocation = new LatLng(
			currentLocation.getLatitude(),
			currentLocation.getLongitude());
			// Report the location update to the UI.
			reportLocationUpdateToUi();
			
			// If the map is visible, show the current location on the map.
			if (mapVisible) {
				showCurrentLocationOnMap();
				zoomToCurrentLocationOnMap();
			}
		}
	}
	
	
	/*
	* Called by Location Services if the connection to the
	* location client drops because of an error.
	*/
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
		Toast.LENGTH_SHORT).show();
	}
	
	/*
	* Called by Location Services if the attempt to
	* Location Services fails.
	*/
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		* Google Play services can resolve some errors it detects.
		* If the error has a resolution, try sending an Intent to
		* start a Google Play services activity that can resolve
		* error.
		*/
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
				this,
				CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				* Thrown if Google Play services canceled the original
				* PendingIntent
				*/
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			* If no resolution is available, display a dialog to the
			* user with the error.
			*/
			// TODO: fix this
			// showErrorDialog(connectionResult.getErrorCode());
		}
	}
	
	// Define the callback method that receives location updates
	@Override
	public void onLocationChanged(Location location) {
		Location currentLocation = location;
		mCurrentLocation = new LatLng(
		currentLocation.getLatitude(),
		currentLocation.getLongitude());
		// Report to the UI that the location was updated
		reportLocationUpdateToUi();
	}
	
	private void reportLocationUpdateToUi() {
		printCurrentLocation();
		// Refresh the list of notes.
		requestNoteListUpdate();
	}
	
	
	public void printCurrentLocation() {
		String msg = "Updated Location: " +
		Double.toString(mCurrentLocation.latitude) + ", " +
		Double.toString(mCurrentLocation.longitude);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	public void setDistance(int distanceInMeters) {
		noteDistanceInMeters = distanceInMeters;
		adjustMapCircle();
		requestNoteListUpdate();
	}
	
	private void adjustMapCircle() {
		// If the circle exists, it can be adjusted.
		if (noteRadiusCircle != null) {
			// If the distance is not -1 (marking infinity).
			if (noteDistanceInMeters != -1) {
				// Show the circle.
				noteRadiusCircle.setVisible(true);
				noteRadiusCircle.setRadius(noteDistanceInMeters);
			}
			else {
				// Hide the circle.
				noteRadiusCircle.setVisible(false);
			}
		}		
		
	}
	
	private void clearMapNoteMarkers() {
		if (mapMarkers != null) {
			// Remove all the current markers from the map.
			for (Entry<Marker, Note> mapMarkerNoteEntry : mapMarkers.entrySet()) {
				Marker marker = mapMarkerNoteEntry.getKey();
				marker.remove();
			}
			
			// Clear the map of Marker-Note pairs.
			mapMarkers.clear();
		}
	}
	
	private void adjustMapNotes() {
		if (mapMarkers != null) {
			clearMapNoteMarkers();
			
			// If there are some notes to be displayed.
			if (notes != null && notes.size() > 0) {
				// Create new markers from the current notes.
				for (Note note : notes) {
					// Get the location of this note.
					LatLng noteLocation = note.getLocation();
					if (noteLocation == null) {
						// This Note does not specify a location.
						// It cannot be displayed on the map.
						continue;
					}
					
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(noteLocation);
					String markerTitle = null;
					// If the note has a summary, display it as the marker title.
					if (note.getSummary() != null) {
						markerTitle = note.getSummaryString();
					}
					// If the note specifies a description, get the first few letters of it.
					else if (note.getDescription() != null) {
						String noteDescription = note.getDescriptionString();
						int substrLength = 20;
						if (noteDescription.length() < substrLength) {
							substrLength = noteDescription.length();
						}
						markerTitle = noteDescription.substring(0, substrLength) +
						"..."; 
					}
					else {
						markerTitle = "Note " + note.getDatabaseId();
					}
					// Set the marker title if any.
					if (markerTitle != null) {
						markerOptions.title(markerTitle);
					}
					
					// Add the marker to the map.
					Marker marker = mMap.addMarker(markerOptions);
					
					// Add the marker to the array list of mapMarkers.
					// In this way it can be removed from the map later on if needed.
					// Also store its corresponding Note object.
					mapMarkers.put(marker, note);
				}
			}
		}
	}
	
	
	/**
	* Called when the map markers popups are clicked.
	*/
	@Override
	public void onInfoWindowClick(Marker marker) {
		// Attempt to get the note that corresponds to this marker.
		Note note = mapMarkers.get(marker);
		if (note != null) {
			// Found the note. Open it.
			openNote(note);
		}
	}
	
	/**
	* Check if location updates were requested and initialize
	* the mUpdatesRequested variable depending on that. 
	*/
	public void checkLocationUpdatesRequested() {
		/*
		* Get any previous setting for location updates
		* Gets "false" if an error occurs
		*/
		if (mPrefs.contains("KEY_UPDATES_ON")) {
			mUpdatesRequested =
			mPrefs.getBoolean("KEY_UPDATES_ON", false);
			
			
			// Otherwise, turn off location updates
		} else {
			mEditor.putBoolean("KEY_UPDATES_ON", false);
			mEditor.commit();
			mUpdatesRequested = false;
		}
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.UpdateLocationCheckBox:
			mUpdatesRequested = isChecked;
			// Update the last known location.
			if (mUpdatesRequested) {
				if (mLocationClient != null && mLocationRequest != null) {
					// Start listening for location updates.
					mLocationClient.requestLocationUpdates(mLocationRequest, this);
				}
				getInitialLocation();
			}
			else {
				// Stop listening for location updates.
				mLocationClient.removeLocationUpdates(this);
			}
			break;
		}
	}
	
	
	@Override
	public void onMapClick(LatLng noteLocation) {
		addNote(noteLocation);
	}
	
}
