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
	
	public void onTest(View v){
		
		//call square test view
		Intent test = new Intent(this, MainActivity.class);
		startActivity(test);		
	}
	
	
}
