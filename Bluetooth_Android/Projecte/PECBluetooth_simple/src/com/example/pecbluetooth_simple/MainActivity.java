package com.example.pecbluetooth_simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class MainActivity extends Activity {
	private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
	BluetoothAdapter mBluetoothAdapter;
	
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
        UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    
    private int mState;
    
    final int RECIEVE_MESSAGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
	//Agafem l'objecte "adapter", el dispositiu.
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
	private BluetoothSocket mmSocket;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	
	private StringBuilder sb = new StringBuilder();
	 
	Handler h;
	Message msg;
	int Master = -1;
	
	Button btnMa, btnSl, btnEnv, btnLle;
	TextView txtquisoc, txtquefaig;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnMa = (Button) findViewById(R.id.btnMaster);					
		btnSl = (Button) findViewById(R.id.btnSlave);
		btnEnv = (Button) findViewById(R.id.btnEnvia);
		btnLle= (Button) findViewById(R.id.btnLlegeix);
		
		txtquisoc = (TextView) findViewById(R.id.quisoc);
		txtquefaig = (TextView) findViewById(R.id.quefaig);
		
		btnMa.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				btnMa.setEnabled(false);
				btnSl.setEnabled(false);
				Master = 1;
				
				try { mConnectThread.cancel();}catch (Exception e) { }
				mAcceptThread = new AcceptThread(true);
				mAcceptThread.start();
				
				txtquisoc.setText("Sóc màster");
				//Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
				//mConnectedThread és l'objecte encarregat d'enviar dades, d'escriure al buffer
			}
		});

		btnSl.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				btnMa.setEnabled(false);
				btnSl.setEnabled(false);
				Master = 0;
				
				try { mAcceptThread.cancel();}catch (Exception e) { }
				//BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("90:C1:15:BE:65:83");
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("90:C1:15:BE:65:83");
				mConnectThread = new ConnectThread(device, true);
				mConnectThread.start();
				txtquisoc.setText("Sóc slave");
			}
		});
		
		btnEnv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mConnectedThread.write(" FUU FUU FUU ".getBytes());
			}
		});
		
		btnLle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mConnectedThread.read();
			}
		});	
		
		
	    h = new Handler() {
	    	public void handleMessage(android.os.Message msg) {
	    		switch (msg.what) {
	            case RECIEVE_MESSAGE:			// if receive massage
	            	byte[] readBuf = (byte[]) msg.obj;
	            	String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
	            	sb.append(strIncom);												// append string
	            	Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
	            	//Log, inicialment comentat.
	            	break;
	    			}
	    		}
	        };
		
		

	}
	@Override
	public void onResume() {
		super.onResume();
		Log.d("BT", "...Començant()...");

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}

		mState = STATE_CONNECTING;

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}

		/*mBluetoothAdapter.startDiscovery();

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {

				// Add the name and address to an array adapter to show in a ListView
		        Log.d("paired",device.getName() + "\n" + device.getAddress());
		    }
		}

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			mmSocket.close();
			mAcceptThread.cancel();
			mConnectThread.cancel();
		} catch (Exception e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("BT", "...In onPause()...");
		mBluetoothAdapter.cancelDiscovery();

	}
	
	/******************************************************************************************/
	/******************************************************************************************/
	 private class AcceptThread extends Thread {
	        // The local server socket
	        private final BluetoothServerSocket mmServerSocket;
	        private String mSocketType;

	        public AcceptThread(boolean secure) {
	            BluetoothServerSocket tmp = null;
	            mSocketType = secure ? "Secure":"Insecure";

	            // Create a new listening server socket
	            try {
	                if (secure) {
	                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
	                        MY_UUID_SECURE);
	                } else {
	                    tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
	                            NAME_INSECURE, MY_UUID_INSECURE);
	                }
	            } catch (IOException e) {
	                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
	            }
	            mmServerSocket = tmp;
	        }

	        public void run() {
	            if (D) Log.d(TAG, "Socket Type: " + mSocketType +
	                    "BEGIN mAcceptThread" + this);
	            setName("AcceptThread" + mSocketType);

	            BluetoothSocket socket = null;

	            // Listen to the server socket if we're not connected
	            while (mState != STATE_CONNECTED) {
	                try {
	                    // This is a blocking call and will only return on a
	                    // successful connection or an exception
	                    socket = mmServerSocket.accept();
	                } catch (IOException e) {
	                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
	                    break;
	                }

	                // If a connection was accepted
	                if (socket != null) {
	                    synchronized (MainActivity.this) {
	                        switch (mState) {
	                        case STATE_LISTEN:
	                        case STATE_CONNECTING:
	                            // Situation normal. Start the connected thread.
	                            connected(socket, socket.getRemoteDevice(),
	                                    mSocketType);
	                            break;
	                        case STATE_NONE:
	                        case STATE_CONNECTED:
	                            // Either not ready or already connected. Terminate new socket.
	                            try {
	                                socket.close();
	                            } catch (IOException e) {
	                                Log.e(TAG, "Could not close unwanted socket", e);
	                            }
	                            break;
	                        }
	                    }
	                }
	            }
	            if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

	        }

	        public void cancel() {
	            if (D) Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
	            try {
	                mmServerSocket.close();
	            } catch (IOException e) {
	                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
	            }
	        }
	    }

	    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
	            device, final String socketType) {
	        if (D) Log.d(TAG, "connected, Socket Type:" + socketType);

	        // Cancel the thread that completed the connection
	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

	        // Cancel any thread currently running a connection
	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	        // Start the thread to manage the connection and perform transmissions
	        mConnectedThread = new ConnectedThread(socket);
	        mConnectedThread.start();

	        // Send the name of the connected device back to the UI Activity
	       // Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
	       // Bundle bundle = new Bundle();
	        //bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
	       // msg.setData(bundle);
	        //mHandler.sendMessage(msg);

	        mState = STATE_CONNECTED;
	        }
	    /**
	     * This thread runs while attempting to make an outgoing connection
	     * with a device. It runs straight through; the connection either
	     * succeeds or fails.
	     */
		/******************************************************************************************/
		/******************************************************************************************/
	    private class ConnectThread extends Thread {
	        private final BluetoothSocket mmSocket;
	        private final BluetoothDevice mmDevice;
	        private String mSocketType;

	        public ConnectThread(BluetoothDevice device, boolean secure) {
	            mmDevice = device;
	            BluetoothSocket tmp = null;
	            mSocketType = secure ? "Secure" : "Insecure";

	            // Get a BluetoothSocket for a connection with the
	            // given BluetoothDevice
	            try {
	                if (secure) {
	                    tmp = device.createRfcommSocketToServiceRecord(
	                            MY_UUID_SECURE);
	                } else {
	                    tmp = device.createInsecureRfcommSocketToServiceRecord(
	                            MY_UUID_INSECURE);
	                }
	            } catch (IOException e) {
	                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
	            }
	            mmSocket = tmp;
	        }

	        public void run() {
	            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
	            setName("ConnectThread" + mSocketType);

	            // Always cancel discovery because it will slow down a connection
	            mBluetoothAdapter.cancelDiscovery();

	            // Make a connection to the BluetoothSocket
	            try {
	                // This is a blocking call and will only return on a
	                // successful connection or an exception
	                mmSocket.connect();
	            } catch (IOException e) {
	                // Close the socket
	                try {
	                    mmSocket.close();
	                } catch (IOException e2) {
	                    Log.e(TAG, "unable to close() " + mSocketType +
	                            " socket during connection failure", e2);
	                }
	                //connectionFailed();
	                return;
	            }

	            // Reset the ConnectThread because we're done
	            synchronized (MainActivity.this) {
	                mConnectThread = null;
	            }

	            // Start the connected thread
	            connected(mmSocket, mmDevice, mSocketType);
	        }

	        public void cancel() {
	            try {
	                mmSocket.close();
	            } catch (IOException e) {
	                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
	            }
	        }
	    }
		/******************************************************************************************/
		/******************************************************************************************/
	    private class ConnectedThread extends Thread {
	        private final BluetoothSocket mmSocket;
	        private final InputStream mmInStream;
	        private final OutputStream mmOutStream;
	     
	        public ConnectedThread(BluetoothSocket socket) {
	            mmSocket = socket;
	            InputStream tmpIn = null;
	            OutputStream tmpOut = null;
	     
	            // Get the input and output streams, using temp objects because
	            // member streams are final
	            try {
	                tmpIn = socket.getInputStream();
	                tmpOut = socket.getOutputStream();
	            } catch (Exception e) { }
	     
	            mmInStream = tmpIn;
	            mmOutStream = tmpOut;
	        }
	     
	        public void run() {
	            byte[] buffer = new byte[1024];  // buffer store for the stream
	            int bytes; // bytes returned from read()
	     
	            // Keep listening to the InputStream until an exception occurs
	            while (true) {
	                try {
	                    // Read from the InputStream
	                    bytes = mmInStream.read(buffer);
	                    // Send the obtained bytes to the UI activity
	                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)
	                            .sendToTarget();
	                } catch (Exception e) {
	                    break;
	                }
	            }
	        }

	        public void read() {
	            try {
	            	int bytes;
	            	byte[] buffer = new byte[1024];
	            	
	            	Log.e(TAG, "Llegint...");
	                bytes = mmInStream.read(buffer);
	                Log.e(TAG, "Llegint..."+buffer.toString());
	                
	            } catch (IOException e) { }
	        }
	        
	        /* Call this from the main activity to send data to the remote device */
	        public void write(byte[] bytes) {
	            try {
	                mmOutStream.write(bytes);
	                Log.e(TAG, "Escrivint...");
	            } catch (IOException e) { }
	        }
	     
	        /* Call this from the main activity to shutdown the connection */
	        public void cancel() {
	            try {
	                mmSocket.close();
	            } catch (IOException e) { }
	        }
	    }
}



