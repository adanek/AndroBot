package at.uibk.informatik.androbot.programms;

import android.util.Log;
import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;

public class SquareTest extends ProgrammBase {

	private static final String LOG_TAG = "SquareTest";

	private int distance;
	
	
	public SquareTest() {
		//super(SettingsActivity.MacAddress);
		
		//super.getRobot().setAngularCorrection(SettingsActivity.AngularCorrecion);
		//super.getRobot().setLinearCorrection(SettingsActivity.LinearCorrection);
	}

	@Override
	public void execute() {
		
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
