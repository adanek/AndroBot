package at.uibk.informatik.androbot.programms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.uibk.informatik.androbot.app.BallDetectionActivity;
import at.uibk.informatik.androbot.app.BeaconDetectionActivity;
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

		localizeSelf();
		
		//Log
		Log.d(LOG_TAG, "Beacon detection started");
	}

	private void localizeSelf() {
		//call self localization activity
		Intent sl = new Intent(context, SelfLocalizationActivity.class);
		((Activity) context).startActivityForResult(sl, 3);
	}
	
	public void searchBall(){
		
		
		BallDetectionActivity.color = BeaconDetectionActivity.ballColor;
		BallDetectionActivity.homoMat = BeaconDetectionActivity.getHomoMat();
		
		//call ball detection activity
		Intent sl = new Intent(context, BallDetectionActivity.class);
		((Activity) context).startActivityForResult(sl, 4);
		
		//Log
		Log.d(LOG_TAG, "Ball detection started");
		
	}
	
	public void startSelfLocalization(){
		this.onExecute();
	}
	
	public void testMethod(String result){
		//Log
		Log.d(LOG_TAG, "Returned with result: " + result);
		searchBall();
	}
	
	public void ballDetectionCallback(){
		
		//Log
		Log.d(LOG_TAG, "Ball is at position: " + ball.toString());
		
		int angle = getAngle(new Position(), ball);
		int distance = getDistanceToTarget(new Position(), ball);
		Log.d(LOG_TAG, String.format("ang: %d dis: %d", angle, distance));
		turn(angle);		
		moveDistance(distance-10);
		
		this.getRobot().lowerBar();
		
		this.getRobot().raiseBar();
		
		
	}
	
}
