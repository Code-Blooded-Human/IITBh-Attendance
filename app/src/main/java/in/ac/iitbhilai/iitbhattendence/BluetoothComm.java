package in.ac.iitbhilai.iitbhattendence;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Use of this class, you need to have the following two permissions <br /> 
  * & Lt; uses-permission android: name = "android.permission.BLUETOOTH" / & gt; <br /> 
  * & Lt; uses-permission android: name = "android.permission.BLUETOOTH_ADMIN" / & gt; <br /> 
  * Android supported versions LEVEL 4 or more, and LEVEL 17 support bluetooth 4 of ble equipment
 * */
@SuppressLint("NewApi")
public class BluetoothComm{
	/**Service UUID*/
	public final static String UUID_STR = "00001101-0000-1000-8000-00805F9B34FB";
	/**Bluetooth address code*/
	private String msMAC;
	/**Bluetooth connection status*/
	private boolean mbConectOk = false;

	/* Get Default Adapter */
	private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
	/**Bluetooth serial port connection object*/
	private BluetoothSocket mbsSocket = null;
	/** Input stream object */
	public static InputStream misIn = null;
	/** Output stream object */
	public static OutputStream mosOut = null;
	//private String sbtName = "";
	/**Constant: The current Adnroid SDK version number*/
	private static final int SDK_VER;
	public static boolean BTconnected = false;
	static{
		SDK_VER = Build.VERSION.SDK_INT;
	};

	/**
	 * Constructor 
	 * @param sMAC Bluetooth device MAC address required to connect
	 * */
	public BluetoothComm(String sMAC){
		Log.d(TAG, "smac:"+ sMAC);
		this.msMAC = sMAC;
	}


