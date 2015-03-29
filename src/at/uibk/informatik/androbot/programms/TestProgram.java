package at.uibk.informatik.androbot.programms;

import android.content.Context;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class TestProgram extends ProgrammBase {

	public TestProgram(Context context, IRobotResponseCallback listener) {
		super(context, listener);		
	}

	@Override
	protected void onExecute() {		

		getRobot().requestSensorData();
	}

}
