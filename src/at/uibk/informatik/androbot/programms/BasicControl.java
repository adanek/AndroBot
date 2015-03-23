package at.uibk.informatik.androbot.programms;

import android.util.Log;
import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;

public class BasicControl extends ProgrammBase {

	public BasicControl() {
		//super(SettingsActivity.MacAddress);
		
		//super.getRobot().setAngularCorrection(SettingsActivity.AngularCorrecion);
		//super.getRobot().setLinearCorrection(SettingsActivity.LinearCorrection);
	}

	private static final String LOG_TAG = "Basic Control";

	@Override
	public void execute() {
		
		
	}
	
	//move
	public void move(Direction dir){
		
		//log
		Log.d(LOG_TAG, "Move " + dir);
		
		IRobot robot = super.getRobot();
		
		
		switch(dir){
		//turn left
		case LEFT:
			robot.turnLeft();
			break;
		//turn right
		case RIGHT:
			robot.turnRight();
			break;
		//move forward
		case FORWARD:
			robot.moveForward();
			break;
		//move backward
		case BACKWARD:
			robot.moveBackward();
			break;
		}
	}
	
	//stop
	public void stop(){
		
		//log
		Log.d(LOG_TAG, "Robot stopped");
		
		IRobot robot = super.getRobot();
		
		//stop robot
		robot.stop();
	}

}
