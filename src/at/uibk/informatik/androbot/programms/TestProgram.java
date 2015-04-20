package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.Position;

public class TestProgram extends ProgrammBase {

	private static final String LOG_TAG = "Testing";

	public TestProgram(Context context, IRobotResponseCallback listener) {
		super(context, listener);
		
	}

	@Override
	protected void onExecute()  {
		
		Log.d(LOG_TAG, "Executing...");
		
		IRobot rob = getRobot();
		
		Log.d(LOG_TAG, String.format("linear runtime: %f",rob.getLinearRuntimePerCentimeter()));
		
		rob.setLinearRuntimePerCentimeter(54);
		
		rob.moveDistance(250);		// 54 
		//rob.stop(true);		
	}
	


	
}