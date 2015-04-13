package at.uibk.informatik.androbot.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	@Override
	protected void onResume() {
		super.onResume();
				
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}

	//Settings
	public void onSettings(View v){
		
		//call settings view
		Intent settings = new Intent(this, SettingsActivity.class);
		startActivity(settings);
		
	}
	
	//Basic Control
	public void onBasic(View v){
		
		//call basic control view
		Intent basic = new Intent(this, BasicControlActivity.class);
		startActivity(basic);
		
	}
	
	//Square Test
	public void onSquare(View v){
		
		//call square test view
		Intent square = new Intent(this, SquareTestActivity.class);
		startActivity(square);
		
	}
	
	//Find Goal
	public void onGoal(View v){
		
		//call square test view
		Intent goal = new Intent(this, FindGoalActivity.class);
		startActivity(goal);
		
	}
	
	public void onTest(View v){
		
		//call square test view
		Intent test = new Intent(this, MainActivity.class);
		startActivity(test);		
	}
	
	
}
