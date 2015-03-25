package Sprint;

import android.os.Handler;

public class MoveBackwardsRequest extends RequestBase{

	public MoveBackwardsRequest(IConnection conn, Handler handler) {
		super(conn, handler);
	}

	@Override
	protected void sendRequest() {
		
		this.sendCommand('x');		
	}

}
