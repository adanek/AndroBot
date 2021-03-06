package at.uibk.informatik.androbot.control;

import java.nio.ByteBuffer;
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
import at.uibk.informatik.androbot.control.requests.Request;
import at.uibk.informatik.androbot.control.requests.SetBarRequest;
import at.uibk.informatik.androbot.control.requests.SetLedsRequest;
import at.uibk.informatik.androbot.control.requests.SetVelocityRequest;
import at.uibk.informatik.androbot.control.requests.SimpleCommandRequest;
import at.uibk.informatik.androbot.control.requests.TurnByAngleRequest;

public class Robot implements IRobot {

	// *********************************************** Locals *********************************************************
	private static final String LOG_TAG = "Robot";
	private IConnection connection;
	private Queue<IRequest> requests;
	private boolean executing;
	private Handler caller;
	private Handler connectionHandler;
	private double linearCorrection;
	private double linearRuntimePerCentimeter;
	private double angularCorrection;
	private double angularRuntimePerDegree;

	// ******************************************** Constructors ******************************************************

	public Robot(IConnection connection, Handler caller) {

		this.caller = caller;

		this.connectionHandler = new Handler(new ConnectionCallback());
		this.connection = connection;
		this.connection.setReadHandler(connectionHandler);

		this.linearCorrection = 1.0;
		this.setLinearRuntimePerCentimeter(0.03);
		this.angularCorrection = 1.0;
		this.setAngularRuntimePerDegree(0.127);

		this.requests = new LinkedList<IRequest>();
		this.executing = false;
	}

	// ********************************************* Properties *******************************************************

	public void setCaller(Handler caller) {
		this.caller = caller;
	}

	@Override
	public void setLinearCorrection(double newValue) {
		this.linearCorrection = newValue;

	}

	@Override
	public void setLinearRuntimePerCentimeter(double linearRuntimePerCentimeter) {
		this.linearRuntimePerCentimeter = linearRuntimePerCentimeter;
	}

	@Override
	public void setAngularCorrection(double newValue) {
		this.angularCorrection = newValue;
	}

	@Override
	public void setAngularRuntimePerDegree(double angularRuntimePerDegree) {
		this.angularRuntimePerDegree = angularRuntimePerDegree;
	}

	// ********************************************** Methods *********************************************************

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

	@Override
	public void initialize() {

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
		
		
		long runtime = (long) (distance_cm * linearRuntimePerCentimeter);
		Log.d(LOG_TAG, String.format("Calculated runtime: %d",	runtime));
		
		Request move = new Request(connection, connectionHandler);
		move.setCommand('i');
		move.addParameter((byte)20);
		move.addParameter((byte)20);
		
		Request stop = new Request(connection, connectionHandler);
		stop.setCommand('s');
		
		connectionHandler.post(move);
		connectionHandler.postDelayed(stop, runtime);
		
		
		
		
		
		

//		// Calculate total distance
//		int distanceLeft = (int) (distance_cm * this.linearCorrection);
//
//		// Split large distances into smaller parts
//		while (distanceLeft > 0) {
//
//			int maxStepSize = Byte.MAX_VALUE;
//			byte step = (byte) (distanceLeft > maxStepSize ? maxStepSize : distanceLeft);
//			distanceLeft -= step;
//
//			// Calculate the runtime
//			long runtime = (long) (linearRuntimePerCentimeter * step);
//
//			IRequest req = new MoveDistanceRequest(connection, connectionHandler, step, runtime);
//			this.addRequest(req);
//		}
	}

	@Override
	public void setVelocity(int left, int right) {
		SetVelocityRequest req = new SetVelocityRequest(connection, connectionHandler, (byte) left, (byte) right);
		this.addRequest(req);
	}

	@Override
	public synchronized void stop(boolean immediately) {

		if (immediately) {
			this.requests.clear();
			this.stopExecution();
		}

		this.addSimpleCommandRequest('s');
	}

