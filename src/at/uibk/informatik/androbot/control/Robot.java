package at.uibk.informatik.androbot.control;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.graphics.Path.Direction;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Robot {

	// *********************************************** Locals *********************************************************
	private static final String LOG_TAG = "Robot";
	private IConnection connection;
	private Queue<Request> requests;
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

		this.requests = new LinkedList<Request>();
		this.executing = false;
	}

	// ********************************************* Properties *******************************************************

	public void setCaller(Handler caller) {
		this.caller = caller;
	}

	public void setLinearCorrection(double newValue) {
		this.linearCorrection = newValue;
	}

	public void setLinearRuntimePerCentimeter(double linearRuntimePerCentimeter) {
		this.linearRuntimePerCentimeter = linearRuntimePerCentimeter;
	}

	public double getLinearRuntimePerCentimeter() {
		return this.linearRuntimePerCentimeter;
	}

	public void setAngularCorrection(double newValue) {
		this.angularCorrection = newValue;
	}

	public void setAngularRuntimePerDegree(double angularRuntimePerDegree) {
		this.angularRuntimePerDegree = angularRuntimePerDegree;
	}
	public double getAngularRuntimePerDegree(){
		return this.angularRuntimePerDegree;
	}

	// ********************************************** Methods *********************************************************

	public void connect() {
		this.connection.connect();
	}

	public void disconnect() {
		this.connection.stop();
	}

	public boolean isConnected() {
		return this.connection.getState() == MessageTypes.CONNECTION_STATE_CONNECTED;
	}

//	public void moveDistance(int distance_cm) {
//
//		long runtime = (long) (distance_cm * linearRuntimePerCentimeter);
//		Log.d(LOG_TAG, String.format("Calculated runtime: %d", runtime));
//
//		Request move = new Request(connection, connectionHandler);
//		move.setCommand('i');
//		move.addParameter((byte) 20);
//		move.addParameter((byte) 20);
//		move.setRuntime(runtime);
//
//		addRequest(move);
//		this.stop(false);
//	}

	public void setVelocity(byte left, byte right) {
		
		Request req = new Request(connection, connectionHandler);
		req.setCommand('i');
		
		req.addParameter(left);
		req.addParameter(right);		

		sendRequest(req);
	}
	
	public void moveForward(){
		byte speed = 20;
		this.setVelocity(speed, speed);		
	}
	
	public void moveBackward(){
		byte speed = -15;
		this.setVelocity(speed, speed);	
	}
	
	public int getRuntime(int distance){
		
		return (int) (distance * linearRuntimePerCentimeter);		
	}
	
	
	private void sendRequest(Request r){
		connection.write(r.getData());
	}
	
	public void requestSensorData() {
		Request r = new Request(connection, connectionHandler);
		r.setCommand('q');

		sendRequest(r);
	}
	
	public void stop(){
		this.stop(true);
	}

	public void stop(boolean immediately) {

		byte speed = 0;
		this.setVelocity(speed, speed);	
	}
	
	public void setBar(byte level){
		Request r = new Request(connection, connectionHandler);
		r.setCommand('o');
		r.addParameter(level);
		
		sendRequest(r);
	}
	
	public void raiseBar(){
		Request r = new Request(connection, connectionHandler);
		r.setCommand('+');		
		
		for(int i = 0; i < 12; i++){
			sendRequest(r);
		}		
	}
	
	
//
//	// public void turn(int degrees) {
//	//
//	// Log.d(LOG_TAG, String.format("angular correction %f %f", angularCorrection, angularRuntimePerDegree));
//	// int deg = (int) (degrees * angularCorrection);
//	//
//	// while (deg > 0) {
//	//
//	// // Calculate the step width
//	// byte step = (byte) (deg > Byte.MAX_VALUE ? Byte.MAX_VALUE : deg);
//	// deg -= step;
//	//
//	// // Calculate the runtime
//	// int runtime = (int) (angularRuntimePerDegree * step);
//	//
//	// }
//	// }
//

//
//	private void addSimpleCommandRequest(char command) {
//
//	}
//
//	private synchronized void addRequest(Request request) {
//
//		this.requests.add(request);
//
//		if (executing)
//			return;
//
//		executeNext(-1);
//	}
//
//	private synchronized void executeNext(int id) {
//
//		Request req = requests.remove();
//		connectionHandler.postDelayed(req, 0);
//	}
//
	private void parseData(Message msg) {

		if (msg.obj == null) {
			Log.d(LOG_TAG, "Unable to parse message: " + msg.toString());
		}

		String response = (String)msg.obj;

		if (response.contains("sensor:"))
			sendSensorData(response);

		else {
			Log.d(LOG_TAG, response);
		}
	}

	private void sendSensorData(String response) {
		
		

		List<DistanceSensor> sensors = DistanceSensor.parse(response);
		caller.obtainMessage(MessageTypes.ROBOT_EVENT, MessageTypes.ROBOT_SENSORDATA_RECEIVED, -1, sensors).sendToTarget();
		Log.d(LOG_TAG, "Sending sensors to program");
		
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
	
			case MessageTypes.CONNECTION_MESSAGE_EVENT:
				switch(msg.arg1){
				
				case MessageTypes.CONNECTION_MESSAGE_RECEIVED:
					Log.d(LOG_TAG, "Sending sensors to program");
					parseData(msg);
					break;
				default:
					Log.w(LOG_TAG, "Unexpected: "+ msg.toString());
				}
					
					break;

//			case MessageTypes.REQUEST_EVENT:
//				switch (msg.arg1) {
//				case MessageTypes.REQUEST_DONE:
//					
//					// execute next request in queue
//					executeNext(msg.arg2);
//					break;
//				
//				default:
//					Log.w(LOG_TAG, "Unexpected Message received: " + msg.toString());
//				}
//
//				break;
//			// case Request.REQUEST_EVENT:
//			// switch (msg.arg1) {
//			// case Request.REQUEST_SENT:
//			// if (executing)
//			// executeNext(msg.arg2);
//			// break;
//			// }
//			// break;
//			case MessageTypes.CONNECTION_MESSAGE_RECEIVED:
//				parseData(msg);
//				break;
//			case MessageTypes.CONNECTION_STATE_CHANGED:
//				if (msg.arg1 == MessageTypes.CONNECTION_STATE_CONNECTED) {
//					requestSensorData();
//				}
//
//				// Should fall trough
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
