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
		this.conn.write(new byte[] { 'k', distance_cm, '\r', '\n' });

	}

	@Override
	public void turn(Direction direction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveForward() {
		this.conn.write(new byte[] { 'w', '\r', '\n' });
		
	}

	@Override
	public void moveBackward() {
		this.conn.write(new byte[] { 'x', '\r', '\n' });
		
	}

	@Override
	public void stop() {
		this.conn.write(new byte[] { 's', '\r', '\n' });

	}
	
	@Override
	public void turnLeft() {
		this.conn.write(new byte[] { 'a', '\r', '\n' });

	}
	
	@Override
	public void turnRight() {
		this.conn.write(new byte[] { 'd', '\r', '\n' });

	}
	
	@Override
	public void barLower() {
		this.conn.write(new byte[] { '-', '\r', '\n' });

	}
	
	@Override
	public void barRise() {
		this.conn.write(new byte[] { '+', '\r', '\n' });

	}
	
	
	@Override
	public void setBar(byte value) {
		this.conn.write(new byte[] { 'o', value, '\r', '\n' });

	}
	
	@Override
	public void turn(byte degree) {
		this.conn.write(new byte[] { 'l', degree, '\r', '\n' });
		
	}
	
	@Override
	public void setVelocity(byte left, byte right) {
		this.conn.write(new byte[] { 'i', left, right, '\r', '\n' });
		
	}

	@Override
	public void setLeds(byte red, byte blue) {
		this.conn.write(new byte[] { 'u', red, blue, '\r', '\n' });
		
	}
	
	@Override
	public String getOdomentry() {
		// Send command
		this.conn.write(new byte[] { 'h', '\r', '\n' });
		// TODO read results from robot
		return null;
	}
	
	@Override
	public void setOdomentry(byte xlow, byte xheigh, byte ylow, byte
			yheigh, byte alphalow, byte alphaheigh) {
		
		// Test if byte is big enough for the input data
		
		// Send command
		this.conn.write(new byte[] { 'j', xlow, xheigh, ylow, yheigh, alphalow, alphaheigh, '\r', '\n' });
		
	}
	
	@Override
	public String getSensors() {
		// Send command
		this.conn.write(new byte[] { 'q', '\r', '\n' });
		// TODO read results from robot
		return null;
	}
	
	

}
