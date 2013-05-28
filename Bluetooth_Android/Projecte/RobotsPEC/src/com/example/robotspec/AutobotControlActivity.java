package com.example.robotspec;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class AutobotControlActivity extends Activity  {
	
	Button btnEndavant, btnEndarrera, btnEsquerra, btnDreta, btnStop;
	TextView txtrebut;
	
	static String MAC_Arduino = "00:06:66:08:B2:8A";
	static String msgEndavant = "0";
	static String msgEndarrera = "1";
	static String msgEsquerra = "2";
	static String msgDreta = "3";
	static String msgStop = "4";
	
	

	BluetoothAdapter bt;
	mod_Bluetooth mb;

	void PreparaBluetooth(){
		
		btnEndavant = (Button) findViewById(R.id.ButtonFor);
		btnEndarrera = (Button) findViewById(R.id.buttonBac);		
		btnEsquerra = (Button) findViewById(R.id.buttonLef);
		btnDreta = (Button) findViewById(R.id.buttonRig);
		btnStop = (Button) findViewById(R.id.buttonSto);
	//	txtrebut = (TextView) findViewById(R.id.txtrebut);
		
		bt = BluetoothAdapter.getDefaultAdapter();
		mb = new mod_Bluetooth(bt, "00001101-0000-1000-8000-00805F9B34FB", btnEndavant, txtrebut);
		mb.Activa_Client(MAC_Arduino); //Ens connectarem al PIC
		
		btnEndavant.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mb.Envia(msgEndavant);
			}
		});
		
		btnEndarrera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mb.Envia(msgEndarrera);
			}
		});
		btnEsquerra.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mb.Envia(msgEsquerra);
			}
		});
		btnDreta.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mb.Envia(msgDreta);
			}
		});
		btnStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mb.Envia(msgStop);
			}
		});
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autobot);
		PreparaBluetooth();
		
	}

	public void onResume() {
		super.onResume();
		Log.d("BT", "...Començant()...");

		
		if (bt== null) {
			// Device does not support Bluetooth
		}
		
		if (!bt.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}
		
		
		mb.Retorna();
		
	}
	//public void MostraActivitatAutobot(View clickedButton) {
	//	Intent activityIntent = new Intent(this, LoanCalculatorActivity.class);
	//	startActivity(activityIntent);
	//}
}