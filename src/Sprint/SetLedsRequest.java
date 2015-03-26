package Sprint;

import android.os.Handler;

public class SetLedsRequest extends RequestBase{

	private byte red;
	private byte blue;

	public SetLedsRequest(IConnection conn, Handler handler, byte red, byte blue) {
		super(conn, handler);
		
		this.red = red;
		this.blue = blue;
	}

	@Override
	protected void sendRequest() {
		
		byte[] data = new byte[]{'u', red, blue, '\r','\n'};
		String msg  = String.format("%s %2X %2X", (char) data[0], data[1], data[2]);
		sendCommand(data, msg  );
	}

}
