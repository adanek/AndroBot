package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.control.Position;

public class BeaconDetection extends ProgrammBase {

	private static final String LOG_TAG = "Beacon detection";	

	public BeaconDetection(Context context) {
		super(context);
	}	

	@Override
	protected void onExecute() {

		//Log
		Log.d(LOG_TAG, "Beacon detection started");
	}
	
}
