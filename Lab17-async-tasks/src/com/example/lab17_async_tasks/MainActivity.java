package com.example.lab17_async_tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private TextView sourceTextView;
	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				sourceTextView.setText((String)msg.obj); 
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sourceTextView = (TextView)findViewById(R.id.SourceTextView);
		// Register listener with the button.
		Button getSourceButton = (Button)findViewById(R.id.GetSourceButton);
		getSourceButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		SClient sc = new SClient(uiHandler);
		Thread t = new Thread(sc);
		t.start();
	}

}
