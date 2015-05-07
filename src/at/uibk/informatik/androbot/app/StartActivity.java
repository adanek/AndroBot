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
	
	//find goal activity
	public void onGoal(View v){
		
		//call square test view
		Intent find = new Intent(this, FindGoalActivity.class);
		startActivity(find);		
	}
	
	//blob activity
	public void onBlob(View v){
		
		//call square test view
		Intent blob = new Intent(this, BlobActivity.class);
		startActivity(blob);		
	}
	
	//beacon activity
	public void onBeacon(View v){
		
		//call square test view
		Intent beacon = new Intent(this, BeaconDetectionActivity.class);
		startActivity(beacon);		
	}
	
	public void onTest(View v){
		
		//call square test view
		Intent test = new Intent(this, MainActivity.class);
		startActivity(test);		
	}
	
	
}
