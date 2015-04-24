package at.uibk.informatik.androbot.app;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import at.uibk.informatik.androbot.control.DistanceSensor;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.programms.findGoal;

public class FindGoalActivity extends ProgramActivityBase{

	private static final String LOG_TAG = "FindGoalActivity";
	
	private findGoal find;
	
	private int X  = 100;
	private int Y  = 100;
	private int TH = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findgoal);
		
		this.find = new findGoal(getApplicationContext());
		setProgramm(find);
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
	
	//start find goal activity
	public void onStart(View view){
		
		EditText x  = (EditText) findViewById(R.id.inputX);
		EditText y  = (EditText) findViewById(R.id.inputY);
		EditText th = (EditText) findViewById(R.id.inputTH);
		
		X  = Integer.valueOf(x.getText().toString());
		Y  = Integer.valueOf(y.getText().toString());
		TH = Integer.valueOf(th.getText().toString());
		
		//create target position
		Position target = new Position(X,Y,TH);
		
		//set target
		find.setTarget(target);
		
		//start activity
		find.start();
	}
	
	public void onStop(View view){
		
	}
	
	//reset robots current position
	public void onReset(View v){
		
		//set current position to 0,0,0
		find.setCurrent(new Position());
		
	}
	
}
