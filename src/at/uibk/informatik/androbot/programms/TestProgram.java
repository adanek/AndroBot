package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import at.uibk.informatik.androbot.control.DistanceSensor;

public class TestProgram extends ProgrammBase {

	private static final String LOG_TAG = "TestProgram";

	public TestProgram(Context context) {
		super(context);
		
	}

	@Override
	protected void onExecute()  {

	}
	

	@Override
	protected void onSensordataReceived(List<DistanceSensor> sensors) {		
		super.onSensordataReceived(sensors);
		
		if (sensors == null)
			return;

		int min = 99;
		for (DistanceSensor s : sensors) {
			if (s.getCurrentDistance() < min) {
				min = s.getCurrentDistance();
			}
		}

		if (min <= 20) {
			getRobot().stop(true);
			stop();
		}
	}	
}