package at.uibk.informatik.androbot.control.requests;

import android.os.Handler;
import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.contracts.IConnection;


public class MoveDistanceRequest extends Request {

	public MoveDistanceRequest(IConnection conn, Handler handler, byte distance, long runtime) {
		super(conn, handler);
		
		setCommand('k');
		addParameter(distance);
		setRuntime(runtime);
	}	
}
