package com.example.testapp2;

import java.util.Date;

import Sprint.BluetoothConnection;
import Sprint.Constants;
import Sprint.IConnection;
import Sprint.IRequest;
import Sprint.IRobot;
import Sprint.MoveDistanceRequest;
import Sprint.Robot;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.DateTimeKeyListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView state;
	private TextView output;
	private IRobot robot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.output = (TextView) findViewById(R.id.textView2);
		this.state = (TextView) findViewById(R.id.textView1);

		BluetoothConnection connection = new BluetoothConnection(getApplicationContext());
		//connection.setDeviceAddress("00:26:83:30:F7:E8");
		connection.setDeviceAddress("0C:8B:FD:CC:54:51");
		Robot r = new Robot(connection);
		r.setCaller(mHandler);		
		this.robot = r;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		this.robot.connect();
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		robot.disconnect();		
		//BluetoothAdapter.getDefaultAdapter().disable();
	}

	public void onClick(View view) {
		
		robot.moveDistance(1000);
	}
	
	public void btnStop_Click(View view){
		robot.stop();
	}

	/**
	 * The Handler that gets information back from the BluetoothConnection
	 */
	private final Handler mHandler = new Handler() {
		private String connectedDeviceName;

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case Constants.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case IConnection.STATE_CONNECTED:
					state.setText("Connected");
					break;
				case IConnection.STATE_CONNECTING:
					state.setText("Connecting ...");
					break;				
				case IConnection.STATE_NONE:
					state.setText("Disconected");
					break;
				}
				break;
			case Constants.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				break;
			case Constants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				Toast.makeText(getApplicationContext(), "Received: " + readMessage, Toast.LENGTH_SHORT).show();
				// mConversationArrayAdapter.add(mConnectedDeviceName + ":  " +
				// readMessage);
				break;
			case Constants.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();

				break;
			case Constants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
				break;
			case IRequest.REQUEST_EVENT:
				switch(msg.arg1){
				case IRequest.REQUEST_SENT:
					String out = String.format("%s\nMove request sent %s", output.getText(), new Date().toString()); 
					output.setText(out);
					break;
				}
			}
		}
	};
}