	@Override
	public void turn(Direction direction, int degrees) {

		Log.d(LOG_TAG, String.format("angular correction %f %f",  angularCorrection, angularRuntimePerDegree));
		int deg = (int) (degrees * angularCorrection);

		while (deg > 0) {

			// Calculate the step width
			byte step = (byte) (deg > Byte.MAX_VALUE ? Byte.MAX_VALUE : deg);
			deg -= step;

			// Calculate the runtime
			int runtime = (int) (angularRuntimePerDegree * step);

			// Use negative values for turn to the right
			if (direction == Direction.RIGHT) {
				step *= -1;
			}

			Request req = new TurnByAngleRequest(connection, connectionHandler, step, runtime);
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

		IRequest req = new SetBarRequest(connection, connectionHandler, (byte) position);
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
		IRequest req = new SetLedsRequest(connection, connectionHandler, red, blue);
		addRequest(req);
	}

	@Override
	public void requestSensorData(boolean immediately) {
		
		if(immediately){
			Request req = new Request(connection, connectionHandler, false);
			req.setCommand('q');
			
			connectionHandler.post(req);
			return;
		}

		addSimpleCommandRequest('q');
	}
	
	@Override
	public void requestSensorData() {
		requestSensorData(true);
	};

	
	@Override
	public void requestCurrentPosition(boolean immediately) {
		
		if(immediately){
			Request req = new Request(connection, connectionHandler, false);
			req.setCommand('h');
			connectionHandler.post(req);
			return;
		}
		
		addSimpleCommandRequest('h');
	}
	
	
	@Override
	public void requestCurrentPosition() {
		requestCurrentPosition(false);
	}

	@Override
	public void setOdomentry(IPosition position) {
		
		Request r = new Request(connection, connectionHandler);
		r.setCommand('j');
		
		int x = position.getX();
		int y = position.getY();
		int a = position.getOrientation();
		
		byte[] xb = new byte[] {(byte) (x >>> 8), (byte) (x) };
		byte[] yb = new byte[] {(byte) (x >>> 8), (byte) (x) };
		byte[] ab = new byte[] {(byte) (x >>> 8), (byte) (x) };		
		
		byte xl = xb[1];
		byte xh = xb[0];
		byte yl = yb[1];
		byte yh = yb[0];
		byte ah = ab[1];
		byte al = ab[0];
		
		r.addParameter(xl);
		r.addParameter(xh);
		r.addParameter(yl);
		r.addParameter(yh);
		r.addParameter(al);
		r.addParameter(ah);
		
		r.setRuntime(1000);
		
		this.addRequest(r);
	}

	private void addSimpleCommandRequest(char command) {
		IRequest req = new SimpleCommandRequest(connection, connectionHandler, command);
		this.addRequest(req);
	}

	/**
	 * Adds an request to the queue and starts the execution if it is not running.
	 * 
	 * @param request
	 *            The request to add.
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
			stopExecution();
			return;
		}

		this.executing = true;
		connectionHandler.postDelayed(requests.remove(), 0);
	}

	private void stopExecution(){
		this.executing = false;
		caller.obtainMessage(ROBOT_RESPONSE_RECEIVED, IDLE, -1).sendToTarget();
	}
	/**
	 * Handles messages of type REQUEST_EVENT -> MESSAGE_READ and decides which type of response it is
	 * 
	 * @param msg
	 *            The incoming message of type MESSAGE_READ
	 */
	private void parseData(Message msg) {

		if (msg.obj == null) {
			Log.d(LOG_TAG, "Unable to parse message: " + msg.toString());
		}
		String response = new String((byte[]) msg.obj, 0, msg.arg1);		

		if (response.contains("sensor:"))
			sendSensorData(response);

		else if (response.contains("odometry:"))
			sendPositionData(response);
		else if (response.startsWith("comamnd"))
			Log.d(LOG_TAG, response);
	}

	/**
	 * Sends a message to the caller thread containing the current position of the robot
	 * 
	 * @param response
	 *            The raw response string with the unparsed position.
	 */
	private void sendPositionData(String response) {

		IPosition pos = Position.parse(response, linearCorrection, angularCorrection);
		caller.obtainMessage(ROBOT_RESPONSE_RECEIVED, POSITION_RECEIVED, -1, pos).sendToTarget();
	}

	/**
	 * Sends a message to the caller thread containing the current sensor values of the robot
	 * 
	 * @param response
	 *            The raw response string with the unparsed sensor data.
	 */
	private void sendSensorData(String response) {

		List<IDistanceSensor> sensors = DistanceSensor.parse(response);
		caller.obtainMessage(ROBOT_RESPONSE_RECEIVED, SENSOR_DATA_RECEIVED, -1, sensors).sendToTarget();
	};

	// ******************************************* Helper Classes *****************************************************

	/**
	 * Handles the incomeing messages from the connection
	 * 
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
				break;
			default:
				Message m = caller.obtainMessage();
				m.copyFrom(msg);
				m.sendToTarget();
				return false;
			}

			return true;
		}
	}

	@Override
	public double getLinearRuntimePerCentimeter() {
		
		return this.linearRuntimePerCentimeter;
	}
}
