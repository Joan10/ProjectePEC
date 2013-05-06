package com.example.robotspec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;


public class AutobotControlActivity extends Activity  {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new Taulell(this,0));
		
	}

	//public void MostraActivitatAutobot(View clickedButton) {
	//	Intent activityIntent = new Intent(this, LoanCalculatorActivity.class);
	//	startActivity(activityIntent);
	//}
}