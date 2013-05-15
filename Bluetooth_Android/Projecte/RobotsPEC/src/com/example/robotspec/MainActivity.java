package com.example.robotspec;


import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends Activity {

	Button btnMa, btnSl, btnEnv;
	TextView txtquisoc, txtquefaig, txtrebut;
	
	static String MAC_Arduino = "00:06:66:08:B2:8A";
	static String MAC_MeuMobil = "90:C1:15:BE:65:83";
	static String msg1 = "M";

	BluetoothAdapter bt;
	mod_Bluetooth mb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnMa = (Button) findViewById(R.id.btnMaster);					
		btnSl = (Button) findViewById(R.id.btnSlave);
		btnEnv = (Button) findViewById(R.id.btnEnvia);
		
		txtquisoc = (TextView) findViewById(R.id.quisoc);
		txtquefaig = (TextView) findViewById(R.id.quefaig);
		txtrebut = (TextView) findViewById(R.id.txtrebut);
		
		bt = BluetoothAdapter.getDefaultAdapter();
		mb = new mod_Bluetooth(bt, "00001101-0000-1000-8000-00805F9B34FB", btnEnv, txtrebut);
		
		btnMa.setOnClickListener(new OnClickListener() {
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
		});
		
		btnEnv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mb.Envia(msg1);
			}
		});
		
		
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
