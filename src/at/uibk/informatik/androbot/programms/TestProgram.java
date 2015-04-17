package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.Position;

public class TestProgram extends ProgrammBase {

	public static final int SENSORS = 20;
	public static final int POSITION = 30;

	private Handler requester;
	private boolean running = false;

	public TestProgram(Context context, IRobotResponseCallback listener) {
		super(context, listener);

		this.requester = new Handler(new Callback());
	}

	@Override
	protected void onExecute() {

		IRobot rob = getRobot();
		this.running = true;

		rob.setOdomentry(Position.RootPosition());
		rob.requestSensorData();
		rob.requestCurrentPosition();

		rob.moveForward();

	}

	@Override
	public void onPositionReceived(IPosition position) {
		super.onPositionReceived(position);

		if (running ) {
			Message msg = requester.obtainMessage(POSITION);
			requester.sendMessageDelayed(msg, 200);
		}
	}
	
	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		super.onSensorDataReceived(sensors);
		
		if (running ) {
			Message msg = requester.obtainMessage(SENSORS);
			requester.sendMessageDelayed(msg, 200);
		}
	}

	private class Callback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case SENSORS:
				getRobot().requestSensorData();
				break;

			case POSITION:
				getRobot().requestCurrentPosition();
				break;

			default:
				return false;
			}

			return true;
		}

	}
}
