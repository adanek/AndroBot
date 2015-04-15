package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class BlobDetection extends ProgrammBase {

	private static final String LOG_TAG = "Blob detection";
	
	public BlobDetection(Context context, IRobotResponseCallback listener) {
		super(context, listener);
	}	

	@Override
	protected void onExecute() {

		//Log
		Log.d(LOG_TAG, "Blob detection started");




	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPositionReceived(IPosition position) {
		// TODO Auto-generated method stub
		
	}
	

}
