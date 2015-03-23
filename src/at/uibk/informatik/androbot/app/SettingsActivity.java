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
import at.uibk.informatik.androbot.programms.ProgrammBase;
import at.uibk.informatik.androbot.programms.SquareTest;

public class SettingsActivity extends Activity {

	private static final String LOG_TAG = "Settings";
	//public static String MacAddress = "00:26:83:30:F7:E8";
	public static String MacAddress = "20:13:08:16:10:42"; //
	public static double LinearCorrection = 1.34;
	public static double AngularCorrecion = 1.55;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EditText mac     = (EditText) findViewById(R.id.inputMAC);
		EditText linear  = (EditText) findViewById(R.id.inputLinear);
		EditText angular = (EditText) findViewById(R.id.inputAngular);
		
		mac.setText(SettingsActivity.MacAddress);
		linear.setText(String.valueOf(LinearCorrection));
		angular.setText(String.valueOf(AngularCorrecion));
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
//		ProgrammBase.setAngularCorr(angularCo);
//		ProgrammBase.setLinearCorr(linearCo);
		SettingsActivity.AngularCorrecion = angularCo;
		SettingsActivity.LinearCorrection = linearCo;
//		//set MAC address
//		ProgrammBase.setMacAddress(macAddress);
		SettingsActivity.MacAddress = macAddress;
//		
		//log
		Log.d(LOG_TAG, "Mac Address " + macAddress + " set");
		Log.d(LOG_TAG, "Linear coefficient " + linearCo + " set");
		Log.d(LOG_TAG, "Angular coefficient " + angularCo + " set"); 

		//back to start activity
		finish();
	}
	
}
