package at.uibk.informatik.androbot.app;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import at.uibk.informatik.androbot.control.DistanceSensor;
import at.uibk.informatik.androbot.programms.TestProgram;

public class MainActivity extends ProgramActivityBase{

	private static final String LOG_TAG = "TestActivity";
	
	private TestProgram test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.test= new TestProgram(getApplicationContext());
		setProgramm(test);
	}
	
	public void onStart(View view){
		test.start();
	}
	
	public void onStop(View view){
		
	}

}
