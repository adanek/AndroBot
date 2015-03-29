package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

/**
 *  Sends a turn request to the remote device
 * 
 * 	For a turn to the left set the angle to values 0-127 degrees
 *  For a turn to the right set the angle between -1 - -127 degrees
 *
 */
public class TurnByAngleRequest extends Request {	

	public TurnByAngleRequest(IConnection conn, Handler handler, byte angle, int runtime) {
		super(conn, handler);
		
		setCommand('l');
		addParameter(angle);
		setRuntime(runtime);		
	}
}
