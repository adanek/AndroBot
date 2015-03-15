package at.uibk.informatik.androbot.app;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

		EditText distance = (EditText) findViewById(R.id.distance);
		EditText degrees  = (EditText) findViewById(R.id.degrees);
		
		Integer dist = Integer.valueOf(distance.getText().toString());
		Integer degr = Integer.valueOf(degrees.getText().toString());
		
		System.out.println("Distance in cm " + dist);
		System.out.println("Degrees in ° " + degr);
		
		switch (v.getId()) {
		//forward
		case R.id.forward:
			//no distance entered
			if(dist == 0){
				robot.moveForward();
			//distance entered
			} else {
				robot.moveDistance(intToByte(dist));
			}
			break;
		//backward
		case R.id.backward:
			//no distance entered
			if(dist == 0){
				robot.moveBackward();
			//distance entered
			} else {
				robot.moveDistance(intToByte(dist));
			}
			break;
		case R.id.left:
			//turn left . default degrees
			if(degr == 0){
				robot.turn(Direction.LEFT);	
			} else {
				robot.turn(intToByte(degr));
			}
			
			break;
		case R.id.right:
			//turn right - default degrees
			if(degr == 0){
				robot.turn(Direction.RIGHT);	
			} else {
				robot.turn(intToByte(degr));
			}
			break;
		case R.id.btnStop:
			robot.stop();
			break;
		default:
			break;
		}
	}
	
	public void getOdomentry(View v){
		TextView output = (TextView) findViewById(R.id.tvPosition);
		
		output.setText(robot.getOdomentry());
	}
	
	//square test
	public void squareTest(View v){
		
		System.out.println("Square Test started");
		
		EditText distance = (EditText) findViewById(R.id.distance);
		
		System.out.println("Distance in cm " + Integer.valueOf(distance.getText().toString()));
		
		//get byte from integer
		byte dist_byte = intToByte(Integer.valueOf(distance.getText().toString()));
		
		//start square test
		robot.moveDistance(dist_byte);
		robot.turnLeft();
		robot.moveDistance(dist_byte);
		robot.turnLeft();
		robot.moveDistance(dist_byte);
		robot.turnLeft();
		robot.moveDistance(dist_byte);
		robot.turnLeft();
		
	}
	
	//convert integer into byte array
	private byte intToByte(int input){
		
		return (byte) input;
		
	}
}
