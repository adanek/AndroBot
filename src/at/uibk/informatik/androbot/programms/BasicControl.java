package at.uibk.informatik.androbot.programms;

import android.util.Log;
import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.contracts.BarMove;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IRobot;

public class BasicControl extends ProgrammBase {
	
	public BasicControl() {

	}

	private static final String LOG_TAG = "Basic Control";

	@Override
	public void execute() {

	}

	// move
	public void move(Direction dir) {

		// log
		Log.d(LOG_TAG, "Move " + dir);

		IRobot robot = super.getRobot();
		
		switch (dir) {
		// turn left
		case LEFT:
			robot.turnLeft();
			break;
		// turn right
		case RIGHT:
			robot.turnRight();
			break;
		// move forward
		case FORWARD:
			robot.moveForward();
			break;
		// move backward
		case BACKWARD:
			robot.moveBackward();
			break;
		}
	}

	// stop
	public void stop() {

		// log
		Log.d(LOG_TAG, "Robot stopped");

		IRobot robot = super.getRobot();
		
		// stop robot
		robot.stop();
	}

	public void handleBar(BarMove bar) {

		// log
		Log.d(LOG_TAG, "Bar action: " + bar);
		
		IRobot robot = super.getRobot();
		
		switch (bar) {
		case UP:
			robot.setBar(90);
			break;
		case DOWN:
			robot.setBar(0);
			break;
		case PLUS:
			robot.barRise();
			break;
		case MINUS:
			robot.barLower();
			break;
		}

	}
	
	//return distance sensors
	public IDistanceSensor[] getSensorValues(){
		
		IRobot robot = super.getRobot();
		
		//get sensor data from robot
		return robot.getSensors();
		
	}
	
}