	/**
	 * Disconnect the Bluetooth device connection
	 * @return void
	 * */
	public void closeConn(){
		if ( this.mbConectOk ){
			this.BTconnected = false;
			this.mbConectOk = false;//Mark the connection has been closed
			try{
				if (null != this.misIn)
					this.misIn.close();
				if (null != this.mosOut)
					this.mosOut.close();
				if (null != this.mbsSocket)
					this.mbsSocket.close();
			}catch (IOException e){
				//Any part of the error, will be forced to close socket connection
				this.misIn = null;
				this.mosOut = null;
				this.mbsSocket = null;
				this.mbConectOk = false;//Mark the connection has been closed
			}
		}
		Log.e(TAG, " Closed connection");
	}
	private static final String TAG = "Prowess BT Comm";
	/**
	 * Bluetooth devices establish serial communication connection <br /> 
	 * This function is best to put the thread to call, because it will block the system when calling 
	 * @return Boolean false: connection creation failed / true: the connection is created successfully
	 * */
	final public boolean createConn(){
		if (! mBT.isEnabled())
			return false;
		Log.e(TAG,"bt comm .....crete connection  1");
		//If a connection already exists, disconnect
		if (mbConectOk)
			this.closeConn();
		Log.e(TAG,".....crete connection  1");
		/*Start Connecting a Bluetooth device*/
		//	final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.msMAC);
		Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
		final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
		for(int i = 0; i < pairedObjects.length; ++i) {
			pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
		}
		int i=0;
		Log.d(TAG, "btnamee:"+MainActivity.Sbtname);
		try{
		//final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(BluetoothComm.this, android.R.layout.simple_list_item_1);
			for ( i= 0; i < pairedDevices.length; ++i) {
				String st = pairedDevices[i].getName();
				//mArrayAdapter.add(st);
				Log.i(TAG, "<"+i+"> : "+st);

				//Log.e("BT Config","........>"+MainActivity.btname);
				Log.e("BT Config","........>"+MainActivity.Sbtname);
				/*Editor editor = MainActivity.sharedpreferences.edit();
	          editor.putString(MainActivity.Name, btname);
	          editor.commit(); */
			


				if (MainActivity.Sbtname.equals(pairedDevices[i].getName())){
					

					//Log.e(TAG, ">>>>>Found the Device............! : WPFF040000100001");
					//wisePadController.startBTv2(pairedDevices[i]);
					Thread.sleep(2000);
					final UUID uuidComm = UUID.fromString(UUID_STR);
					try{
						this.mbsSocket = pairedDevices[i].createRfcommSocketToServiceRecord(uuidComm);
						Thread.sleep(2000);
						Log.e(TAG, ">>> Connecting ");
						this.mbsSocket.connect();
						Log.e(TAG, ">>> CONNECTED SUCCESSFULLY");
						Thread.sleep(2000);
						mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
						misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
						mbConectOk = true; //Device is connected successfully
						BTconnected = true;
						return true;
					}catch (Exception e){
						try {
							Thread.sleep(2000);
							Log.e(TAG, ">>>>>>           Try 2  ................!");
							this.mbsSocket = pairedDevices[i].createInsecureRfcommSocketToServiceRecord(uuidComm);
							Log.e(TAG, " Socket obtained");
							Thread.sleep(2000);
							Log.e(TAG, " Connecting againg ");
							this.mbsSocket.connect();
							Log.e(TAG, " Successful connection 2nd time....... ");
							Thread.sleep(2000);
							this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
							this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
							this.mbConectOk = true;
							this.BTconnected = true;
							return true;
						} catch (IOException e1) {
							Log.e(TAG, " Connection Failed by trying both ways....... ");
							e1.printStackTrace();
							this.closeConn();//Disconnect
							Log.e(TAG, " Returning False");
							this.mbConectOk = false;
							this.BTconnected = false;
							return false;
						} catch (Exception ee){
							Log.e(TAG, " Connection Failed due to other reasons....... ");
							ee.printStackTrace();
							this.closeConn();//Disconnect
							Log.e(TAG, " Returning False");
							this.mbConectOk = false;
							this.BTconnected = false;
							return false;
						}
					}
				}
			}
		} catch(Exception e) {
			Log.e(MainActivity.TAG,"Device is not configer"); 
			//MainActivity.btname="ESBAA24555";  
		}
		if (i==pairedDevices.length){
			Log.e(MainActivity.TAG,"Device is not paired");
		}
		
		return false;
		//final UUID uuidComm = UUID.fromString(UUID_STR);
		/*try{
			this.mbsSocket = device.createRfcommSocketToServiceRecord(uuidComm);
			Thread.sleep(2000);
            Log.e(TAG, ">>> Connecting ");
			this.mbsSocket.connect();
			Log.e(TAG, ">>> CONNECTED SUCCESSFULLY");
			Thread.sleep(2000);
			this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
			this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
			this.mbConectOk = true; //Device is connected successfully

		}catch (Exception e){
			try {
				Thread.sleep(2000);
				Log.e(TAG, ">>>>>>           Try 2  ................!");
				this.mbsSocket = device.createInsecureRfcommSocketToServiceRecord(uuidComm);
				Log.e(TAG, " Socket obtained");
				Thread.sleep(2000);
				Log.e(TAG, " Connecting againg ");
				this.mbsSocket.connect();
				Log.e(TAG, " Successful connection 2nd time....... ");
				Thread.sleep(2000);
				this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
				this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
				this.mbConectOk = true;
			} catch (IOException e1) {
				Log.e(TAG, " Connection Failed by trying both ways....... ");
				e1.printStackTrace();
				this.closeConn();//Disconnect
				Log.e(TAG, " Returning False");
				return false;
			} catch (Exception ee){
				Log.e(TAG, " Connection Failed due to other reasons....... ");
				ee.printStackTrace();
				this.closeConn();//Disconnect
				Log.e(TAG, " Returning False");
				return false;
			}
		}*/
		//return true;
	}

	
	final public boolean createConn1(){
		if (! mBT.isEnabled())
			return false;
		Log.e(TAG,".....crete connection  1");
		//If a connection already exists, disconnect
		if (mbConectOk)
			this.closeConn();
		Log.e(TAG,".....crete connection  1");
		/*Start Connecting a Bluetooth device*/
    	final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.msMAC);
    	final UUID uuidComm = UUID.fromString(UUID_STR);
		try{
			this.mbsSocket = device.createInsecureRfcommSocketToServiceRecord(uuidComm);
			/*this.mbsSocket = device.createRfcommSocketToServiceRecord(uuidComm);*/
			Thread.sleep(2000);
            Log.e(TAG, ">>> Connecting ");
			this.mbsSocket.connect();
			Log.e(TAG, ">>> CONNECTED SUCCESSFULLY");
			Thread.sleep(2000);
			this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
			this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
			this.mbConectOk = true; //Device is connected successfully
			BTconnected = true;
		}catch (Exception e){
			try {
				Thread.sleep(2000);
				Log.e(TAG, ">>>>>>           Try 2  ................!");
				this.mbsSocket = device.createRfcommSocketToServiceRecord(uuidComm);
				/*this.mbsSocket = device.createInsecureRfcommSocketToServiceRecord(uuidComm);*/
				Log.e(TAG, " Socket obtained");
				Thread.sleep(2000);
				Log.e(TAG, " Connecting againg ");
				this.mbsSocket.connect();
				Log.e(TAG, " Successful connection 2nd time....... ");
				Thread.sleep(2000);
				this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
				this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
				this.mbConectOk = true;
				BTconnected = true;
			} catch (IOException e1) {
				Log.e(TAG, " Connection Failed by trying both ways....... ");
				e1.printStackTrace();
				this.closeConn();//Disconnect
				Log.e(TAG, " Returning False");
				this.mbConectOk = false;
				this.BTconnected = false;
				return false;
			} catch (Exception ee){
				Log.e(TAG, " Connection Failed due to other reasons....... ");
				ee.printStackTrace();
				this.closeConn();//Disconnect
				Log.e(TAG, " Returning False");
				this.mbConectOk = false;
				this.BTconnected = false;
				return false;
			}
		}
		return true;
	}
	/**
	 * If the communication device has been established 
	 * @return Boolean true: communication has been established / false: communication lost
	 * */
	public boolean isConnect()	{
		return this.mbConectOk;
	}


}
