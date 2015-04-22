package at.uibk.informatik.androbot.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import at.uibk.informatik.androbot.programms.Settings;

public class SettingsActivity extends ProgramActivityBase{

	public static final String LINEAR_CORRECTION = "LinearCorrection";
	public static final String ANGULAR_CORRECTION = "AngularCorrection";
	public static final String USE_FAKECONNECTION = "UseFakeConnection";	
	private static final String LOG_TAG = "Settings";
	public static final String LINEAR_RUNTIME = "LinearRuntime";
	public static final String ANGULAR_RUNTIME = "AngularRuntime";

	public static boolean useFakeConnection = true;
	// public static String MacAddress = "00:26:83:30:F7:E8";
	//public static String MacAddress = "20:13:08:16:10:50"; // 0C:8B:FD:CC:54:51
	//public static String MacAddress = "0C:8B:FD:CC:54:51"; //Laptop Andi
	public static String MacAddress = "20:13:08:16:10:50"; //Laptop Andi

	public static double LinearRuntimePerCentimeter = 100;
	public static double AngularCorrecion = 1.0; // Last Value 1.55
	public static double AngularRuntimePerDegree = 25;


	private Settings configuration;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private long TestStartTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// create settings distance
		configuration = new Settings(getApplicationContext());
		this.setProgramm(configuration);
	}

	@Override
	protected void onResume() {

		// super
		super.onResume();

		// Get persistent settings
		settings = getSharedPreferences("Androbot_Settings", MODE_PRIVATE);
		editor = settings.edit();
		
		// Bind controls
		CheckBox fake = (CheckBox) findViewById(R.id.chkFakeConnection);
		
		
		// Set values
		fake.setChecked(settings.getBoolean(USE_FAKECONNECTION, false));
		
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	public void setUseFakeConnection(View view){
		boolean newValue = ((CheckBox) view).isChecked();
		
		Log.d(LOG_TAG, String.format("Use fake connection set to %b", newValue));
		editor.putBoolean(USE_FAKECONNECTION, newValue);
		editor.commit();
	}
	
	public void runLinearCorrectionTest(View view) {

		EditText dis = (EditText) findViewById(R.id.eLinearCorrectionExpect);
		int distance = Integer.valueOf(dis.getText().toString());
		configuration.runLinearCorrectionTest(distance);
	}

	public void setLinearCorrection(View view) {

		EditText tvExpected = (EditText) findViewById(R.id.eLinearCorrectionExpect);
		EditText tvGot = (EditText) findViewById(R.id.eLinearCorrectionGot);
		
		int expected = Integer.valueOf(tvExpected.getText().toString());
		int got = Integer.valueOf(tvGot.getText().toString());
		
		float current = settings.getFloat(LINEAR_CORRECTION, 1.0f); 
		current *= expected * 1.0f / got;
	
		Log.d(LOG_TAG, String.format("Linear correction set to %f", current));
		editor.putFloat(LINEAR_CORRECTION, current);
		editor.commit();
		
		configuration.setLinearCorrectionValue(current);
		
		//reset Text
		tvGot.setText("");
	}
	
	
	public void startLinearRuntimeTest(View view){
		
		EditText dis = (EditText) findViewById(R.id.eLinearRuntimeDistance);
		int distance = Integer.valueOf(dis.getText().toString());
		
		TestStartTime = System.currentTimeMillis();
		configuration.runLinearRuntimeTest(distance);
	}
	
	
	public void stopLinearRuntimeTest(View view){
		
		long endTime = System.currentTimeMillis();
		configuration.stopLinearRuntimeTest();
		
		EditText dis = (EditText) findViewById(R.id.eLinearRuntimeDistance);
		int distance = Integer.valueOf(dis.getText().toString());
		
		long diff = endTime - TestStartTime;
		float current = settings.getFloat(LINEAR_RUNTIME, 1.0f);		
		current = diff / (1.0f * distance); 
		
		Log.d(LOG_TAG, String.format("Linear runtime per centimeter set to %f", current));
		editor.putFloat(LINEAR_RUNTIME, current);
		editor.commit();
		
		configuration.setLinearRuntime(current);
	}
	
	public void runAngularCorrectionTest(View view){
		
		EditText tvFactor =  (EditText) findViewById(R.id.eAngularCorrectionExpect);
		int factor = Integer.valueOf(tvFactor.getText().toString());
		
		configuration.runAngularCorrectionTest(factor);		
	}
	
	public void setAngularCorrection(View v){
		
		EditText tvFactor = (EditText) findViewById(R.id.eAngularCorrectionExpect);
		EditText tvRes    = (EditText) findViewById(R.id.eAngularCorrectionGot);
		
		int factor = Integer.valueOf(tvFactor.getText().toString());
		int res    = Integer.valueOf(tvRes.getText().toString());
		
		factor = Math.abs(factor);
		
		int expected = factor * 90;
		
		float current = settings.getFloat(ANGULAR_CORRECTION, 1.0f); 
		current *= expected * 1.0f / res;
		
		Log.d(LOG_TAG, String.format("Angular correction set to %f", current));
		editor.putFloat(ANGULAR_CORRECTION, current);
		editor.commit();
		
		configuration.setAngularCorrectionValue(current);
		
		//reset text
		tvRes.setText("");
		
	}
	
	public void runAngularRuntimeTest(View view){
		TestStartTime = System.currentTimeMillis();
		configuration.runAngularRuntimeTest();
	}
	
	public void stopAngularRuntimeTest(View view){
		
		long endTime = System.currentTimeMillis();		
		long diff = endTime - TestStartTime;
		
		float current = settings.getFloat(ANGULAR_RUNTIME, 1.0f);		
		current = diff / (1.0f * 90); 
		
		Log.d(LOG_TAG, String.format("Angular runtime per centimeter set to %f", current));
		editor.putFloat(ANGULAR_RUNTIME, current);
		editor.commit();
		
		configuration.setAngularRuntime(current);		
	}
	
	
}



