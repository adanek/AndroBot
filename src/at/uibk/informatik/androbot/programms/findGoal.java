package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.control.Robot;

public class findGoal extends ProgrammBase {

	private static final String LOG_TAG = "TestProgram";
	private boolean obstacleDeteced;

	public findGoal(Context context) {
		super(context);
		this.obstacleDeteced = false;
	}

	@Override
	protected void onExecute() {
		
		Log.d(LOG_TAG, "onExecute called");

		Robot rob = getRobot();
		
		Position current = new Position();
		Position target = new Position(-100,-100,0);
		
		int ang = getAngle(current, target);
		turn(ang);
		current.setTh(ang);
		moveDistance(getDistanceToTarget(current, target));
		
		ang = target.getTh() - current.getTh();
		turn(ang);
		current.setTh(ang);

		
	}
	
	boolean running;
	@Override
	protected void onObstacleDetected() {
		super.onObstacleDetected();
		
		getRobot().stop(true);
		this.running = false;
	}
}