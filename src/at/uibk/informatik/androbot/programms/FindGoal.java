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

	private IPosition current;
	private Position target;
	public static final int SENSORS = 20;
	public static final int POSITION = 30;
	private static String LOG_TAG = "FindGoal";

	private Handler requester;

	public FindGoal(Context context, IRobotResponseCallback listener) {
		super(context, listener);

		this.requester = new Handler(new Callback());
	}

	@Override
	protected void onExecute() {
		  Log.d(LOG_TAG, "blue cow");
		
		IRobot rob = getRobot();

		current = new Position(0, 0, 0);
		rob.setOdomentry(current);

		moveTowardsTarget(rob);	

		requester.obtainMessage(POSITION).sendToTarget();
		requester.obtainMessage(SENSORS).sendToTarget();

	}

	private void moveTowardsTarget(IRobot rob) {
		
		int angle = getAngleToTarget();
		int dis = getDistanceToTarget();
		Log.d(LOG_TAG, String.format("Target detected @ %d degrees, %d distance", angle, dis));

		rob.turn(Direction.LEFT, angle);
		rob.moveDistance(dis);
	}

	@Override
	public void onPositionReceived(IPosition position) {
		super.onPositionReceived(position);

		if (isExecuting()) {

			Message msg = requester.obtainMessage(POSITION);
			requester.sendMessageDelayed(msg, 100);
		}

		if (position == null) {
			return;
		}

		current = position;

		int x = getDistanceToTarget();

		Log.d(LOG_TAG, String.format("red cow %d", x));
		if (x <= 10) {
			getRobot().stop(true);
			atTarget();
		}
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		super.onSensorDataReceived(sensors);

		if (isExecuting()) {
			Message msg = requester.obtainMessage(SENSORS);
			requester.sendMessageDelayed(msg, 200);
		}

		if (sensors == null)
			return;

		int min = 99;
		for (IDistanceSensor s : sensors) {
			if (s.getCurrentDistance() < min) {
				min = s.getCurrentDistance();
			}
		}

		if (min <= 20) {
			getRobot().stop(true);
			stop();
		}

	}

	void atTarget() {
		Log.d(LOG_TAG, "Target location reached");

		Log.d(LOG_TAG, String.format("little red riding hood %d %d", current.getOrientation(), target.getOrientation()));
		// check orientation
		if (current.getOrientation() != target.getOrientation()) {

			Log.d(LOG_TAG, "I#m turning im turning ");
			int angle = target.getOrientation() - current.getOrientation();
			Direction dir = angle > 0 ? Direction.LEFT : Direction.RIGHT;
			angle = Math.abs(angle);

			Log.d(LOG_TAG, String.format("Adjust oriendation by %d degree to the %s", angle, dir.toString()));
			getRobot().turn(dir, angle);
		}

		Log.d(LOG_TAG, "Target reached.");
		// stop();
	}

	public int getDistanceToTarget() {

		double x = Math.abs(target.getX() - current.getX());
		double y = Math.abs(target.getY() - current.getY());

		double dis = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

		Log.d(LOG_TAG, String.format("new distance to target: %d", (int) dis));
		return (int) dis;
	}

	public int getAngleToTarget() {

		double x = (target.getX() - current.getX());
		double y = (target.getY() - current.getY());
		
		
		int res = 0;

		if (x == 0) {
			
			res = y < 0 ? 270 : 90;
		} else if (y == 0) {

			res = x < 0 ? 180 : 0;
		} else {
			double ang = Math.toDegrees(Math.atan(y / x));
			res = (int) Math.round(ang);
		}

		Log.d(LOG_TAG, String.format("sending new Angle to target: %d", res));
		return res;
	}

	private class Callback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case SENSORS:
				getRobot().requestSensorData(false);
				break;

			case POSITION:
				getRobot().requestCurrentPosition(false);
				break;

			default:
				return false;
			}

			return true;
		}

	}

	public IPosition getCurrent() {
		return current;
	}

	public void setCurrent(IPosition current) {
		this.current = current;
	}

	public Position getTarget() {
		return target;
	}

	public void setTarget(Position target) {
		this.target = target;
	}
	
	
}
