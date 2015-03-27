package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

/**
 *  Sends a set bar request to the remote device
 */
public class SetBarRequest extends RequestBase {

	private byte position;

	public SetBarRequest(IConnection conn, Handler handler, byte position) {
		super(conn, handler);
		this.setPosition(position);
	}
	
	/**
	 * @return the position
	 */
	public byte getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(byte position) {
		this.position = position;
	}
	
	@Override
	protected void sendRequest() {
		
		setDelay(500);		
		sendCommand('o', position);
	}



}
