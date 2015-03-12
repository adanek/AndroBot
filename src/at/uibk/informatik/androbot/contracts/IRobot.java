package at.uibk.informatik.androbot.contracts;

public interface IRobot {
	
	void connect();
	void disconnect();
	
	void moveForward();
	void moveBackward();
	void move(byte distance_cm);
	void turn(Direction direction);
	void turn(byte degree);
	void stop();
	void setBar(byte value);
	String getOdomentry();
	//void execute(Queue)
}
