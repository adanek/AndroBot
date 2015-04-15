package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class SquareTest extends ProgrammBase {

	private static final int SENSORS = 1;
	private static final int POSITION = 2;
	private static final String LOG_TAG = "SquareTest";
	private int distance;

	private Handler timer;
	private boolean running;

	public SquareTest(Context context, IRobotResponseCallback listener) {
		super(context, listener);

		this.timer = new Handler(new Callback());
	}

	@Override
	protected void onExecute() {

		// Log
		Log.d(LOG_TAG, "Square Test started");
		Log.d(LOG_TAG, "Distance in cm " + Integer.valueOf(distance));

		this.running = true;
		
		// Think about encapsulating the obstacale logic.

		// Start
		// Get start position (Odometry)

		// Start Sensors
		this.requestSensors();

		IRobot robot = super.getRobot();

		// start square test
		robot.moveDistance(distance);
		robot.turnLeft();
		robot.moveDistance(distance);
		robot.turnLeft();
		robot.moveDistance(distance);
		robot.turnLeft();
		robot.moveDistance(distance);
		robot.turnLeft();

		// STtop sensors

		// Get final position (Odomentry)

		// Show difference

		// Display startPosition, endPosition and difference on screen

	}
	
	@Override protected void onRobotIsIdle() {
		super.onRobotIsIdle();
		
		this.running = false;
	};

	private void requestSensors() {
		Message req = this.timer.obtainMessage(SENSORS);
		timer.sendMessageDelayed(req, 500);		
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {

		for (IDistanceSensor s : sensors) {

			int dis = s.getCurrentDistance();

			if (dis == 0) {

				this.getRobot().stop(true);
				Log.d(LOG_TAG, String.format("DANGER OF COLLISION @ %s - ROBOT STOPPED", s.getName()));
				this.running = false;
				break;
			}
		}
		
		if(this.running){
			requestSensors();
		}

	}

	@Override
	public void onPositionReceived(IPosition position) {
		// TODO Auto-generated method stub

	}


	private class Callback implements Handler.Callback {


		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			
			case SENSORS:
				getRobot().requestSensorData();
				break;
				
			case POSITION:
				getRobot().requestCurrentPosition();
				break;
			
			default:
				return false;
			}

			return true;
		}
	}
}
