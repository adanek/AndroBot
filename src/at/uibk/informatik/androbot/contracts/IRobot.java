package at.uibk.informatik.androbot.contracts;

public interface IRobot {
	
	void connect();
	void disconnect();
	
	void move();
	void move(byte distance_cm);
	void turn(Direction direction);
	void turn(Direction direction,byte degree);
	void stop();
	void setBar(byte value);
}
