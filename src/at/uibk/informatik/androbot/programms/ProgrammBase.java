package at.uibk.informatik.androbot.programms;

import static at.uibk.informatik.androbot.contracts.Constants.DEVICE_NAME;
import static at.uibk.informatik.androbot.contracts.Constants.MESSAGE_DEVICE_NAME;
import static at.uibk.informatik.androbot.contracts.Constants.MESSAGE_STATE_CHANGE;
import static at.uibk.informatik.androbot.contracts.Constants.MESSAGE_TOAST;
import static at.uibk.informatik.androbot.contracts.Constants.TOAST;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.FakeConnection;
import at.uibk.informatik.androbot.control.Robot;

public abstract class ProgrammBase implements IRobotResponseCallback{

	private static final String LOG_TAG = "ProgrammBase";
	private IRobot robot;
	private Context context;
	private IRobotResponseCallback listener;
	private Handler uiHandler;
	private boolean executing;

	// ******************************************** Constructors ******************************************************

	public ProgrammBase(Context context, IRobotResponseCallback listener) {

		this.context = context;
		this.listener = listener;
		this.uiHandler = new Handler(new Callback());
		this.executing = false;

		// Load settings
		SharedPreferences settings = context.getSharedPreferences("Androbot_Settings", android.content.Context.MODE_PRIVATE);
		
		// Create the connection
		IConnection conn;
		if (settings.getBoolean(SettingsActivity.USE_FAKECONNECTION, false)) {
			conn = new FakeConnection();
		} else {
			BluetoothConnection btc = new BluetoothConnection(context);
			btc.setDeviceAddress(SettingsActivity.MacAddress);
			conn = btc;
		}

		// Create the robot		
		this.robot = new Robot(conn, uiHandler);
		this.robot.setAngularCorrection(SettingsActivity.AngularCorrecion);
		this.robot.setAngularRuntimePerDegree(SettingsActivity.AngularRuntimePerDegree);
		this.robot.setLinearCorrection(settings.getFloat(SettingsActivity.LINEAR_CORRECTION, 1.0f));
		this.robot.setLinearRuntimePerCentimeter(settings.getFloat(SettingsActivity.LINEAR_RUNTIME, 1.0f));
	}

	// ********************************************** Methods *********************************************************

	public void connect() {
		this.robot.connect();
	}

	public void disconnect() {

		this.robot.disconnect();
	}

	public void start() {

		this.executing = true;

		if (robot.isConnected())
			this.execute();
		else
			this.connect();
	}

	public void stop() {
		this.executing = false;
		this.robot.stop(true);
	}

	private void execute() {
		onExecute();

	}

	protected abstract void onExecute();
	
	protected void onRobotIsIdle(){
		Log.d(LOG_TAG, "Robot is idle");
	}

	// ******************************************** Properties ********************************************************

	public IRobot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	// ********************************************** Classes *********************************************************

	/**
	 * Handles incoming messages from the robot
	 */
	private class Callback implements Handler.Callback {

		private static final String LOG_TAG = "ROBOT HANDLER";

		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case IConnection.STATE_CONNECTING:
					Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
					break;
				case IConnection.STATE_NONE:
					Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
					break;
				case IConnection.STATE_CONNECTED:
					if (executing)
						execute();
				}
				break;

			case MESSAGE_DEVICE_NAME:
				String connectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(context, "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();

				break;
			case MESSAGE_TOAST:
				Toast.makeText(context, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;

			case IRobot.ROBOT_RESPONSE_RECEIVED:
				switch (msg.arg1) {

				case IRobot.POSITION_RECEIVED:
					onPositionReceived((IPosition) msg.obj);
					listener.onPositionReceived((IPosition) msg.obj);
					break;
				case IRobot.SENSOR_DATA_RECEIVED:
					onSensorDataReceived((List<IDistanceSensor>) msg.obj);
					listener.onSensorDataReceived((List<IDistanceSensor>) msg.obj);
					break;
				case IRobot.IDLE:
					onRobotIsIdle();
					break;
				default:
					Log.d(LOG_TAG, "Unexpected message received: " + msg.toString());
					break;
				}
				break;

			default:
				Log.w(LOG_TAG, "Unexpected message received: " + msg.toString());
				return false;
			}

			return true;
		};
	}
}
