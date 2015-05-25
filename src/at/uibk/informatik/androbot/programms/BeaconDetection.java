package at.uibk.informatik.androbot.programms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.uibk.informatik.androbot.app.BallDetectionActivity;
import at.uibk.informatik.androbot.app.SelfLocalizationActivity;
import at.uibk.informatik.androbot.control.Position;

public class BeaconDetection extends ProgrammBase {

	private static final String LOG_TAG = "Beacon detection";	
	private Context context;
	public static Position ball;

	public BeaconDetection(Context context) {
		super(context);
		this.context = context;
	}	

	@Override
	protected void onExecute() {

		//call self localization activity
		Intent sl = new Intent(context, SelfLocalizationActivity.class);
		((Activity) context).startActivityForResult(sl, 3);
		
		//Log
		Log.d(LOG_TAG, "Beacon detection started");
	}
	
	public void searchBall(){
		
		//call ball detection activity
		Intent sl = new Intent(context, BallDetectionActivity.class);
		((Activity) context).startActivityForResult(sl, 3);
		
		//Log
		Log.d(LOG_TAG, "Ball detection started");
		
	}
	
	public void startSelfLocalization(){
		this.onExecute();
	}
	
	public void testMethod(String result){
		//Log
		Log.d(LOG_TAG, "Returned with result: " + result);
	}
	
	public void ballDetectionCallback(){
		
		//Log
		Log.d(LOG_TAG, "Ball is at position: " + ball.toString());
		
	}
	
}
