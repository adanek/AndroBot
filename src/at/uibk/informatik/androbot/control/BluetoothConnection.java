package at.uibk.informatik.androbot.control;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IConnection;

public class BluetoothConnection implements IConnection {

	private BluetoothAdapter btAdapter;
	private BluetoothSocket btSocket;
	private OutputStream outStream;

	private static final UUID SERVICE_ID = UUID.randomUUID();
	private static final String TAG = "BluetoothConnenction";
	private String address;

	public BluetoothConnection(String macAddress) {
		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		this.checkBtState();
		this.setAddress(macAddress);
	}

	@Override
	public void write(byte[] msg) {
		
		Log.d(TAG, "Sending message: " + msg);
		try {
			outStream.write(msg);
		} catch (IOException e) {
			Log.d(TAG, "Exception occured during writing: " + e.getMessage());
		}
	}

	@Override
	public void connect() {

		// Set pointer to the remote device
		BluetoothDevice device = btAdapter.getRemoteDevice(getAddress());

		try {
			btSocket = createSocket(device);
			
		} catch (IOException e) {
			Log.d(TAG, "Can't create socket to remote device");
		}

		// Cancel discovery before opening socket
		btAdapter.cancelDiscovery();

		// Open socket
		try {
			Log.d(TAG, "Try do open socket to remote device...");
			btSocket.connect();
			Log.d(TAG, "Connection established");
		} catch (IOException e) {
			try {
				Log.d(TAG, "Connection failed");
				btSocket.close();
			} catch (IOException e_closing) {
				Log.d(TAG, "Can't close socket: " + e_closing.getMessage());
			}
		}
		
		// Create output stream
		try{
			Log.d(TAG, "Creating stream...");
			outStream = btSocket.getOutputStream();
			Log.d(TAG, "OutputStream ready");
		}catch(IOException e){
			Log.d(TAG, "Stream creation failed: "+ e.getMessage());			
		}
	}

	@Override
	public void disconnect(){
		
		if(outStream != null){
			try {
				outStream.flush();
			} catch (IOException e) {
				Log.d(TAG, "Failed to flush the outputstream: " + e.getMessage());
			}
		}
		
		try {
			btSocket.close();
		} catch (IOException e) {
			Log.d(TAG, "Faild to close the socket: " + e.getMessage());
		}
	}
	
	private void checkBtState() {

		// Ensure Bluetooth is supported
		if (this.btAdapter == null)
			throw new UnsupportedOperationException(
					"Bluetooth is not supported");

		// Ensure Bluetooth is enabled
		if (!this.btAdapter.isEnabled()) {
			btAdapter.enable();
		}

	}

	private BluetoothSocket createSocket(BluetoothDevice device)
			throws IOException {

		return device.createRfcommSocketToServiceRecord(SERVICE_ID);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
