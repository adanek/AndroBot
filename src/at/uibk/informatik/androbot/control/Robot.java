package at.uibk.informatik.androbot.control;

import java.io.IOException;

import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IRobot;

public class Robot implements IRobot {

	private IConnection conn;
	
	public Robot(IConnection connection){
		this.conn = connection;		
	}
	
	@Override
	public void connect() {
		try {
			this.conn.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		this.conn.disconnect();
	}

	
	@Override
	public void moveDistance(byte distance_cm) {
		
		
		//odomentry stuff
		byte dis = (byte)(distance_cm * 1.43);
		this.conn.sendCommand(new byte[] { 'k', dis, '\r', '\n' });
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void turn(Direction direction) {
		switch (direction){
		case LEFT:
			this.turnLeft();
			break;
		case RIGHT:
			this.turnRight();
			break;
		default:
			break;		
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void moveForward() {
		this.conn.sendCommand(new byte[] { 'w', '\r', '\n' });
		
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
	public void turnLeft() {
		this.conn.sendCommand(new byte[] { 'a', '\r', '\n' });
		
	}
	
	@Override
	public void turnRight() {
		this.conn.sendCommand(new byte[] { 'd', '\r', '\n' });

	}
	
	@Override
	public void barLower() {
		this.conn.sendCommand(new byte[] { '-', '\r', '\n' });

	}
	
	@Override
	public void barRise() {
		this.conn.sendCommand(new byte[] { '+', '\r', '\n' });

	}
	
	
	@Override
	public void setBar(byte value) {
		this.conn.sendCommand(new byte[] { 'o', value, '\r', '\n' });

	}
	
	@Override
	public void turn(int degree) {
		byte deg = (byte)(degree * 1.17);
		this.conn.sendCommand(new byte[] { 'l', degree, '\r', '\n' });
		
	}
	
	@Override
	public void setVelocity(byte left, byte right) {
		this.conn.sendCommand(new byte[] { 'i', left, right, '\r', '\n' });
		
	}

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
	public void setOdomentry(byte xlow, byte xheigh, byte ylow, byte
			yheigh, byte alphalow, byte alphaheigh) {
		
		// Test if byte is big enough for the input data
		
		// Send command
		this.conn.sendCommand(new byte[] { 'j', xlow, xheigh, ylow, yheigh, alphalow, alphaheigh, '\r', '\n' });
		
	}
	
	@Override
	public String getSensors() {
		// Send command
		this.conn.sendCommand(new byte[] { 'q', '\r', '\n' });
		// TODO read results from robot
		return null;
	}
	
	

}
