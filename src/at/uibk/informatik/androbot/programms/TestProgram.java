package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class TestProgram extends ProgrammBase {

	public TestProgram(Context context, IRobotResponseCallback listener) {
		super(context, listener);		
	}

	@Override
	protected void onExecute() {		

		getRobot().requestSensorData();
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPositionReceived(IPosition position) {
		// TODO Auto-generated method stub
		
	}

}
