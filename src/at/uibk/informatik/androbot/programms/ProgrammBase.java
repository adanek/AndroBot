package at.uibk.informatik.androbot.programms;

import static at.uibk.informatik.androbot.contracts.Constants.DEVICE_NAME;
import static at.uibk.informatik.androbot.contracts.Constants.MESSAGE_DEVICE_NAME;
import static at.uibk.informatik.androbot.contracts.Constants.MESSAGE_STATE_CHANGE;
import static at.uibk.informatik.androbot.contracts.Constants.MESSAGE_TOAST;
import static at.uibk.informatik.androbot.contracts.Constants.TOAST;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;

public abstract class ProgrammBase {

	private static double angularCorr;
	private static double linearCorr;
	private IRobot robot;
	private Context context;
	//private IRobotResponseCallback listener;

	public ProgrammBase(Context context, IRobotResponseCallback listener) {
		
		this.context = context;
		//this.listener = listener;
		
		BluetoothConnection conn = new BluetoothConnection(context);
		conn.setDeviceAddress(SettingsActivity.MacAddress);
		
		this.robot = new Robot(conn, uiHandler, listener);
		this.robot.setAngularCorrection(SettingsActivity.AngularCorrecion);
		this.robot.setLinearCorrection(SettingsActivity.LinearCorrection);
	}

	public void start() {
		this.connect();		
	}

	public void connect() {
		this.robot.connect();
	}

	public void disconnect() {

		this.end();
	}

	private void execute(){
		onExecute();
		end();
	}
	
	protected abstract void onExecute();

	public void end() {
		this.robot.stop();
		this.getRobot().disconnect();
	}

	public IRobot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public static double getAngularCorr() {
		return angularCorr;
	}

	public static void setAngularCorr(double angularCorr) {
		ProgrammBase.angularCorr = angularCorr;
	}

	public static double getLinearCorr() {
		return linearCorr;
	}

	public static void setLinearCorr(double linearCorr) {
		ProgrammBase.linearCorr = linearCorr;
	}
	
	private Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case IConnection.STATE_CONNECTING:
					Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT)
							.show();
					break;
				case IConnection.STATE_NONE:
					Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT)
							.show();
					break;
				case IConnection.STATE_CONNECTED:
					execute();
				}
				break;

			case MESSAGE_DEVICE_NAME:
				String connectedDeviceName = msg.getData().getString(
						DEVICE_NAME);
				Toast.makeText(context, "Connected to " + connectedDeviceName,
						Toast.LENGTH_SHORT).show();

				break;
			case MESSAGE_TOAST:
				Toast.makeText(context, msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
				
//			case IRobot.SENSOR_DATA_RECEIVED:
//				@SuppressWarnings("unchecked")
//				List<IDistanceSensor> data = (List<IDistanceSensor>) msg.obj;
//				listener.onSensorDataReceived(data);
//				break;
//			case IRobot.POSITION_RECEIVED:
//				IPosition pos = (IPosition) msg.obj;
//				listener.onPositionDataReceived(pos);
			default:
				Log.w("UI Handler",
						"Unexpected message received: " + msg.toString());

			}
		};
	};

}
