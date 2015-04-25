package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.control.Robot;

public class findGoal extends ProgrammBase {

	private static final String LOG_TAG = "Find Goal Program";
	private boolean obstacleDeteced;
	private Position current = new Position();
	private Position target = new Position(100, 100, 0);

	public findGoal(Context context) {
		super(context);
		this.obstacleDeteced = false;
	}

	@Override
	protected void onExecute() {

		Log.d(LOG_TAG, "onExecute called");
		obstacleDeteced = false;

		while (!target.equals(current)) {

			Log.d(LOG_TAG, String.format("Pos: %d %d %d %b", current.getX(), current.getY(), current.getTh(), obstacleDeteced));
			
			if (obstacleDeteced) {
				Log.d(LOG_TAG, "There is something in front of me");
				
				obstacleDeteced = false;	
				
				int direction = Math.random() > 0.6 ? -1 : 1;
				int angle = -90 * direction;
				
				turn(angle);
				current.setTh(current.getTh() + (angle));
				Log.d(LOG_TAG, String.format("Pos: %d %d %d %b", current.getX(), current.getY(), current.getTh(), obstacleDeteced));
				
				moveDistance(50);
				
				if(obstacleDeteced){
					continue;
				}
				
				setPosition(50);
				Log.d(LOG_TAG, String.format("Pos: %d %d %d %b", current.getX(), current.getY(), current.getTh(), obstacleDeteced));			
				
				turn(angle * -1);
				current.setTh(current.getTh() + (angle * -1));
				Log.d(LOG_TAG, String.format("Pos: %d %d %d %b", current.getX(), current.getY(), current.getTh(), obstacleDeteced));			
					
			} else {
				moveTowardsTarget(target);
			}
			
			Log.d(LOG_TAG, String.format("Pos: %d %d %d %b", current.getX(), current.getY(), current.getTh(), obstacleDeteced));
		}
		Log.d(LOG_TAG, String.format("Pos: %d %d %d %b", current.getX(), current.getY(), current.getTh(), obstacleDeteced));

		Log.d(LOG_TAG, String.format("Done"));
	}

	public void moveTowardsTarget(Position target) {

		Log.d(LOG_TAG, String.format("Searching target..."));

		// Richte dich auf das Ziel
		int ang = getAngle(current, target);
		Log.d(LOG_TAG, String.format("Angle to tdistancearget %d", ang));
		if (ang != 0) {
			turn(ang);
			current.setTh(current.getTh() + ang);
		}

		// Fahre auf das Ziel
		int dis = getDistanceToTarget(current, target);
		Log.d(LOG_TAG, String.format("Distance to target %d", dis));
		if (dis != 0) {
			moveDistance(dis);
		}

		// Dreh dich in den gewünschten Winkel
		if (!obstacleDeteced) {
			setPosition(dis);
			ang = target.getTh() - current.getTh();
			turn(ang);
			current.setTh(current.getTh() + ang);
		}

	}

	public void setPosition(int distance) {

		double th = current.getTh();
		double w = 90;

		double a = getCurrent().getX();
		double b = getCurrent().getY();
		double c = distance;

		if (th > 0 && th <= 90) {
			a += Math.cos(Math.toRadians(th)) * c;
			b += Math.sin(Math.toRadians(th)) * c;
		} else if (th > 90 && th <= 180) {
			w = w - (th - 90);
			a += Math.cos(Math.toRadians(w)) * c * -1;
			b += Math.sin(Math.toRadians(w)) * c;
		} else if (th < 0 && th >= -90) {
			w = th * -1;
			a += Math.cos(Math.toRadians(w)) * c;
			b += Math.sin(Math.toRadians(w)) * c * -1;
		} else {
			w = w - ((th * -1) - 90);
			a += Math.cos(Math.toRadians(w)) * c * -1;
			b += Math.sin(Math.toRadians(w)) * c * -1;
		}

		current.setX((int) Math.round(a));
		current.setY((int) Math.round(b));

		Log.d(LOG_TAG, String.format("RED COW x: %d y: %d, th: %d", current.getX(), current.getY(), current.getTh()));
	}

	@Override
	protected void onObstacleDetected() {
		super.onObstacleDetected();

		long end = System.currentTimeMillis();
		getRobot().stop();

		int distance = (int) ((end - getLastStart()) / getRobot().getLinearRuntimePerCentimeter());
		setPosition(distance);
		Log.d(LOG_TAG, String.format("Moved distance: %d", distance));
		this.obstacleDeteced = true;
	}

	public Position getTarget() {
		return target;
	}

	public void setTarget(Position target) {
		this.target = target;
	}

	public Position getCurrent() {
		return current;
	}

	public void setCurrent(Position current) {
		this.current = current;
	}

}