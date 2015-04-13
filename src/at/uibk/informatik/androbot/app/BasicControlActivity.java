package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.BarMove;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.BasicControl;

public class BasicControlActivity extends ProgramActivityBase implements
		IRobotResponseCallback {

	private static final String LOG_TAG = "BasicControl";

	private BasicControl basic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic);

		this.basic = new BasicControl(getApplicationContext(), this);
		this.setProgramm(basic);
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

		basic.requestSensorValues();
	}

	// on odometry
	public void onPosData(View v) {

		basic.requestPositionData();

	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {

		// log
		Log.d(LOG_TAG, "sensor data received");

		// no sensor data available
		if (sensors == null) {
			return;
		}

		for (int i = 0; i < sensors.size(); i++) {

			TextView text = null;

			IDistanceSensor sensor = sensors.get(i);

			// no sensor
			if (sensor == null) {
				continue;
			}

			String name = sensor.getName();

			// get sensor view element
			if (name == "Front-Middle") {
				text = (TextView) findViewById(R.id.txtFM);
			} else if (name == "Front-Left") {
				text = (TextView) findViewById(R.id.txtFL);
			} else if (name == "Front-Right") {
				text = (TextView) findViewById(R.id.txtFR);
			}	

			// set text on screen
			if (text != null) {
				// set text on screen
				text.setText(Integer.toString(sensors.get(i)
						.getCurrentDistance()));
			}
		}
	}

	@Override
	public void onPositionReceived(IPosition position) {

		// log
		Log.d(LOG_TAG, "position data received");

		TextView pos = (TextView) findViewById(R.id.txtOdometry);

		// refresh position data
		if (position != null & pos != null) {
			pos.setText(position.toString());
		}
	}

}
