package at.uibk.informatik.androbot.contracts;

public interface IRobot {
	
	void connect();
	void disconnect();
	
	void moveForward();
	void moveBackward();
	void turn(Direction direction);
	void turn(int degree);
	void stop();
	void setBar(byte value);
	String getOdomentry();
	//void execute(Queue)
	void turnLeft();
	void turnRight();
	void barLower();
	void barRise();
	String getSensors();
	void moveDistance(byte distance_cm);
	void setVelocity(byte left, byte right);
	void setLeds(byte red, byte blue);
	void setOdomentry(byte xlow, byte xheigh, byte ylow, byte yheigh,
			byte alphalow, byte alphaheigh);
}
