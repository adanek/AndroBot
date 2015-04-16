package at.uibk.informatik.androbot.app;

import java.util.List;

import android.app.Activity;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.programms.ProgrammBase;

public abstract class ProgramActivityBase extends Activity implements IRobotResponseCallback{

	private ProgrammBase program;	
	
	@Override
	protected void onResume() {		
		super.onResume();
		
		getProgram().connect();
	}
	
	@Override
	protected void onPause() {	
		super.onPause();
		
		getProgram().disconnect();
	}

	/**
	 * @return the program
	 */
	public ProgrammBase getProgram() {
		return program;
	}

	/**
	 * @param program the program to set
	 */
	public void setProgramm(ProgrammBase program) {
		this.program = program;
	}
	
	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
	}

	@Override
	public void onPositionReceived(IPosition position) {
	}

}