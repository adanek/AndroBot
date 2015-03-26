package Sprint;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.os.Handler;
import android.os.Message;

public class Robot implements IRobot {

	private IConnection connection;
	private Queue<IRequest> requests;
	private boolean executing;
	private Handler caller;
	private double linearCorrection;
	private double angularCorrection;

	public Robot(IConnection connection) {

		this.connection = connection;
		this.connection.setReadHandler(readHandler);
		
		this.linearCorrection = 1.0;
		this.angularCorrection = 1.0;

		this.requests = new LinkedList<IRequest>();
		this.executing = false;
	}

	@Override
	public void connect() {
		this.connection.connect();
	}

	@Override
	public void disconnect() {
		this.connection.stop();
	}

	@Override
	public boolean isConnected() {
		return this.connection.getState() == IConnection.STATE_CONNECTED;
	}

	public void setCaller(Handler caller) {
		this.caller = caller;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLinearCorrection(double newValue) {
		this.linearCorrection = newValue;

	}

	@Override
	public double getLinearCorrection() {
		return this.linearCorrection;
	}

	@Override
	public void setAngularCorrection(double newValue) {
		this.angularCorrection = newValue;
	}

	@Override
	public double getAngularCorrection() {
		return this.angularCorrection;
	}

	@Override
	public void moveForward() {
		this.addSimpleCommandRequest('w');
	}

	@Override
	public void moveBackward() {
		this.addSimpleCommandRequest('x');
	}

	@Override
	public void moveDistance(int distance_cm) {

		// Calculate total distance
		int distanceLeft = (int)(distance_cm * this.linearCorrection);
		
		// Split large distances into smaller parts
		while (distanceLeft > 0) {

			int maxStepSize = Byte.MAX_VALUE;
			byte step = (byte) (distanceLeft > maxStepSize ? maxStepSize : distanceLeft);
			distanceLeft -= step;

			IRequest req = new MoveDistanceRequest(connection, readHandler, step);
			this.addRequest(req);
		}
	}

	@Override
	public void setVelocity(int left, int right) {
		SetVelocityRequest req = new SetVelocityRequest(connection, readHandler);
		req.setLeftWheelVelocity((byte)left);
		req.setRightWheelVelocity((byte)right);
		
		this.addRequest(req);		
	}

	@Override
	public synchronized void stop() {
		this.requests.clear();
		this.executing = false;

		this.addSimpleCommandRequest('s');
	}

	@Override
	public void turn(Direction direction, int degrees) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void turnLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void turnRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBar(int degrees) {
		// TODO Auto-generated method stub

	}

	@Override
	public void barLower() {
		// TODO Auto-generated method stub

	}

	@Override
	public void barRise() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLeds(byte red, byte blue) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getOdomentry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOdomentry(byte xlow, byte xheigh, byte ylow, byte yheigh, byte alphalow, byte alphaheigh) {
		// TODO Auto-generated method stub

	}



	@Override
	public List<IDistanceSensor> getSensors() {
		// TODO Auto-generated method stub
		return null;
	}

	private void addSimpleCommandRequest(char command) {
		IRequest req = new SimpleCommandRequest(connection, readHandler, command);
		this.addRequest(req);
	}

	private synchronized void addRequest(IRequest request) {

		this.requests.add(request);

		if (executing)
			return;

		executeNext();
	}

	private synchronized void executeNext() {

		if (requests.isEmpty()) {
			this.executing = false;
			return;
		}

		this.executing = true;
		readHandler.post(requests.remove());
	}

	private Handler readHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case IRequest.REQUEST_EVENT:
				switch (msg.arg1) {
				case IRequest.REQUEST_SENT:
					if (executing)
						executeNext();
					break;
				}
			default:
				Message m = caller.obtainMessage();
				m.copyFrom(msg);
				m.sendToTarget();
			}
		};
	};

}
