package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.DistanceSensor;
import at.uibk.informatik.androbot.control.IConnection;
import at.uibk.informatik.androbot.control.MessageTypes;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.control.Robot;

public abstract class ProgrammBase {

	private static final String LOG_TAG = "ProgrammBase";
	private Robot robot;
	private IConnection conn;
	private Context context;
	private Handler uiHandler;
	private boolean executing;

	// ******************************************** Constructors ******************************************************

	public ProgrammBase(Context context) {

		this.context = context;
		this.uiHandler = new Handler(new Callback());
		this.executing = false;

		// Load settings
		SharedPreferences settings = context.getSharedPreferences("Androbot_Settings",
				android.content.Context.MODE_PRIVATE);

		// Create the connection
		
		if (settings.getBoolean(SettingsActivity.USE_FAKECONNECTION, false)) {
			conn = null;// new FakeConnection();
		} else {
			BluetoothConnection btc = new BluetoothConnection(context);
			btc.setDeviceAddress("20:13:08:16:10:50");
			conn = btc;
		}

		// Create the robot
		this.robot = new Robot(conn, uiHandler);

		// Set the linear calibration
		this.robot.setAngularCorrection(settings.getFloat(SettingsActivity.ANGULAR_CORRECTION, 2.0f));
		//this.robot.setAngularRuntimePerDegree(settings.getFloat(SettingsActivity.ANGULAR_RUNTIME, 100.0f));
		this.robot.setAngularRuntimePerDegree(21.2);

		// Set the angular calibration
		this.robot.setLinearCorrection(settings.getFloat(SettingsActivity.LINEAR_CORRECTION, 0.5f));
		//this.robot.setLinearRuntimePerCentimeter(settings.getFloat(SettingsActivity.LINEAR_RUNTIME, 100.0f));
		this.robot.setLinearRuntimePerCentimeter(54);
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
			onExecute();
		else
			this.connect();
	}
	
	protected abstract void onExecute();
	
	public boolean hasInteruptRequest() {
		return this.conn.hasInteruptRequest();
	}
	
	public void interruptHandled(){
		this.conn.setInteruptRequest(false);
	}

	// ******************************************** Properties ********************************************************

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}
	
	protected void onSensordataReceived(List<DistanceSensor> sensors){
		
	}
	
	public void moveDistance(int distance){
		
		int runtime = robot.getRuntime(distance);
		
		long start = System.currentTimeMillis();

		robot.moveForward();
		
		long last = start;		
		while (System.currentTimeMillis() < (start + runtime)) {
			
			long now = System.currentTimeMillis();
			
			if(now -last > 250){
				last = now;
				robot.requestSensorData();
				Log.d(LOG_TAG, "Sensors requested");
			}			
			
			if (hasInteruptRequest()) {
				interruptHandled();
				onObstacleDetected();				
				break;			
			}
		}
		robot.stop();
	}

	protected void onObstacleDetected() {
		
		
	}
	
	public void turn(int degrees){		
		
		int runtime = (int) (Math.abs(degrees) * robot.getAngularRuntimePerDegree());
		
		byte l = -9;
		byte r = 9;
		
		if(degrees < 0){
			l *= -1;
			r *= -1;
		}
		robot.setVelocity(l, r);
		
		long start  = System.currentTimeMillis();
		while(System.currentTimeMillis() < start + runtime);
		robot.stop();
	}
	
	public int getAngle(Position current, Position target){
		return (int) Math.toDegrees(Math.atan2(target.getY() - current.getY(), target.getX() - current.getX()));
	}
	
	public int getDistanceToTarget(Position current, Position target){		
		return (int) Math.sqrt(Math.pow(target.getX()-current.getX(), 2) + Math.pow(target.getY()-current.getY(),2));	
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
			case MessageTypes.CONNECTION_STATE_CHANGED:
				switch (msg.arg1) {
				case MessageTypes.CONNECTION_STATE_CONNECTING:
					Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
					break;
				case MessageTypes.CONNECTION_STATE_DISCONNECTED:
					Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
					break;
				case MessageTypes.CONNECTION_STATE_CONNECTED:
					Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
					if (executing){
						start();
					}
					break;
				default:
					Log.w(LOG_TAG, "Unexpected message received: " + msg.toString());
					return false;
				}
				break;
				
			case MessageTypes.ROBOT_EVENT:
				if(msg.arg1 == MessageTypes.ROBOT_SENSORDATA_RECEIVED){
					onSensordataReceived((List<DistanceSensor>)msg.obj);
				}

			case MessageTypes.CONNECTION_MESSAGE_EVENT:
				switch(msg.arg1){
				
				case MessageTypes.CONNECTION_MESSAGE_RECEIVED:
					
					
					break;
				default:
					Log.w(LOG_TAG, "Unexpected: "+ msg.toString());
				}
					
					break;
			// case Robot.ROBOT_RESPONSE_RECEIVED:
			// switch (msg.arg1) {
			//
			// case Robot.POSITION_RECEIVED:
			// onPositionReceived((IPosition) msg.obj);
			// listener.onPositionReceived((IPosition) msg.obj);
			// break;
			//
			// case Robot.SENSOR_DATA_RECEIVED:
			// onSensorDataReceived((List<IDistanceSensor>) msg.obj);
			// listener.onSensorDataReceived((List<IDistanceSensor>) msg.obj);
			// break;
			//
			// case IRobot.IDLE:
			// onRobotIsIdle();
			// break;
			//
			// default:
			// Log.d(LOG_TAG, "Unexpected message received: " + msg.toString());
			// break;
			// }
			// break;

			default:
				Log.w(LOG_TAG, "Unexpected message received: " + msg.toString());
				return false;
			}

			return true;
		};
	}
}
