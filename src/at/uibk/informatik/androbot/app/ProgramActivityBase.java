package at.uibk.informatik.androbot.app;

import android.app.Activity;
import at.uibk.informatik.androbot.programms.ProgrammBase;

public abstract class ProgramActivityBase extends Activity {

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
	
	

}