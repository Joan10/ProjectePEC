package com.example.robotspec;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void MostraActivitatAutobot(View clickedButton) {
		Intent activityIntent = new Intent(this, AutobotControlActivity.class);
		startActivity(activityIntent);
	}
	
	public void MostraActivitatDeception(View clickedButton) {
		Intent activityIntent = new Intent(this, DeceptionControlActivity.class);
		startActivity(activityIntent);
	}

}
