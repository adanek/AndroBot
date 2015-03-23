package at.uibk.informatik.androbot.app;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.BarMove;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;
import at.uibk.informatik.androbot.programms.BasicControl;
import at.uibk.informatik.androbot.programms.SquareTest;

public class BasicControlActivity extends Activity {

	private BasicControl basic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic);

		// create basic control instance
		basic = new BasicControl();
	}

	@Override
	protected void onResume() {
		super.onResume();
		basic.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		basic.disconnect();
	}

	// Move
	public void onMove(View v) {

		switch (v.getId()) {
		// forward
		case R.id.forward:
			basic.move(Direction.FORWARD);
			break;
		// backward
		case R.id.backward:
			basic.move(Direction.BACKWARD);
			break;
		// turn left
		case R.id.left:
			basic.move(Direction.LEFT);
			break;
		// turn right
		case R.id.right:
			basic.move(Direction.RIGHT);
			break;
		default:
			break;
		}

	}

	// stop
	public void onStop(View v) {
		basic.stop();
	}

	// handle bar
	public void onBar(View v) {

		switch (v.getId()) {
		case R.id.down:
			basic.handleBar(BarMove.DOWN);
			break;
		case R.id.up:
			basic.handleBar(BarMove.UP);
			break;
		case R.id.plus:
			basic.handleBar(BarMove.PLUS);
			break;
		case R.id.minus:
			basic.handleBar(BarMove.MINUS);
			break;
		}

	}

	// on Sensors
	public void onSensors(View v) {

		List<IDistanceSensor> sensorData = basic.getSensorValues();

		for (int i = 0; i < sensorData.size(); i++) {

			TextView text = null;

			// get sensor view element
			switch (i) {
			case 0:
				text = (TextView) findViewById(R.id.txtRL);
				break;
			case 1:
				text = (TextView) findViewById(R.id.txtFL);
				break;
			case 2:
				text = (TextView) findViewById(R.id.txtFM);
				break;
			case 3:
				text = (TextView) findViewById(R.id.txtFR);
				break;
			case 4:
				text = (TextView) findViewById(R.id.txtRR);
				break;
			}

			// set text on screen
			if (text != null) {
				// set text on screen
				text.setText(Integer.toString(sensorData.get(i)
						.getCurrentDistance()));
			}

		}

	}

	// on odometry
	public void onPosData(View v) {

		String posData = basic.getPositionData();

		TextView pos = (TextView) findViewById(R.id.txtOdometry);

		//refresh position data
		if (posData != null & pos != null) {
			pos.setText(posData);
		}

	}

}
