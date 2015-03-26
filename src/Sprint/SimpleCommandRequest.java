package Sprint;

import android.os.Handler;

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
