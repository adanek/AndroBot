package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.control.Robot;

public class TestProgram extends ProgrammBase {

	private static final String LOG_TAG = "TestProgram";
	private boolean obstacleDeteced;
	private Position current = new Position();
	private Position target = new Position(100,100,0);

	public TestProgram(Context context) {
		super(context);
		this.obstacleDeteced = false;
	}

	@Override
	protected void onExecute() {

		Log.d(LOG_TAG, "onExecute called");

		obstacleDeteced = false;

		start = System.currentTimeMillis();
		moveTowardsTarget(target);
		
		Log.d(LOG_TAG, String.format("RED COW @ x: %d y: %d, th: %d", current.getX(), current.getY(), current.getTh()));
	}


	long start;
	public void moveTowardsTarget(Position target) {

		int ang = getAngle(current, target);
		turn(ang);
		current.setTh(current.getTh() + ang);
		
		int dis = getDistanceToTarget(current, target);
		moveDistance(dis);
		setPosition(dis);
		
		if(!obstacleDeteced){
			ang = target.getTh() - current.getTh();
			turn(ang);
			current.setTh(current.getTh() + ang);			
		}
		
		
	}	
	
	public void setPosition(int distance){
		
		double th = current.getTh();
		double w = 90;	
	
		double a;
		double b;
		double c = distance;
		
		if(th > 0 && th <= 90){
			a = Math.cos(Math.toRadians(th)) * c;
			b = Math.sin(Math.toRadians(th)) * c;
		}else if(th > 90 && th <= 180){
			w = w - (th - 90);
			a = Math.cos(Math.toRadians(w)) * c * -1;
			b = Math.sin(Math.toRadians(w)) * c;
		}else if(th < 0 && th >= -90){
			w = th * -1;
			a = Math.cos(Math.toRadians(w)) * c;
			b = Math.sin(Math.toRadians(w)) * c * -1;
		}else {
			w = w - ((th * -1) -90);
			a = Math.cos(Math.toRadians(w)) * c * -1;
			b = Math.sin(Math.toRadians(w)) * c * -1;
		}
		
		current.setX((int)Math.round(a));
		current.setY((int)Math.round(b));
		
		Log.d(LOG_TAG, String.format("RED COW x: %d y: %d, th: %d", current.getX(), current.getY(), current.getTh()));
	}

	@Override
	protected void onObstacleDetected() {
		super.onObstacleDetected();	
	
		long end = System.currentTimeMillis();			
		getRobot().stop();
		
		int distance = (int) ((end - start) / getRobot().getLinearRuntimePerCentimeter());	
		setPosition(distance);
		Log.d(LOG_TAG, String.format("Moved distance: %d",  distance));
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