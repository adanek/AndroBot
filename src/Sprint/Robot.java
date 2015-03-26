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
		
		int deg = (int)(degrees * angularCorrection);
				
		while(deg > 0){
			byte step = (byte)(deg > Byte.MAX_VALUE ? Byte.MAX_VALUE: deg);
			deg -= step;
			
			// Use negative values for turn to the right
			if(direction == Direction.RIGHT){
				step *= -1;
			}
			
			IRequest req = new TurnByAngleRequest(connection, readHandler, step);
			addRequest(req);
		}
	}
	
	@Override
	public void turnLeft() {		
		turn(Direction.LEFT, 90);
	}

	@Override
	public void turnRight() {
		turn(Direction.RIGHT, 90);
	}

	@Override
	public void setBar(byte position) {
	
		IRequest req = new SetBarRequest(connection, readHandler, position);
		addRequest(req);
	}

	@Override
	public void barLower() {
		setBar((byte) 0);
	}

	@Override
	public void barRise() {
		setBar((byte) Byte.MAX_VALUE);
	}

	@Override
	public void setLeds(byte red, byte blue) {
		IRequest req = new SetLedsRequest(connection, readHandler, red, blue);
		addRequest(req);
	}
	
	@Override
	public void requestSensorData() {
		addSimpleCommandRequest('q');		
	}

	@Override
	public void requestCurrentPosition() {
		addSimpleCommandRequest('h');		
	}

	@Override
	public void setOdomentry(IPosition position) {
		// TODO Auto-generated method stub
		
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
			case Constants.MESSAGE_READ:
				parseData(msg.obj);
			default:
				Message m = caller.obtainMessage();
				m.copyFrom(msg);
				m.sendToTarget();
			}
		}

		private void parseData(Object obj) {
			String response = new String((byte[]) obj);
			
			if(response.contains("sensor:"))
				sendSensorData(response);
			
			else if(response.contains("odometry:"))
				sendPositionData(response);
		}

		private void sendPositionData(String response) {
			Message msg = caller.obtainMessage(POSITION_RECEIVED);			
			msg.obj = Position.parse(response);
			msg.sendToTarget();
			
		}

		private void sendSensorData(String response) {
			Message msg = caller.obtainMessage(SENSOR_DATA_RECEIVED);			
			msg.obj = DistanceSensor.parse(response);
			msg.sendToTarget();
		};
	};
	


}
