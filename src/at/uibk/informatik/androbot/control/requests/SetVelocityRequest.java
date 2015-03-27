package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

/**
 * Sends a request to set the speed of each wheel separately *
 */
public class SetVelocityRequest extends RequestBase {

	private byte leftWheel;
	private byte rightWheel;
	
	public SetVelocityRequest(IConnection conn, Handler handler) {
		super(conn, handler);	
	}

	public byte getLeftWheelVelocity() {
		return leftWheel;
	}

	public void setLeftWheelVelocity(byte leftWheel) {
		this.leftWheel = leftWheel;
	}
	
	public byte getRightWheelVelocity() {
		return rightWheel;
	}

	public void setRightWheelVelocity(byte rightWheel) {
		this.rightWheel = rightWheel;
	}
	@Override
	protected void sendRequest() {
		byte[] data = new byte[]{'i', leftWheel, rightWheel, '\r', '\n'};
		
		String msg = String.format("%s 0x%2X 0x%2X\n", (char)(data[0]), data[1], data[2]);
		this.sendCommand(data, msg);
	}
}
