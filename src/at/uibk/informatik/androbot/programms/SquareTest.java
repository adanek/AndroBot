package at.uibk.informatik.androbot.programms;

import android.util.Log;
import android.widget.EditText;
import at.uibk.informatik.androbot.app.R;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.control.Robot;

public class SquareTest extends Programm {

	private static final String LOG_TAG = "SquareTest";

	private int distance;
	
	@Override
	public void ExecutionPlan() {
		
		//Log
		Log.d(LOG_TAG, "Square Test started");
		Log.d(LOG_TAG, "Distance in cm " + Integer.valueOf(distance));

		// Think about encapsulating the obstacale logic.
		
		// Start
		// Get start position (Odomentry)
		
		// Start Sensors
		
		// start square test
		robot.moveDistance((byte) distance);
		robot.turn(Direction.LEFT);
		robot.moveDistance((byte) distance);
		robot.turn(Direction.LEFT);
		robot.moveDistance((byte) distance);
		robot.turn(Direction.LEFT);
		robot.moveDistance((byte) distance);
		robot.turn(Direction.LEFT);
		
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
