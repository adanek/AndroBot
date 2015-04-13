package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
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
	

}
