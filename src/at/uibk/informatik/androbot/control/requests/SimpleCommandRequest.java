package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

/**
 * Sends the command to the remote device an answers immediately
 */
public class SimpleCommandRequest extends RequestBase {

	private char command;

	public SimpleCommandRequest(IConnection conn, Handler handler, char command) {
		super(conn, handler);
		this.command = command;
	}

	@Override
	protected void sendRequest() {
		sendCommand(command);
	}

}
