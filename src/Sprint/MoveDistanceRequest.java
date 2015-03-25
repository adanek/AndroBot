package Sprint;

import android.os.Handler;


public class MoveDistanceRequest extends RequestBase {

	private byte distance;

	public MoveDistanceRequest(IConnection conn, Handler handler, byte distance) {
		super(conn, handler);		
		
		this.distance = distance;
		this.setDelay(5000);
	}

	@Override
	protected void sendRequest() {
		
		this.sendCommand('k', distance);	
	}	
}
