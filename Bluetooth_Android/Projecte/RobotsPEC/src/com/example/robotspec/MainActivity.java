package com.example.robotspec;


import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
	
	public void MostraActivitatDecepticon(View clickedButton) {
		Intent activityIntent = new Intent(this, DecepticonControlActivity.class);
		startActivity(activityIntent);
	}

}



/*	btnMa.setOnClickListener(new OnClickListener() {
public void onClick(View v) {
	btnMa.setEnabled(false);
	btnSl.setEnabled(false);
	mb.Activa_Servidor();
	txtquisoc.setText("Sóc màster");
	//Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
	//mConnectedThread és l'objecte encarregat d'enviar dades, d'escriure al buffer
}
});

btnSl.setOnClickListener(new OnClickListener() {
public void onClick(View v) {
	btnMa.setEnabled(false);
	btnSl.setEnabled(false);
	
	mb.Activa_Client(MAC_Arduino);
	txtquisoc.setText("Sóc slave");
}
});*/
