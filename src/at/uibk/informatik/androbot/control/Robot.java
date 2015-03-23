package at.uibk.informatik.androbot.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IRobot;

public class Robot implements IRobot {

	private static final String LOG_TAG = "Robot";
	private IConnection conn;
	private double angularCorrection;
	private double linearCorrection;
	private int barCurrentAngel;
	private final int barMaxAngle = 90;
	private final int barStepSize = 10;

	/* ********************************************************************************** *
	 * Constructor
	 * **************************************************************
	 * ******************** *
	 */

	public Robot(IConnection connection) {
		this.conn = connection;

		this.angularCorrection = 1.0;
		this.linearCorrection = 1.0;
	}

	/* ********************************************************************************** *
	 * Properties
	 * ***************************************************************
	 * ******************* *
	 */

	@Override
	public double getAngularCorrection() {
		return angularCorrection;
	}

	@Override
	public void setAngularCorrection(double angularCorrection) {
		this.angularCorrection = angularCorrection;
	}
	
	@Override
	public double getLinearCorrection(){
		return this.linearCorrection;		
	}
	
	@Override
	public void setLinearCorrection(double newValue){
		this.linearCorrection = newValue;
	}

	/* ********************************************************************************** *
	 * Methods
	 * ******************************************************************
	 * **************** *
	 */

	/*
	 * Connection
	 */


	@Override
	public void connect() {
		try {
			this.conn.connect();
			//this.initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void disconnect() {
		this.conn.disconnect();
	}
	
	public synchronized boolean isConnected(){
		return this.conn.isConnected();
	}


	@Override
	public void initialize() {

		this.setBar(90);
	}

	/*
	 * Movement
	 */


	@Override
	public void moveForward() {
		this.conn.sendCommand(new byte[] { 'w', '\r', '\n' });

	}


	@Override
	public void moveDistance(byte distance_cm) {

		int distance = (int) (distance_cm * linearCorrection);

		try {
			while (distance > 0) {
				byte stepWidth = (byte) (distance > Byte.MAX_VALUE ? Byte.MAX_VALUE
						: distance);
				distance -= stepWidth;

				// Calculate runtime in milliseconds
				int runtime = 2000;

				// Send partial command
				this.conn
						.sendCommand(new byte[] { 'k', stepWidth, '\r', '\n' });
				Thread.sleep(runtime);
			}

		} catch (InterruptedException e) {
			Log.d(LOG_TAG, "Move distance has been interupt");
			e.printStackTrace();
		}
	}


	@Override
	public void moveBackward() {
		this.conn.sendCommand(new byte[] { 'x', '\r', '\n' });
	}

	@Override
	public void stop() {
		this.conn.sendCommand(new byte[] { 's', '\r', '\n' });
	}


	@Override
	public void turn(Direction direction, int degrees) {

		int deg = (int) (degrees * angularCorrection);
		int maxDegreePerStep = 127;

		try {
			while (deg > 0) {

				int stepWidth = deg > maxDegreePerStep ? maxDegreePerStep : deg;
				byte stepData = getStepDataForTurn(direction, stepWidth);
				deg -= stepWidth;

				// Calculate runtime for turning
				int runtime = 1000;

				this.conn.sendCommand(new byte[] { 'l', stepData, '\r', '\n' });
				Thread.sleep(runtime);
			}
		} catch (InterruptedException e) {
			Log.d(LOG_TAG, "Turn command has been interupt");
			e.printStackTrace();
		}
	}


	@Override
	public void turnLeft() {
		this.turn(Direction.LEFT, 90);
	}


	@Override
	public void turnRight() {
		this.turn(Direction.RIGHT, 90);
	}

	// Calculate the accurate parameter for turning the desired degrees in the
	// desired direction
	private byte getStepDataForTurn(Direction direction, int stepWidth) {
		int data = 0;

		switch (direction) {
		case LEFT:
			data = stepWidth;
			break;

		case RIGHT:
			data = Byte.MAX_VALUE - stepWidth + 1; // plus 1 because 255 is
													// already a turn by 1
													// degree
			break;

		default:
			break;
		}
		return (byte) data;
	}

	
	
	
	@Override
	public void setBar(int degrees) {

		if (degrees < 0 || degrees > barMaxAngle)
			throw new IllegalArgumentException(
					"Argument degrees is out of range");

		this.barCurrentAngel = degrees; // Remember the new position of the bar
		byte data = (byte) (degrees * Byte.MAX_VALUE / barMaxAngle);

		this.conn.sendCommand(new byte[] { 'o', data, '\r', '\n' });
	}

	@Override
	public void barLower() {
		if (this.barCurrentAngel > 0) {

			int newValue = barCurrentAngel > barStepSize ? barCurrentAngel
					- barStepSize : 0;
			this.setBar(newValue);
		}
	}

	@Override
	public void barRise() {
		if (this.barCurrentAngel < barMaxAngle) {

			int newValue = (barCurrentAngel + barStepSize) < barMaxAngle ? (barCurrentAngel + barStepSize)
					: barMaxAngle;
			this.setBar(newValue);
		}
	}

	
	
	/*
	 * Sensors
	 */
		
	/*
	 * (non-Javadoc)
	 * @see at.uibk.informatik.androbot.contracts.IRobot#getSensors()
	 */
	@Override
	public List<IDistanceSensor> getSensors() {

		List<IDistanceSensor> sensors = new ArrayList<IDistanceSensor>();
		String[] sensorNames = new String[5];

		sensorNames[0] = "Rear-Left";
		sensorNames[1] = "Front-Left";
		sensorNames[2] = "Front-Middel";
		sensorNames[3] = "Front-Right";
		sensorNames[4] = "Rear-Right";

		String response = this.conn.getResponse(new byte[] { 'q', '\r', '\n' });
		Log.d(LOG_TAG, "Received sensordata: " + response);
		String[] fields = response.split(" ");

		if (fields.length != 11)
			return null;

		int ndx = 0;
		for (int i = 3; i < 8; i++) {
			sensors.add(new DistanceSensor(sensorNames[ndx],
					Integer.decode(fields[i])));
		}

		return sensors;
	}

	
	
	
	
	
	
	
	
	
	
	/*
	 * (non-Javadoc)
	 * @see at.uibk.informatik.androbot.contracts.IRobot#setVelocity(byte, byte)
	 */
	@Override
	public void setVelocity(byte left, byte right) {
		this.conn.sendCommand(new byte[] { 'i', left, right, '\r', '\n' });

	}

	/*
	 * (non-Javadoc)
	 * @see at.uibk.informatik.androbot.contracts.IRobot#setLeds(byte, byte)
	 */
	@Override
	public void setLeds(byte red, byte blue) {
		this.conn.sendCommand(new byte[] { 'u', red, blue, '\r', '\n' });

	}

	@Override
	public String getOdomentry() {
		// Send command
		String response = this.conn.getResponse(new byte[] { 'h', '\r', '\n' });
		return response;
	}

	@Override
	public void setOdomentry(byte xlow, byte xheigh, byte ylow, byte yheigh,
			byte alphalow, byte alphaheigh) {

		// Test if byte is big enough for the input data

		// Send command
		this.conn.sendCommand(new byte[] { 'j', xlow, xheigh, ylow, yheigh,
				alphalow, alphaheigh, '\r', '\n' });
	}

}
