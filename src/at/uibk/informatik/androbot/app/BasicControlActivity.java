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
import at.uibk.informatik.androbot.programms.BasicControl;
import at.uibk.informatik.androbot.programms.SquareTest;

public class BasicControlActivity extends Activity {

	private BasicControl basic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic);
		
		//create basic control instance
		basic = new BasicControl();
	}

	@Override
	protected void onResume() {
		super.onResume();
				
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}

	//Move
	public void onMove(View v){
		
		switch (v.getId()) {
		// forward
		case R.id.forward:
			basic.move(Direction.FORWARD);
			break;
		// backward
		case R.id.backward:
			basic.move(Direction.BACKWARD);
			break;
		// turn left
		case R.id.left: 
			basic.move(Direction.LEFT);
			break;
		// turn right
		case R.id.right:
			basic.move(Direction.RIGHT);
			break;
		default:
			break;
		}
		
	}
	
	//stop
	public void onStop(View v){
		basic.stop();
	}
	
}
