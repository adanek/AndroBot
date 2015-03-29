package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.SquareTest;

public class SquareTestActivity extends ProgramActivityBase implements IRobotResponseCallback{

	private SquareTest squareTest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);
		
		//create square test instance
		squareTest = new SquareTest(getApplicationContext(), this);
		this.setProgramm(squareTest);
	}

	@Override
	protected void onResume() {
		super.onResume();
				
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}

	//Start
	public void onStart(View v){
		
		SeekBar distSeek = (SeekBar) findViewById(R.id.distance);
		
		//get distance value from seekbar
		squareTest.setDistance(distSeek.getProgress());
		
		//start square test
		squareTest.start();
		
	}
	
	//Stop
	public void onStop(View v){
		
		//stop square test
		squareTest.stop();
		
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {		
	}

	@Override
	public void onPositionReceived(IPosition position) {
	}
	
}
