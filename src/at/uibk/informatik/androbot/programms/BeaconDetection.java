package at.uibk.informatik.androbot.programms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.uibk.informatik.androbot.app.BallDetectionActivity;
import at.uibk.informatik.androbot.app.BeaconDetectionActivity;
import at.uibk.informatik.androbot.app.SelfLocalizationActivity;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.enums.States;

public class BeaconDetection extends ProgrammBase {

	private static final String LOG_TAG = "Beacon detection";
	private Context context;
	public static Position ball;
	private Position currentPos;
	private States currentState = States.INIT;
	public Position home =  new Position(100, 100, 0);

	public States getCurrentState() {
		return currentState;
	}

	public void setCurrentState(States currentState) {
		this.currentState = currentState;
	}

	public BeaconDetection(Context context) {
		super(context);
		this.context = context;
	}

	public void startSelfLocalization() {
		this.onExecute();
	}

	@Override
	protected void onExecute() {

		switch (currentState) {
		case INIT:
			Log.d(LOG_TAG, "init");
			this.currentState = States.LOCALIZE;
			onExecute();
			break;
		case LOCALIZE:
			Log.d(LOG_TAG, "Localize start");
			this.localizeSelf();
			Log.d(LOG_TAG, "Localize end");
			break;
		case FIND_BALL:
			Log.d(LOG_TAG, "Searching for ball...");
			this.searchBall();
			break;
		case CATCH_BALL:
			Log.d(LOG_TAG, "Go for the ball...");
			catchBall();
			break;
		case DELIVER:
			Log.d(LOG_TAG, "Bring ball the ball to mummy...");			
			deliverBall();
			break;

		default:
			break;
		}

	}



	private void localizeSelf() {

		// call self localization activity
		Intent sl = new Intent(context, SelfLocalizationActivity.class);
		((Activity) context).startActivityForResult(sl, 3);
	}

	public void selflocationCallback() {

		Log.d("Test", "Callback");

		if (BeaconDetectionActivity.current == null) {
			turn(30);
		} else {
			currentState = States.FIND_BALL;
			currentPos = BeaconDetectionActivity.current;
		}

		onExecute();
	}

	public void searchBall() {

		BallDetectionActivity.color = BeaconDetectionActivity.ballColor;
		BallDetectionActivity.homoMat = BeaconDetectionActivity.getHomoMat();

		// call ball detection activity
		Intent sl = new Intent(context, BallDetectionActivity.class);
		((Activity) context).startActivityForResult(sl, 4);

		// Log
		Log.d(LOG_TAG, "Ball detection started");
	}

	public void ballDetectionCallback() {

		// Log

		if (ball == null) {
			turn(30);
			setOrientation(30);

		} else {

			this.currentState = States.CATCH_BALL;
			Log.d(LOG_TAG, "Ball is at position: " + ball.toString());
		}
		onExecute();
	}

	private void catchBall() {

		int angle = getAngle(new Position(), ball);
		int distance = getDistanceToTarget(new Position(), ball);
		Log.d(LOG_TAG, String.format("ang: %d dis: %d", angle, distance));

		turn(angle);
		this.currentPos.setTh(currentPos.getTh() + angle);

		moveDistance(distance - 10);
		setPosition(distance);

		this.getRobot().lowerBar();
		
		this.currentState = States.DELIVER;
		onExecute();
	}
	
	
	private void deliverBall() {
		
		int angle = getAngle(currentPos, home);
		int distance = getDistanceToTarget(currentPos, home);
		Log.d(LOG_TAG, String.format("Movement to home: ang: %d dis: %d", angle, distance));
		
		turn(angle);
		this.currentPos.setTh(currentPos.getTh() + angle);

		moveDistance(distance - 10);
		setPosition(distance);
		
		this.getRobot().raiseBar();
		
		turn(180);
		this.currentState = States.LOCALIZE;
		onExecute();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private void setOrientation(int angle) {
		this.currentPos.setTh((currentPos.getTh() + 30) % 360);
	}

	private void setPosition(int distance) {

		double th = currentPos.getTh();
		double w = 90;

		double a = currentPos.getX();
		double b = currentPos.getY();
		double c = distance;

		if (th > 0 && th <= 90) {
			a += Math.cos(Math.toRadians(th)) * c;
			b += Math.sin(Math.toRadians(th)) * c;
		} else if (th > 90 && th <= 180) {
			w = w - (th - 90);
			a += Math.cos(Math.toRadians(w)) * c * -1;
			b += Math.sin(Math.toRadians(w)) * c;
		} else if (th < 0 && th >= -90) {
			w = th * -1;
			a += Math.cos(Math.toRadians(w)) * c;
			b += Math.sin(Math.toRadians(w)) * c * -1;
		} else {
			w = w - ((th * -1) - 90);
			a += Math.cos(Math.toRadians(w)) * c * -1;
			b += Math.sin(Math.toRadians(w)) * c * -1;
		}

		currentPos.setX((int) Math.round(a));
		currentPos.setY((int) Math.round(b));

		Log.d(LOG_TAG,
				String.format("RED COW x: %d y: %d, th: %d", currentPos.getX(), currentPos.getY(), currentPos.getTh()));
	}
}
