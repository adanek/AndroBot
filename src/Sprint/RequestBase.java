package Sprint;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class RequestBase implements IRequest {

	private static final String LOG_TAG = "Request";
	
	private IConnection conn;
	private Handler handler;
	private long delay;
	
	public RequestBase(IConnection conn, Handler handler) {
		super();
		this.conn = conn;
		this.handler = handler;
		this.delay = 0;
	}

	@Override
	public void run(){
		
		sendRequest();
		
		Message doneMsg = handler.obtainMessage(REQUEST_EVENT, REQUEST_SENT, -1);
		handler.sendMessageDelayed(doneMsg, delay);
	};
	
	protected void setDelay(long delay) {
		this.delay = delay;
	}

	protected abstract void sendRequest();


	protected final void sendCommand(char command) {
		Log.d(LOG_TAG, String.format("%s", Character.toString(command)));
		this.conn.write(new byte[] {(byte) command, '\r', '\n'});
	}
	
	protected final void sendCommand(char command, byte parameter) {
		Log.d(LOG_TAG, String.format("%s 0x%02X", Character.toString(command), parameter));
		this.conn.write(new byte[] {(byte) command, parameter, '\r', '\n'});
	}
}