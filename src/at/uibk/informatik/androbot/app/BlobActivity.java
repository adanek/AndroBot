package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.BlobDetection;
import at.uibk.informatik.androbot.programms.SquareTest;

public class BlobActivity extends ProgramActivityBase implements IRobotResponseCallback{

	private BlobDetection blob;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blob);
		
		//create square test instance
		blob = new BlobDetection(getApplicationContext(), this);
		this.setProgramm(blob);
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
		
		//start square test
		blob.start();
		
	}
	
	//Stop
	public void onStop(View v){
		
		//stop square test
		blob.stop();
		
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {		
	}

	@Override
	public void onPositionReceived(IPosition position) {
	}
	
}
