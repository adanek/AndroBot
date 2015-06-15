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
	private boolean obstacleDeteced;
	public static Position ball;
	private Position ballWorld;
	private Position currentPos;
	private States currentState = States.INIT;
	private States prevState = States.INIT;
	public Position home = new Position(100, 100, 0);

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
			
			//BEGIN TO BE IMPLEMENTED
			//this.currentState = States.LOCALIZE;
			//END TO BE IMPLEMENTED
			
			//BEGIN TO BE DELETED
			this.currentState = States.FIND_BALL;
			currentPos = new Position();
			//END TO BE DELETED
			
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
		case OBSTACLE:
			Log.d(LOG_TAG, "Obstacle avoidance started");
			avoidObstacle();
			break;
		default:
			break;
		}

	}

	private void avoidObstacle() {

		Log.d(LOG_TAG, "There is something in front of me");

		obstacleDeteced = false;

		int direction = Math.random() > 0.6 ? -1 : 1;
		int angle = -90 * direction;

		turn(angle);
		setOrientation(angle);		
		Log.d(LOG_TAG,
				String.format("Pos: %d %d %d %b", currentPos.getX(),
						currentPos.getY(), currentPos.getTh(), obstacleDeteced));

		moveDistance(25);
		setPosition(25);

		if (obstacleDeteced == false) {
			currentState = prevState;
			prevState = States.INIT;
		}

		onExecute();

	}

	private void localizeSelf() {

		// call self localization activity
		Intent sl = new Intent(context, SelfLocalizationActivity.class);
		((Activity) context).startActivityForResult(sl, 3);
	}

	public void selflocationCallback() {

		Log.d("Test", "Callback");

		if (BeaconDetectionActivity.current == null) {
			turn(45);
			setOrientation(45);
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
			turn(45);
			setOrientation(45);

		} else {

			this.currentState = States.CATCH_BALL;
			Log.d(LOG_TAG, "Ball is at position: " + ball.toString());
		}
		onExecute();
	}

	private void catchBall() {

		// ball position is not yet set
		if (ballWorld == null) {
			int angle = getAngle(new Position(), ball);
			int distance = getDistanceToTarget(new Position(), ball);
			Log.d(LOG_TAG, String.format("ang: %d dis: %d", angle, distance));

			// turn(angle);
			// this.currentPos.setTh(currentPos.getTh() + angle);

			// set ball position
			this.ballWorld = calcPosition(distance, angle);
		}

		// move towards ball
		moveTowardsTarget(ballWorld);

		// return if obstacle was detected
		if (obstacleDeteced == true) {
			if (currentState != States.OBSTACLE) {
				prevState = currentState;
			}
			currentState = States.OBSTACLE;
			onExecute();
			return;
		}

		// reset ball position
		ballWorld = null;

		this.getRobot().lowerBar();

		this.currentState = States.DELIVER;
		onExecute();
	}

	private void deliverBall() {

		// int angle = getAngle(currentPos, home);
		// int distance = getDistanceToTarget(currentPos, home);
		// Log.d(LOG_TAG, String.format("Movement to home: ang: %d dis: %d",
		// angle, distance));

		// turn(angle);
		// this.currentPos.setTh(currentPos.getTh() + angle);

		// moveDistance(distance - 10);

		// bring the ball home
		moveTowardsTarget(home);

		// obstacle was detected
		if (obstacleDeteced == true) {
			if (currentState != States.OBSTACLE) {
				prevState = currentState;
			}
			currentState = States.OBSTACLE;
			onExecute();
			return;
		}

		// setPosition(distance);

		this.getRobot().raiseBar();

		turn(179);
		setOrientation(179);
		this.currentState = States.INIT;
		onExecute();
	}

	@Override
	protected void onObstacleDetected() {
		super.onObstacleDetected();

		long end = System.currentTimeMillis();
		getRobot().stop();

		int distance = (int) ((end - getLastStart()) / getRobot()
				.getLinearRuntimePerCentimeter());
		setPosition(distance);
		Log.d(LOG_TAG, String.format("Moved distance: %d", distance));
		this.obstacleDeteced = true;
	}

	private void setOrientation(int angle) {
		int ori = this.currentPos.getTh() + angle;
		
		if(ori < 0 ){
			ori = 360 + ori;
		} else if (ori >= 360){
			ori = ori - 360;
		}
		
		this.currentPos.setTh(ori);
	}

	private Position calcPosition(int distance, int angle) {

		double th = currentPos.getTh();
		th = (th + angle) % 360;
		if (th < 0) {
			th = 360 - th;
		}
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

		return new Position((int) Math.round(a), (int) Math.round(b),
				(int) Math.round(th));

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

		Log.d(LOG_TAG, String.format("RED COW x: %d y: %d, th: %d",
				currentPos.getX(), currentPos.getY(), currentPos.getTh()));
	}

	public void moveTowardsTarget(Position target) {

		Log.d(LOG_TAG, String.format("Searching target..."));

		// Richte dich auf das Ziel
		int ang = getAngle(currentPos, target);
		Log.d(LOG_TAG, String.format("Angle to tdistancearget %d", ang));
		if (ang != 0) {
			turn(ang);
			setOrientation(ang);
		}

		// Fahre auf das Ziel
		int dis = getDistanceToTarget(currentPos, target);
		Log.d(LOG_TAG, String.format("Distance to target %d", dis));
		if (dis != 0) {
			moveDistance(dis);			
		}

		// Dreh dich in den gewünschten Winkel
		if (!obstacleDeteced) {
			setPosition(dis);
			// ang = target.getTh() - currentPos.getTh();
			// turn(ang);
			// currentPos.setTh(currentPos.getTh() + ang);
		}

	}
}
