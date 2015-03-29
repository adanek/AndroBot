package at.uibk.informatik.androbot.control;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.Constants;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRequest;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.requests.MoveDistanceRequest;
import at.uibk.informatik.androbot.control.requests.SetBarRequest;
import at.uibk.informatik.androbot.control.requests.SetLedsRequest;
import at.uibk.informatik.androbot.control.requests.SetVelocityRequest;
import at.uibk.informatik.androbot.control.requests.SimpleCommandRequest;
import at.uibk.informatik.androbot.control.requests.TurnByAngleRequest;

public class Robot implements IRobot {

	private static final String LOG_TAG = "Robot";
	private IConnection connection;
	private Queue<IRequest> requests;
	private boolean executing;
	private Handler caller;
	private Handler connectionHandler;
	private double linearCorrection;
	private double angularCorrection;	

	public Robot(IConnection connection, Handler caller) {

		this.caller = caller;
	
		this.connectionHandler = new Handler(new ConnectionCallback());
		this.connection = connection;
		this.connection.setReadHandler(connectionHandler);

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
		int distanceLeft = (int) (distance_cm * this.linearCorrection);

		// Split large distances into smaller parts
		while (distanceLeft > 0) {

			int maxStepSize = Byte.MAX_VALUE;
			byte step = (byte) (distanceLeft > maxStepSize ? maxStepSize
					: distanceLeft);
			distanceLeft -= step;

			IRequest req = new MoveDistanceRequest(connection,
					connectionHandler, step);
			this.addRequest(req);
		}
	}

	@Override
	public void setVelocity(int left, int right) {
		SetVelocityRequest req = new SetVelocityRequest(connection,
				connectionHandler);
		req.setLeftWheelVelocity((byte) left);
		req.setRightWheelVelocity((byte) right);

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

		int deg = (int) (degrees * angularCorrection);

		while (deg > 0) {
			byte step = (byte) (deg > Byte.MAX_VALUE ? Byte.MAX_VALUE : deg);
			deg -= step;

			// Use negative values for turn to the right
			if (direction == Direction.RIGHT) {
				step *= -1;
			}

			IRequest req = new TurnByAngleRequest(connection,
					connectionHandler, step);
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
	public void setBar(int position) {

		IRequest req = new SetBarRequest(connection, connectionHandler,
				(byte) position);
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
		IRequest req = new SetLedsRequest(connection, connectionHandler, red,
				blue);
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
		IRequest req = new SimpleCommandRequest(connection, connectionHandler,
				command);
		this.addRequest(req);
	}

	/**
	 * Adds an request to the queue and starts the execution if it is not running.
	 * @param request The request to add.
	 */
	private synchronized void addRequest(IRequest request) {

		this.requests.add(request);

		if (executing)
			return;

		executeNext();
	}

	/**
	 * Executes the next Request from the queue and stops the execution if the queue is empty
	 */
	private synchronized void executeNext() {

		if (requests.isEmpty()) {
			this.executing = false;
			return;
		}

		this.executing = true;
		connectionHandler.post(requests.remove());
	}

	/**
	 * Handles messages of type REQUEST_EVENT -> MESSAGE_READ and decides which type of response it is
	 * @param msg The incoming message of type MESSAGE_READ
	 */
	private void parseData(Message msg) {

		if (msg.obj == null) {
			Log.d(LOG_TAG, "Unable to parse message: " + msg.toString());
		}
		String response = new String((byte[]) msg.obj);

		if (response.contains("sensor:"))
			sendSensorData(response);

		else if (response.contains("odometry:"))
			sendPositionData(response);
	}

	/**
	 * Sends a message to the caller thread containing the current position of the robot
	 * 
	 * @param response The raw response string with the unparsed position.
	 */
	private void sendPositionData(String response) {

		IPosition pos = Position.parse(response);
		caller.obtainMessage(ROBOT_RESPONSE_RECEIVED, POSITION_RECEIVED, -1, pos).sendToTarget();
	}

	/**
	 * Sends a message to the caller thread containing the current sensor values of the robot
	 * 
	 * @param response The raw response string with the unparsed sensor data.
	 */
	private void sendSensorData(String response) {

		List<IDistanceSensor> sensors = DistanceSensor.parse(response);
		caller.obtainMessage(ROBOT_RESPONSE_RECEIVED, SENSOR_DATA_RECEIVED, -1, sensors).sendToTarget();
	};

	/**
	 * Handles the incomeing messages from the connection
	 * @author adanek
	 *
	 */
	private class ConnectionCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case IRequest.REQUEST_EVENT:
				switch (msg.arg1) {
				case IRequest.REQUEST_SENT:
					if (executing)
						executeNext();
					break;
				}
				break;
			case Constants.MESSAGE_READ:
				parseData(msg);
			default:
				Message m = caller.obtainMessage();
				m.copyFrom(msg);
				m.sendToTarget();
				return false;
			}

			return true;
		}
	}
}
