package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.control.Robot;

public class BlobDetection extends ProgrammBase {

	public BlobDetection(Context context) {
		super(context);
		this.obstacleDeteced = false;
	}

	private static final String LOG_TAG = "TestProgram";
	private boolean obstacleDeteced;

	@Override
	protected void onExecute() {
		
		Log.d(LOG_TAG, "onExecute called");

		Robot rob = getRobot();
		rob.setLinearRuntimePerCentimeter(54);
		
		int dis= 50;
		int ang = 90;
		obstacleDeteced = false;
		
		for(int i = 0; i < 8; i++){
			
			if(!obstacleDeteced){
				if(i % 2 == 0){
					moveDistance(dis);
				} else
				{
					turn(ang);
				}
			}
		}
	}	

	@Override
	protected void onObstacleDetected() {
		super.onObstacleDetected();
		
		getRobot().stop(true);
		this.obstacleDeteced = true;
	}
}