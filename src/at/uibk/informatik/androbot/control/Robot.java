package at.uibk.informatik.androbot.control;

import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IRobot;

public class Robot implements IRobot {

	private IConnection conn;
	
	public Robot(IConnection connection){
		this.conn = connection;
		this.conn.connect();
	}
	
	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void move() {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(byte distance_cm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void turn(Direction direction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void turn(Direction direction, byte degree) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBar(byte value) {
		// TODO Auto-generated method stub

	}

}
