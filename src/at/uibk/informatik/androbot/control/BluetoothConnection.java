package at.uibk.informatik.androbot.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IConnection;

public class BluetoothConnection implements IConnection {

	// **********************************************************************************************
	// Locals
	// **********************************************************************************************

	private BluetoothAdapter btAdapter;

	// private static final UUID SERVICE_ID =
	// UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = "BluetoothConnenction";
	private String address;

	private ConnectThread tConnect;
	private BtDataExchangeThread tConnected;

	// **********************************************************************************************
	// Constructors
	// **********************************************************************************************

	public BluetoothConnection(String macAddress) {
		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		this.checkBtState();
		this.setAddress(macAddress);
	}

	// **********************************************************************************************
	// Properties
	// **********************************************************************************************

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	// **********************************************************************************************
	// Methods
	// **********************************************************************************************

	@Override
	public void connect() {
		// Ensure Bluetooth is enabled
		this.checkBtState();

		// Set pointer to the remote device
		BluetoothDevice device = btAdapter.getRemoteDevice(getAddress());

		// Check if another thread is running
		if (this.tConnect != null && this.tConnect.isAlive())
			this.tConnect.cancel();

		// Create a new Thread which tries to connect to the device
		this.tConnect = new ConnectThread(device);
		this.tConnect.start();
	}

	@Override
	public void disconnect() {
		// Cancel connect thread
		if (this.tConnect != null && this.tConnect.isAlive())
			this.tConnect.cancel();

		// Cancel connected thread
		if (this.tConnected != null && this.tConnected.isAlive())
			this.tConnected.cancel();
	}

	@Override
	public void sendCommand(byte[] command) {
		if (this.tConnected == null || !this.tConnected.isAlive())
			throw new IllegalStateException(
					"The connection must be connected before writing");

		Log.d(TAG, "Sending message: " + new String(command));
		this.tConnected.sendCommand(command);
	}

	@Override
	public String getResponse(byte[] command) {
		if (this.tConnected == null || !this.tConnected.isAlive())
			throw new IllegalStateException(
					"The connection must be connected before writing");

		Log.d(TAG, "Sending message: " + new String(command));
		String response = this.tConnected.getResponse(command);

		return response;
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

	private void manageConnectedSocket(BluetoothSocket socket) {

		Log.d(TAG, "Connection established");

		this.tConnected = new BtDataExchangeThread(socket);
		this.tConnected.start();
	}

	// **********************************************************************************************
	// Helper Classes
	// **********************************************************************************************

	// Encapsulates the connection to the remote device in own thread
	private class ConnectThread extends Thread {

		private final BluetoothSocket btSocket;
		private final String TAG = "BtConnectThread";

		public ConnectThread(BluetoothDevice device) {
			// tmp variable because btSocket is final (Hint from android
			// documentation)
			BluetoothSocket socket = null;

			try {
				// Doesn't work for some reason
				// socket =
				// device.createRfcommSocketToServiceRecord(SERVICE_ID);

				// Workaround found on StackOverflow
				Method m;
				m = device.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
			} catch (Exception e) {
			}

			btSocket = socket;
			Log.d(TAG, "Thread created");
		}

		public void run() {

			Log.d(TAG, "Thread started");

			// The discovery should be deactivated before opening a socket
			btAdapter.cancelDiscovery();

			try {
				Log.d(TAG, "Try to open a socket to the remote device");
				btSocket.connect();

			} catch (IOException connectException) {
				try {
					Log.d(TAG,
							"Socket connection failed:\n"
									+ connectException.getMessage());
					btSocket.close();
				} catch (IOException closeException) {
					Log.d(TAG, "Closing socket failed");
				}
				return;
			}

			Log.d(TAG, "Socket connected");
			manageConnectedSocket(btSocket);
		}

		public void cancel() {

			Log.d(TAG, "Connection canceld");
			try {
				btSocket.close();
			} catch (IOException e) {
				Log.d("ConnectThread", "Failed to close socket");
			}
		}
	}

	// Encapsulates the data exchange with the remote device
	private class BtDataExchangeThread extends Thread {

		private final String LOG_TAG = "BtDataExchangeThread";
		private final BluetoothSocket btSocket;
		private final InputStream btInStream;
		private final OutputStream btOutStream;
		private boolean running = true;

		public BtDataExchangeThread(BluetoothSocket socket) {
			this.btSocket = socket;

			// Use temporary objects because the lager ones are final
			InputStream in = null;
			OutputStream out = null;

			try {
				in = socket.getInputStream();
				out = socket.getOutputStream();
			} catch (IOException e) {
				Log.d(LOG_TAG,
						"Unable to get stream from socket: \n" + e.getMessage());
			}

			btInStream = in;
			btOutStream = out;

			Log.d(LOG_TAG, "Thread created");
		}

		// Receives messages from remote device
		public void run() {

			Log.d(LOG_TAG, "Start to listen on socket for incoming messages");

			while (true) {

				if (this.running) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				} else {
					break;
				}
			}

			Log.d(LOG_TAG, "Thread finished");
		}

		// Sends a command to the remote device
		public void sendCommand(byte[] command) {
			Log.d(LOG_TAG, "Writing message: " + new String(command));

			sendData(command);
		}

		// Receives a response from the remote device to a given command
		public String getResponse(byte[] command) {

			this.flushInputStream();
			sendData(command);
			String response = receiveData();
			return response;
		}

		public void cancel() {
			Log.d(LOG_TAG, "Thread canceled");
			
			this.running = false;

			try {
				btOutStream.flush();
			} catch (IOException e) {
				Log.d(LOG_TAG, "Failed to fluse outgoing stream");
			}

			try {
				btSocket.close();
			} catch (IOException e) {
				Log.d(LOG_TAG, "Failed to close the connection");
			}
		}

		private void sendData(byte[] data) {
			try {
				btOutStream.write(data);
			} catch (IOException e) {
				Log.d(LOG_TAG, "Failed to write data: \n" + e.getMessage());
			}
		}

		private String receiveData() {
			byte[] buffer = new byte[1024];
			int bytes = 0;

			try {
				bytes = btInStream.read(buffer);
			} catch (IOException e) {
			}

			String msg = new String(buffer, 0, bytes);
			Log.d(LOG_TAG, "Received message: " + msg);
			return msg;
		}
		
		private void flushInputStream(){
			try {
				if(btInStream.available()> 0){
					this.receiveData();
				}
			} catch (IOException e) {
			}
		}
	}
}
