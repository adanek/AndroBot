package at.uibk.informatik.androbot.app;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
	
	public void onStart(View view){
		
		Position target = new Position(0,0,0);
		
		find.setTarget(target);
		
		find.start();
	}
	
	public void onStop(View view){
		
	}
	
}
