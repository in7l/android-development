package fi.metropolia.intl.mapnotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
* This class is used for filling in ListView items with multiple
* TextViews per item. Each Map object has keys which list ids of TextViews
* and their values are the Strings that should be set as their text.
*/
public class IdValueAdapter extends ArrayAdapter<Map<Integer, String>> {
	private final Context context;
	private final ArrayList<Map<Integer, String>> data;
	private final int layoutResourceId;
	
	public IdValueAdapter(Context context, int layoutResourceId,
	ArrayList<Map<Integer, String>> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		// If this row has not been displayed before
		// inflate a layout for it.
		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
		}
		
		// Find TextViews based on the idValueMap for the current row.
		// Update their text based on the corresponding values in the Map.
		Map<Integer, String> idValueMap = data.get(position);
		for (Map.Entry<Integer, String> entry : idValueMap.entrySet()) {
			int viewId = entry.getKey();
			String textValue = entry.getValue();
			// Get a reference to the TextView with this viewId.
			TextView textView = (TextView)row.findViewById(viewId);
			textView.setText(textValue);
		}
		
		return row;
	}
}
