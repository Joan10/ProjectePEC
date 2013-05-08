package com.example.robotspec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
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


public class mod_Bluetooth  {
	private BluetoothAdapter mBluetoothAdapter;
	private boolean Connectat = false; //false no connectat, true en marxa
	
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";

    // Unique UUID for this application
    private UUID MY_UUID_SECURE =
        UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    
    private int mState;
    
    final int RECIEVE_MESSAGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    public static final int MSG_UID=17; 
    
	//Agafem l'objecte "adapter", el dispositiu.
	private BluetoothSocket mmSocket;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private TextView Text_rebut; 
	private StringBuilder sb = new StringBuilder();
	
	private Button btAct;
	Handler h, uidh;
	Message msg;
	int Master = -1;
	
	
	mod_Bluetooth(BluetoothAdapter bt, String tUUID, Button btnActiu, TextView txtrebut) {
		//btnActiu és un botó que s'activarà si es pot enviar.
		Text_rebut = txtrebut;
		btAct = btnActiu; 
		mBluetoothAdapter = bt;
		MY_UUID_SECURE = UUID.fromString(tUUID);
		btAct.setEnabled(false);
		Connectat = false;
	    h = new Handler() {
	    	public void handleMessage(android.os.Message msg) {
	    		switch (msg.what) {
	            case RECIEVE_MESSAGE:			// if receive massage
	            	byte[] readBuf = (byte[]) msg.obj;
	            	String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
	            	sb.append(strIncom);												// append string
	            	Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
	            	//Log, inicialment comentat.
	            	Text_rebut.setText(strIncom);
	            	break;
	    			}
	    		}
	        };
		
	    uidh = new Handler() {
	    	public void handleMessage(android.os.Message msg) {
	    		switch (msg.what) {
	            case MSG_UID:			// if receive massage
	            	Connectat = true;
	            	btAct.setEnabled(true);
	            	break;
	    			}
	    		}
	        };

	}
	/* Ho han de fer de dalt.
	public void onResume() {
		super.onResume();
		Log.d("BT", "...Començant()...");

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}

		

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}*/

	
	boolean estaConnectat(){
		return Connectat;
	}
	
	String Llegeix(){
		String ret = new String(sb.toString());
		sb = new StringBuilder();
		//Buidem el buffer sb i retornem el que hi havia.
		return ret;
		
	}
	
	
	public void Activa_Client(String MAC){
		try {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC);
			mConnectThread = new ConnectThread(device);
			mConnectThread.start();
		} catch (Exception e){
			Log.e("BT", "Error configurant el client!");
		}
	}
	
	public void Activa_Servidor(){
		//try {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		//} catch (Exception e){
		//	Log.e("BT", "Error configurant el servidor");
		//}
		
	}
	
	public void Envia(String s){
		try {
			mConnectedThread.write(s.getBytes());
		} catch (Exception e){
			Log.e("BT", "Error enviant!");
		}
	}
	

	public void Pausa() {
		try {
			mmSocket.close();
			mAcceptThread.cancel();
			mConnectThread.cancel();
			//.cancel() TANCA EL SOCKET però no mata els dimonis
		} catch (Exception e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("BT", "...In onPause()...");
		mBluetoothAdapter.cancelDiscovery();

	}
	
	public void Retorna() {
		mState = STATE_CONNECTING;
		Log.d("BT", "...Tornant...");
	}
	
	/******************************************************************************************/
	/******************************************************************************************/
	private class AcceptThread extends Thread {
	        // The local server socket
	        private final BluetoothServerSocket mmServerSocket;
	        private String mSocketType;

	        public AcceptThread() {
	            BluetoothServerSocket tmp = null;
	            mSocketType ="Secure";

	            // Create a new listening server socket
	            try { 
	                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
	                        MY_UUID_SECURE);
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
	                    synchronized (mod_Bluetooth.this) {
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
            uidh.obtainMessage(MSG_UID, 1, -1, 0)
            .sendToTarget();
	        
            
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

	        public ConnectThread(BluetoothDevice device) {
	            mmDevice = device;
	            BluetoothSocket tmp = null;
	            mSocketType = "Secure";

	            // Get a BluetoothSocket for a connection with the
	            // given BluetoothDevice
	            try {
	                    tmp = device.createRfcommSocketToServiceRecord(
	                            MY_UUID_SECURE);
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
	            synchronized (mod_Bluetooth.this) {
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
	            } catch (Exception e) {Log.e(TAG, "ERROR ESCRIVINT"); }
	        }
	     
	        /* Call this from the main activity to shutdown the connection */
	        public void cancel() {
	            try {
	                mmSocket.close();
	            } catch (IOException e) { }
	        }
	    }
}



