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
	
	Button btnEnv;
	TextView txtrebut;
	
	static String MAC_Arduino = "00:06:66:08:B2:8A";
	// Autobot
	private static byte[] DISPARA = new byte[] { (byte)'D' };	// D
	private static byte[] TARGET = new byte[] { (byte)'T' };	// T
	private static byte[] NO_TARGET = new byte[] { (byte)'N' };	// N
	private static byte[] CAMERA = new byte[] { (byte)'C' };	// C

	BluetoothAdapter bt;
	mod_Bluetooth mb = null;
	
	public void AC_disparar(){
		if (mb != null ) mb.EnviaByte(DISPARA);
	}

	public byte[] AC_KillCam(){
		// Pre : solo entramos aqui si se ha leido que queremos mostrar foto
		// tengo que recoger la foto y convertirla a algo chulo, para pasarlo a la capa VISTA
		return ( new byte[] { 0x00 } );
	}
	
	void PreparaBluetooth(){
		
		btnEnv = (Button) findViewById(R.id.btnEnvia);
	//	txtrebut = (TextView) findViewById(R.id.txtrebut);
		
		bt = BluetoothAdapter.getDefaultAdapter();
		mb = new mod_Bluetooth(bt, "00001101-0000-1000-8000-00805F9B34FB", btnEnv, txtrebut);
		mb.Activa_Client(MAC_Arduino); //Ens connectarem al PIC
		
		btnEnv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					AC_disparar();
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