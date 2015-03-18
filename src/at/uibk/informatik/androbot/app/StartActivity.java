package at.uibk.informatik.androbot.app;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;
import at.uibk.informatik.androbot.programms.SquareTest;

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
	
}
