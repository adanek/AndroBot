package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class MainActivity extends Activity implements IRobotResponseCallback{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();				
	}

	@Override
	protected void onPause() {
		super.onPause();	
	}
	
	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		
	}

	@Override
	public void onPositionReceived(IPosition position) {		
		
	}
}
