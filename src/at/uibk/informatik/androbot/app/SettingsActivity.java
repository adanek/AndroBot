package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.Settings;

public class SettingsActivity extends Activity implements IRobotResponseCallback{ 

	private static final String LOG_TAG = "Settings";
	
	public static boolean useFakeConnection = false;
	//public static String MacAddress = "00:26:83:30:F7:E8";
	//public static String MacAddress = "20:13:08:16:10:42"; //0C:8B:FD:CC:54:51
	public static String MacAddress = "0C:8B:FD:CC:54:51"; //Laptop Andi
	
	public static double LinearCorrection = 1.0; // Last Value 1.34
	public static double LinearRuntimePerCentimeter = 100;
	public static double AngularCorrecion = 1.0; // Last Value 1.55
	public static double AngularRuntimePerDegree = 25;
	
	private static double DistanceTwenty = 20;
	private static double DistanceForty = 40;
	private static int    Degrees = 360;
	
	private Settings configuration;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		//create settings distance
		configuration = new Settings(getApplicationContext(), this);
	}

	@Override
	protected void onResume() {
		
		//super
		super.onResume();
		
		//get screen elements
		EditText mac     = (EditText) findViewById(R.id.inputMAC);
		EditText twenty  = (EditText) findViewById(R.id.inputTwenty);
		EditText forty = (EditText) findViewById(R.id.inputForty);
		EditText threeSixty = (EditText) findViewById(R.id.inputThreeSixty);
		
		//set texts on screen
		mac.setText(SettingsActivity.MacAddress);
		twenty.setText(String.valueOf(DistanceTwenty));
		forty.setText(String.valueOf(DistanceForty));
		threeSixty.setText(String.valueOf(Degrees));
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}
	
	//Save settings
	public void onSave(View v){
		
		//get screen elements
		EditText mac     = (EditText) findViewById(R.id.inputMAC);
		EditText twenty  = (EditText) findViewById(R.id.inputTwenty);
		EditText forty   = (EditText) findViewById(R.id.inputForty);
		EditText degrees = (EditText) findViewById(R.id.inputThreeSixty);
		
		//update values
		String macAddress = mac.getText().toString();
		DistanceTwenty   = Double.valueOf(twenty.getText().toString());
		DistanceForty    = Double.valueOf(forty.getText().toString());
		Degrees          = Integer.valueOf(degrees.getText().toString());
		
		//set correction values
		LinearCorrection = 1 + ((20 - DistanceTwenty) / 20 + (40 - DistanceForty) / 40) / 2;
		AngularCorrecion = 360.0 / Degrees;
		
		//set MAC address
		SettingsActivity.MacAddress = macAddress;
		
		//log
		Log.d(LOG_TAG, "Mac Address " + macAddress + " set");
		Log.d(LOG_TAG, "Linear coefficient " + LinearCorrection + " set");
		Log.d(LOG_TAG, "Angular coefficient " + AngularCorrecion + " set"); 

		//back to start activity
		finish();
	}
	
	//20 cm test
	public void onTwenty(View v){
		
		Log.d(LOG_TAG, "20 cm test started");
		
		configuration.setDistance(20);
		
		// 0 = distance test
		configuration.test = 0;
		configuration.start();
		
	}
	
	//40 cm test
	public void onForty(View v){
		
		Log.d(LOG_TAG, "40 cm test started");
		
		configuration.setDistance(40);
		
		// 0 = distance test
		configuration.test = 0;
		configuration.start();
		
	}
	
	//360 ° test
	public void onThreeSixty(View v){
		
		Log.d(LOG_TAG, "360 ° test started");
		
		configuration.setDegrees(360);
		
		// 1 = turn test
		configuration.test = 1;
		configuration.start();
		
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {			
	}

	@Override
	public void onPositionReceived(IPosition position) {		
	}
	
}
