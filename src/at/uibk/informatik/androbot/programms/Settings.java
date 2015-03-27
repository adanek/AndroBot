package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class Settings extends ProgrammBase {

	public Settings(Context context, IRobotResponseCallback listener) {
		super(context, listener);
	}

	private static final String LOG_TAG = "Settings";

	private int distance;
	private int degrees;
	
	public int test;

	@Override
	protected void onExecute() {
				
		IRobot robot = super.getRobot();
		
		switch(test){
		case 0:
			//Log
			Log.d(LOG_TAG, "performing " + distance + " distance test");
			robot.moveDistance((byte) distance);
			break;
		case 1:
			//Log
			Log.d(LOG_TAG, "performing " + degrees + " degrees test");
			robot.turn(Direction.LEFT, degrees);
			break;	
		}
	}
	
	//get distance
	public int getDistance() {
		return distance;
	}

	//set distance
	public void setDistance(int distance) {
		this.distance = distance;
	}

	//get degrees
	public int getDegrees() {
		return degrees;
	}

	//set degrees
	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}	
}
