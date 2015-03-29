package at.uibk.informatik.androbot.control;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.Constants;
import at.uibk.informatik.androbot.contracts.IConnection;

public class MockConnection implements IConnection {

	private Handler caller;
	private int state; 
	
	public MockConnection(Handler caller){
		this.caller = caller;
		this.state = IConnection.STATE_NONE;
	}
	
	@Override
	public int getState() {
		return this.state;
	}
	
	private void setState(int newState){
		this.state = newState;

	}

	@Override
	public void connect() {
		
		this.setState(STATE_CONNECTING);
		caller.obtainMessage(Constants.MESSAGE_STATE_CHANGE, STATE_CONNECTING, -1).sendToTarget();
		
		
		this.state=STATE_CONNECTED;
		Message msg = caller.obtainMessage(Constants.MESSAGE_STATE_CHANGE, STATE_CONNECTED, -1);
		caller.sendMessageDelayed(msg, 2000);
	}

	@Override
	public void stop() {
		this.state = STATE_NONE;
		caller.obtainMessage(Constants.MESSAGE_STATE_CHANGE, STATE_NONE, -1).sendToTarget();

	}

	@Override
	public void write(byte[] out) {
		StringBuilder sb = new  StringBuilder();
		
		sb.append((char)(out[0]));
		
		for(int i = 1; i< out.length-2;i++);
		{
			sb.append(String.format(" %2X", out[i]));
		}
		
		sb.append("\r\n");
		
		Log.d("MockBot", sb.toString());

	}

	@Override
	public void setReadHandler(Handler readHandler) {
		// TODO Auto-generated method stub

	}

}
