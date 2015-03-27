package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;


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
