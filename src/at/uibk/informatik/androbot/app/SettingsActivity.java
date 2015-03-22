package at.uibk.informatik.androbot.app;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;
import at.uibk.informatik.androbot.programms.Programm;
import at.uibk.informatik.androbot.programms.SquareTest;

public class SettingsActivity extends Activity {

	private static final String LOG_TAG = "Settings";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	protected void onResume() {
		super.onResume();
				
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}
	
	//Save settings
	public void onSave(View v){
		
		EditText mac     = (EditText) findViewById(R.id.inputMAC);
		EditText linear  = (EditText) findViewById(R.id.inputLinear);
		EditText angular = (EditText) findViewById(R.id.inputAngular);
	
		String macAddress = mac.getText().toString();
		Double linearCo   = Double.valueOf(linear.getText().toString());
		Double angularCo  = Double.valueOf(angular.getText().toString());
		
		//set correction values
		Programm.setAngularCorr(angularCo);
		Programm.setLinearCorr(linearCo);
		
		//set MAC address
		Programm.setMacAddress(macAddress);
		
		//log
		Log.d(LOG_TAG, "Mac Address " + macAddress + " set");
		Log.d(LOG_TAG, "Linear coefficient " + linearCo + " set");
		Log.d(LOG_TAG, "Angular coefficient " + angularCo + " set"); 

		//back to start activity
		finish();
	}
	
}
