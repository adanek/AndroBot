package Sprint;

import android.os.Handler;

public class StopRequest extends RequestBase {

	public StopRequest(IConnection conn, Handler handler) {
		super(conn, handler);
	}

	@Override
	protected void sendRequest() {
		this.sendCommand('s');
	}

}
