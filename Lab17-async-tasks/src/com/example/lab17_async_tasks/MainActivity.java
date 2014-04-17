package com.example.lab17_async_tasks;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, SClientListener {
	private ProgressDialog progressDialog = null;
	private TextView sourceTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sourceTextView = (TextView)findViewById(R.id.SourceTextView);
		// Register listener with the buttons.
		Button getSourceButton = (Button)findViewById(R.id.GetSourceButton);
		Button clearSourceButton = (Button)findViewById(R.id.ClearSourceButton);
		getSourceButton.setOnClickListener(this);
		clearSourceButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.GetSourceButton:
				SClient sc = new SClient(this);
				URL url;
				try {
					url = new URL(getString(R.string.google_url));
					sc.execute(url);
					// Show a progress dialog.
					showProgressDialog(true);
				} catch (MalformedURLException e) {
					Log.e("MalformedURL", e.getMessage(), e);
				}
				break;
			case R.id.ClearSourceButton:
				clearSourceCodeText();
				break;
		}
		
	}

	@Override
	public void setSourceCodeText(String text) {
		sourceTextView.setText(text);
	}

	@Override
	public void clearSourceCodeText() {
		sourceTextView.setText("");;
	}

	@Override
	public void showProgressDialog(boolean show) {
		if (show) {
			// A progress dialog should be shown.
			if (progressDialog == null) {
				// Create the progress dialog
				progressDialog = ProgressDialog.show(this, "", "Loading, please wait ...");
			}
			else {
				// Show the previously created progress dialog.
				progressDialog.show();
			}
		}
		else {
			// The progress dialog should be dismissed.
			progressDialog.dismiss();
		}
	}

}
