package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.TestProgram;

public class MainActivity extends ProgramActivityBase implements IRobotResponseCallback{

	private TestProgram test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.test= new TestProgram(getApplicationContext(), this);
		setProgramm(test);
	}

	@Override
	protected void onResume() {
		super.onResume();				
	}

	@Override
	protected void onPause() {
		super.onPause();	
	}
	
	public void onStart(View view){
		test.start();
	}
	
	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		TextView tv = (TextView) findViewById(R.id.txtOutput);
		tv.setText("Sensor data received");
	}

	@Override
	public void onPositionReceived(IPosition position) {		
		
	}
}
