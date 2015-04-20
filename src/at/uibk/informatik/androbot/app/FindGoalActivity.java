package at.uibk.informatik.androbot.app;

import java.util.List;
import java.util.Observer;

import android.database.Observable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.programms.FindGoal;

public class FindGoalActivity extends ProgramActivityBase implements IRobotResponseCallback{

	protected static final String LOG_TAG = "FindGoalActivity";

	private FindGoal findGoal;
	
	private int X  = 0;
	private int Y  = 0;
	private int TH = 0;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find);
		
		//create find goal instance
		findGoal = new FindGoal(getApplicationContext(), this);
		this.setProgramm(findGoal);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		//get screen elements
		EditText x  = (EditText) findViewById(R.id.inputX);
		EditText y  = (EditText) findViewById(R.id.inputY);
		EditText th = (EditText) findViewById(R.id.inputTH);
		
		//set texts on screen
		x.setText(String.valueOf(X));
		y.setText(String.valueOf(Y));
		th.setText(String.valueOf(TH));
				
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}

	//Start
	public void onStart(View v){
		
		EditText x  = (EditText) findViewById(R.id.inputX);
		EditText y  = (EditText) findViewById(R.id.inputY);
		EditText th = (EditText) findViewById(R.id.inputTH);
		
		X  = Integer.valueOf(x.getText().toString());
		Y  = Integer.valueOf(y.getText().toString());
		TH = Integer.valueOf(th.getText().toString());
		
		//set target
		findGoal.setTarget(new Position(X,Y,TH));
		
		//find goal!!!
		findGoal.start();
		
	}
	
	//Stop
	public void onStop(View v){
		
		//stop square test
		findGoal.stop();
		
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
		
		if(position == null){
			return;
		}
		
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
