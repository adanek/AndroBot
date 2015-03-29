package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

/**
 *  Sends a set bar request to the remote device
 */
public class SetBarRequest extends Request {	

	public SetBarRequest(IConnection conn, Handler handler, byte position) {
		super(conn, handler);
		
		setCommand('o');
		addParameter(position);
		setRuntime(500);
	}
}
