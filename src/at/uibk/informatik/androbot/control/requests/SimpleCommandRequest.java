package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

/**
 * Sends the command to the remote device an answers immediately
 */
public class SimpleCommandRequest extends Request {

	public SimpleCommandRequest(IConnection conn, Handler handler, char command) {
		super(conn, handler);
		
		setCommand(command);
	} 
}
