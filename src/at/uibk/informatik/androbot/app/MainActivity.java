package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.TestProgram;

public class MainActivity extends ProgramActivityBase implements IRobotResponseCallback{

	private static final String LOG_TAG = "TestActivity";
	
	private TestProgram test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.test= new TestProgram(getApplicationContext(), this);
		setProgramm(test);
	}

	@Override
	protected void onResume() {
		super.onResume();				
	}

	@Override
	protected void onPause() {
		super.onPause();	
	}
	
	public void onStart(View view){
		test.start();
	}
	
	public void onStop(View view){
		test.stop();
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
		
		//get position values
		int x = position.getX();
		int y = position.getY();
		int th = position.getOrientation();
		
		//get screen elements
		TextView txtX  = (TextView) findViewById(R.id.txtX);
		TextView txtY  = (TextView) findViewById(R.id.txtY);
		TextView txtTH = (TextView) findViewById(R.id.txtTH);
		
		//set values on screen
		txtX.setText(Integer.toString(x));
		txtY.setText(Integer.toString(y));
		txtTH.setText(Integer.toString(th));
		
	}
}
