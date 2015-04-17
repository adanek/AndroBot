package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.Position;

public class TestProgram extends ProgrammBase {

	public static final int SENSORS = 20;
	public static final int POSITION = 30;
	private static String LOG_TAG = "TestApp";

	private Handler requester;

	public TestProgram(Context context, IRobotResponseCallback listener) {
		super(context, listener);

		this.requester = new Handler(new Callback());
	}

	@Override
	protected void onExecute() {

		IRobot rob = getRobot();
		
		rob.setOdomentry(Position.RootPosition());		
		rob.moveForward();
		
		requester.obtainMessage(POSITION).sendToTarget();
		
		
	}

	@Override
	public void onPositionReceived(IPosition position) {
		super.onPositionReceived(position);

		if (isExecuting()) {
			
			Message msg = requester.obtainMessage(POSITION);
			requester.sendMessageDelayed(msg, 500);
		}
	}

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		super.onSensorDataReceived(sensors);

		if (isExecuting()) {
			Message msg = requester.obtainMessage(SENSORS);
			requester.sendMessageDelayed(msg, 2000);
		}
	}

	private class Callback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case SENSORS:
				getRobot().requestSensorData(false);
				break;

			case POSITION:
				getRobot().requestCurrentPosition(false);
				break;

			default:
				return false;
			}

			return true;
		}

	}
}
