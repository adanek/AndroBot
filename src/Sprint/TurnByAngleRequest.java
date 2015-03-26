package Sprint;

import android.os.Handler;

/**
 *  Sends a turn request to the remote device
 * 
 * 	For a turn to the left set the angle to values 0-127 degrees
 *  For a turn to the right set the angle between -1 - -127 degrees
 *
 */
public class TurnByAngleRequest extends RequestBase {

	private byte angle;

	public TurnByAngleRequest(IConnection conn, Handler handler, byte angle) {
		super(conn, handler);
		
		this.setAngle(angle);
	}

	/**
	 * @return the angle
	 */
	public byte getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(byte angle) {
		this.angle = angle;
		
		//TODO: Calculate runtime
		this.setDelay(1000);
	}

	@Override
	protected void sendRequest() {
		sendCommand('l', angle);
	}


}
