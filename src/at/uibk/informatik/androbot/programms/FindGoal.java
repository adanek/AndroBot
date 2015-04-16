package at.uibk.informatik.androbot.programms;

import java.util.List;
import java.util.Observable;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.Position;

public class FindGoal extends ProgrammBase {

	private static final String LOG_TAG = "FindGoal";
	private static final int REQUEST_SENSORS = 1;
	private static final int REQUEST_POSITION = 2;
	private Handler requester;

	private double X;
	private double Y;
	private double TH;
	private IPosition target;
	private IPosition current;
	private PropertyChangedEvent propChanged;
	private boolean sensorsRunning = false;
	private boolean obstacleDetected = false;

	public FindGoal(Context context, IRobotResponseCallback listener) {
		super(context, listener);

		this.requester = new Handler(new Callback());
		this.propChanged = new PropertyChangedEvent();
		this.current = Position.RootPosition();
		this.target = Position.RootPosition();
	}

	@Override
	protected void onExecute() {

		// Log
		Log.d(LOG_TAG, "Find Goal started");

		IRobot rob = getRobot();

		// Set odometry
		rob.setOdomentry(Position.RootPosition());
		this.setTarget(new Position(4, 3, 90));

		// Start sensors
		requester.obtainMessage(REQUEST_POSITION).sendToTarget();

	}

	public IPosition getTarget() {
		return this.target;
	}

	public void setTarget(IPosition target) {
		this.target = target;
	}

	public int getDistanceToTarget() {

		double x = Math.abs(target.getX() - current.getX());
		double y = Math.abs(target.getY() - current.getY());

		double dis = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

		Log.d(LOG_TAG, String.format("new distance to target: %d", (int) dis));
		return (int) dis;
	}

	public int getAngleToTarget() {

		double x = Math.abs(target.getX() - current.getX());
		double y = Math.abs(target.getY() - current.getY());

		if (x == 0)
			return 0;

		double ang = Math.toDegrees(Math.atan(y / x));

		Log.d(LOG_TAG, String.format("new Angle to target: %f", ang));
		return (int) Math.round(ang);
	}

	public Observable PropertyChanged() {
		return this.propChanged;

	}

	@Override
	public void onPositionReceived(IPosition position) {

		// Update screen
		if (!this.current.equals(position)) {
			this.current = position;
			this.propChanged.onChanged();
		}

		if (obstacleDetected)
			return;

		moveTowardsTarget();
	}

	private void moveTowardsTarget() {
		IRobot rob = this.getRobot();

		Log.d(LOG_TAG, String.format("target : %d %d %d", target.getX(), target.getY(), target.getOrientation()));
		Log.d(LOG_TAG, String.format("current: %d %d %d", current.getX(), current.getY(), current.getOrientation()));

		// Check if target is reached
		if ((current.getX() == target.getX()) && (current.getY() == target.getY())) {

			Log.d(LOG_TAG, "Target location reached");

			// check orientation
			if (current.getOrientation() != target.getOrientation()) {
				int angle = target.getOrientation() - current.getOrientation();
				Direction dir = angle > 0 ? Direction.LEFT : Direction.RIGHT;

				Log.d(LOG_TAG, String.format("Adjust oriendation by %d degree to the %s", angle, dir.toString()));
				rob.turn(dir, angle);
			}

			Log.d(LOG_TAG, "Target reached.");

			return;
		}

		Log.d(LOG_TAG, "Not yet at target location");

		// face target
		int angle = getAngleToTarget();
		Direction dir = angle > 0 ? Direction.LEFT : Direction.RIGHT;
		if (Math.abs(angle) > 5) {
			Log.d(LOG_TAG, String.format("Angle offset is %d. Turn to the %s", angle, dir.toString()));
			rob.stop(true);
			rob.turn(dir, Math.abs(angle));

		}

		if (!sensorsRunning) {
			sensorsRunning = true;
			requester.obtainMessage(REQUEST_SENSORS).sendToTarget();
		}

		rob.moveForward();
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
	}

	public double getTH() {
		return TH;
	}

	public void setTH(double tH) {
		TH = tH;
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {

		if (obstacleDetected) {
			avoidObstacle(sensors);
			return;
		}

		detectObstacles(sensors);

	}

	private void detectObstacles(List<IDistanceSensor> sensors) {

		IRobot rob = this.getRobot();

		for (IDistanceSensor s : sensors) {

			if (s.getCurrentDistance() <= 15) {
				rob.stop(true);

				Log.w(LOG_TAG, String.format("Obstacle detected @ %s", s.getName()));
				obstacleDetected = true;
				break;
			}
		}

		Message msg = requester.obtainMessage(REQUEST_SENSORS);
		requester.sendMessageDelayed(msg, 200);

	}
	
	private boolean turning = false;
	private boolean moving = false;

	private void avoidObstacle(List<IDistanceSensor> sensors) {
		Log.d(LOG_TAG, "I'm avoiding the obstacle");

		
		IDistanceSensor fl = sensors.get(0);
		IDistanceSensor fm = sensors.get(1);
		IDistanceSensor fr = sensors.get(2);
		
		// Find the direction to turn
		Direction dir = fl.getCurrentDistance() < fr.getCurrentDistance() ? Direction.RIGHT : Direction.LEFT;

	}

	private class PropertyChangedEvent extends Observable {

		public PropertyChangedEvent() {

		}

		public void onChanged() {
			this.setChanged();
			this.notifyObservers();
		}
	}

	private class Callback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case REQUEST_SENSORS:
				getRobot().requestSensorData();
				break;

			case REQUEST_POSITION:
				getRobot().requestCurrentPosition();
				break;

			default:
				return false;
			}

			return true;
		}

	}
}
