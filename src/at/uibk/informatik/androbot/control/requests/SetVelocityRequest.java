package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

/**
 * Sends a request to set the speed of each wheel separately *
 */
public class SetVelocityRequest extends Request {
	
	public SetVelocityRequest(IConnection conn, Handler handler, Byte left, Byte right) {
		super(conn, handler);
		
		setCommand('i');
		addParameter(left);
		addParameter(right);		
	}	
}
