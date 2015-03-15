package at.uibk.informatik.androbot.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import at.uibk.informatik.androbot.app.R.id;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;

public class MainActivity extends Activity {

	private IRobot robot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.robot = new Robot(new BluetoothConnection("00:26:83:30:F7:E8")); // MAC from Andi's PC pleas do not delete, just comment out.
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.robot.connect();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.robot.disconnect();
	}

	// move
	public void move(View v) {

		switch (v.getId()) {
		case R.id.forward:
			robot.moveForward();
			break;
		case R.id.backward:
			robot.moveBackward();
			break;
		case R.id.left:
			robot.turn(Direction.LEFT);
			break;
		case R.id.right:
			robot.turn(Direction.RIGHT);
			break;
		case R.id.btnStop:
			robot.stop();
			break;
		default:
			break;
		}

	}
}
