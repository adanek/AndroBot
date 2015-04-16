package at.uibk.informatik.androbot.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.app.R;
import at.uibk.informatik.androbot.contracts.Constants;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IRequest;

public class FakeConnection implements IConnection {

	private static final String LOG_TAG = "FakeBot";
	private Handler caller;
	private int state; 

	int fr = 85;
	int fm = 30;
	int fl = 85;
	
	public FakeConnection(){
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
		
		msg = caller.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(Constants.DEVICE_NAME, LOG_TAG);
		msg.setData(bundle);
		caller.sendMessage(msg);
	}

	@Override
	public void stop() {
		this.state = STATE_NONE;
		caller.obtainMessage(Constants.MESSAGE_STATE_CHANGE, STATE_NONE, -1).sendToTarget();

	}

	@Override
	public void write(byte[] out) {
		sendRequest(out);
		
		char c = (char)out[0];
		if(c == 'q')
			sendSensorResponse();
		if(c == 'h')
			sendPositionResponse();

	}

	private void sendPositionResponse() {
		String response;
		response = "odometry: 0x00 0x00 0x00 0x00 0x00 0x00";
		
		Log.d(LOG_TAG, "Receiving: " + response);
		
		byte[] buffer = response.getBytes();
		int bytes = buffer.length;
		
		Message msg = caller.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer);
		caller.sendMessageDelayed(msg, 200);
	}

	private void sendSensorResponse() {		
		
		String response = String.format("sensor: 0xFF 0xFF 0x%02x 0x%02x 0xFF 0x%02x 0xFF 0xFF", fl, fr, fm );
				
		fl--;
		fr--;
		fm--;
		
		Log.d(LOG_TAG, "Receiving: " + response);
		
		byte[] buffer = response.getBytes();
		int bytes = buffer.length;
		
		Message msg = caller.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer);
		caller.sendMessageDelayed(msg, 200);
	}

	private void sendRequest(byte[] out) {
		StringBuilder sb = new  StringBuilder();
		
		sb.append("Sending: ");
		sb.append((char)(out[0]));	

		for(int i = 1; i< out.length-2; i++){
			String val = String.format(" %2X", out[i]);
			sb.append(val);
		}
		
		sb.append("\r\n");
		
		Log.d(LOG_TAG, sb.toString());
	}

	@Override
	public void setReadHandler(Handler readHandler) {
		this.caller = readHandler;
	}

}
