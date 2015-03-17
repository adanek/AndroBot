package at.uibk.informatik.androbot.app;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;
import at.uibk.informatik.androbot.programms.SquareTest;

public class MainActivity extends Activity {

	private IRobot robot;
	private SquareTest prg;
	private String test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		prg = new SquareTest();
		
		//Andis Maci 00:26:83:30:F7:E8
		this.robot = new Robot(new BluetoothConnection("20:13:08:16:10:42"));   // MAC
																				// from
																				// Andi's
																				// PC
																				// pleas
																				// do
																				// not
																				// delete,
																				// just
																				// comment
																				// out.

	}

	@Override
	protected void onResume() {
		super.onResume();
				
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}

	// move
	public void move(View v) {

		EditText distance = (EditText) findViewById(R.id.distance);
		EditText degrees = (EditText) findViewById(R.id.degrees);

		Integer dist = Integer.valueOf(distance.getText().toString());
		Integer degr = Integer.valueOf(degrees.getText().toString());

		Log.d("main_activity", "Distance in cm " + dist);
		Log.d("main_activity", "Degrees in °" + dist);

		switch (v.getId()) {
		// forward
		case R.id.forward:
			// no distance entered
			if (dist == 0) {
				robot.moveForward();
				// distance entered
			} else {
				robot.moveDistance(intToByte(dist));
			}
			break;
		// backward
		case R.id.backward:
			robot.moveBackward();
			break;
		case R.id.left:
			// turn left . default degrees
			if (degr == 0) {
				robot.turn(Direction.LEFT);
			} else {
				robot.turn(degr);
			}

			break;
		case R.id.right:
			// turn right - default degrees
			if (degr == 0) {
				robot.turn(Direction.RIGHT);
			} else {
				robot.turn(degr);
			}
			break;
		case R.id.btnStop:
			robot.stop();
			break;
		default:
			break;
		}
	}

	public void getOdomentry(View v) {
		TextView output = (TextView) findViewById(R.id.txtName);

		output.setText(robot.getOdomentry());
	}

	// square test
	public void squareTest(View v){
		

	}

	// convert integer into byte array
	private byte intToByte(int input) {

		return (byte) input;

	}
}
