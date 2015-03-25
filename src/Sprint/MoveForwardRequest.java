package Sprint;

import android.os.Handler;

/**
 * Let the robot moves constantly forward until it receives another request
 */
public class MoveForwardRequest extends RequestBase {

	public MoveForwardRequest(IConnection conn, Handler handler) {
		super(conn, handler);			
	}

	@Override
	protected void sendRequest() {
		
		this.sendCommand('w');

	}

}
