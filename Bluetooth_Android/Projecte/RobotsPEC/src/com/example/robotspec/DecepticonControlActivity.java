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


public class DecepticonControlActivity extends Activity {
	
	Button btnEndavant, btnEndarrera, btnEsquerra, btnDreta, btnStop;
	TextView txtDebugger;
	
	static String MAC_Arduino = "00:06:66:08:B2:8A";
	// Decepticon
	private static byte[] PALANTE = new byte[] { 0x1 };			// 1
	private static byte[] MUERTO = new byte[] { 0x2 };			// 2
	private static byte[] PATRAS = new byte[] { 0x3 };			// 3
	private static byte[] ADERECHA = new byte[] { 0x4 };		// 4
	private static byte[] DDERECHA = new byte[] { 0x5 };		// 5
	private static byte[] AIZQUIERDA = new byte[] { 0x6 };		// 6
	private static byte[] DIZQUIERDA = new byte[] { 0x7 };		// 7
	private static byte[] DEBUGGER = new byte[] { (byte) 'V' };	// V
	private static String STOP_DEBUGGER = "^~^";				// ^~^
	

	BluetoothAdapter bt;
	mod_Bluetooth mb;
	
	public String leerBluetooth() {
		return mb.Llegeix();
	}
	
	public void DC_palante(){
		mb.EnviaByte(PALANTE);
	}
	
	public void DC_puntoMuerto(){
		mb.EnviaByte(MUERTO);
	}
	
	public void DC_patras(){
		mb.EnviaByte(PATRAS);
	}
	
	public void DC_activaDerecha(){
		mb.EnviaByte(ADERECHA);
	}
	
	public void DC_desactivaDerecha(){
		mb.EnviaByte(DDERECHA);
	}
	
	public void DC_activaIzquierda() {
		mb.EnviaByte(AIZQUIERDA);
	}
	
	public void DC_desactivaIzquierda(){
		mb.EnviaByte(DIZQUIERDA);
	}

	public void DC_debugger(){
		// Pre : aqui solo me llaman si se ha leido DEBUGGER anteriormente
		String byteIN = mb.Llegeix();
		while ( !byteIN.toCharArray().equals(STOP_DEBUGGER) ) {
			// transformar byte en un PEDAZO DE STRING MOLON
			// funcion para enviarlo todo a escribir en pantalla (PEDRO)
			byteIN = mb.Llegeix();
		}
	}
	
	
	
	void PreparaBluetooth(){
		
		btnEndavant = (Button) findViewById(R.id.ButtonFor);
		btnEndarrera = (Button) findViewById(R.id.buttonBac);		
		btnEsquerra = (Button) findViewById(R.id.buttonLef);
		btnDreta = (Button) findViewById(R.id.buttonRig);
		btnStop = (Button) findViewById(R.id.buttonSto);
		txtDebugger = (TextView) findViewById(R.id.textDebugger);
		
		bt = BluetoothAdapter.getDefaultAdapter();
		mb = new mod_Bluetooth(bt, "00001101-0000-1000-8000-00805F9B34FB", btnEndavant, txtDebugger);
		mb.Activa_Client(MAC_Arduino); //Ens connectarem al PIC
		
		btnEndavant.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DC_palante();
			}
		});
		
		btnEndarrera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DC_patras();
			}
		});
		btnEsquerra.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DC_activaIzquierda();
			}
		});
		btnDreta.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DC_activaDerecha();
			}
		});
		btnStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DC_puntoMuerto();
			}
		});
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decepticon);
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