package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class SquareTest extends ProgrammBase {

	private static final String LOG_TAG = "SquareTest";
	private int distance;
	
	public SquareTest(Context context, IRobotResponseCallback listener) {
		super(context, listener);
	}	

	@Override
	protected void onExecute() {

		//Log
		Log.d(LOG_TAG, "Square Test started");
		Log.d(LOG_TAG, "Distance in cm " + Integer.valueOf(distance));

		// Think about encapsulating the obstacale logic.
		
		// Start
		// Get start position (Odomentry)
		
		// Start Sensors
		
		IRobot robot = super.getRobot();
		
		// start square test
		robot.moveDistance((byte) distance);
		robot.turnLeft();
		robot.moveDistance((byte) distance);
		robot.turnLeft();
		robot.moveDistance((byte) distance);
		robot.turnLeft();
		robot.moveDistance((byte) distance);
		robot.turnLeft();
		
		// STtop sensors
		
		// Get final position (Odomentry)
		
		// Show difference
		
		// Display startPosition, endPosition and difference on screen

	}
	
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
