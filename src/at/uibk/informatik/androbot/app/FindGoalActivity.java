package at.uibk.informatik.androbot.app;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.FindGoal;

public class FindGoalActivity extends ProgramActivityBase implements IRobotResponseCallback{

	private FindGoal findGoal;
	
	private static double X = 1.0;
	private static double Y = 1.0;
	private static double TH = 1.0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);
		
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
		
		X = Double.valueOf(x.getText().toString());
		Y = Double.valueOf(y.getText().toString());
		TH = Double.valueOf(th.getText().toString());
		
		//set X value
		findGoal.setX(X);
		findGoal.setY(Y);
		findGoal.setTH(TH);
		
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
	}

	@Override
	public void onPositionReceived(IPosition position) {
	}
	
}
