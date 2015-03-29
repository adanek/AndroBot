package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.contracts.IConnection;

public class SetLedsRequest extends Request{

	public SetLedsRequest(IConnection conn, Handler handler, byte red, byte blue) {
		super(conn, handler);
		
		setCommand('u');
		addParameter(red);
		addParameter(blue);
	}
}
