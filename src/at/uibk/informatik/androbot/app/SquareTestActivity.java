package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.SquareTest;

public class SquareTestActivity extends ProgramActivityBase implements IRobotResponseCallback{

	private SquareTest squareTest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);
		
		//create square test instance
		squareTest = new SquareTest(getApplicationContext(), this);
		this.setProgramm(squareTest);
	}

	@Override
	protected void onResume() {
		super.onResume();
				
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}

	//Start
	public void onStart(View v){
		
		SeekBar distSeek = (SeekBar) findViewById(R.id.distance);
		
		//get distance value from seekbar
		squareTest.setDistance(distSeek.getProgress());
		
		//start square test
		squareTest.start();
		
	}
	
	//Stop
	public void onStop(View v){
		
		//stop square test
		squareTest.stop();
		
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {	

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
	}
	
}
