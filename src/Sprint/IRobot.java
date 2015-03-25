package Sprint;

import java.util.List;

import android.os.Handler;

public interface IRobot {
	
	/**
	 * Connect the application with the robot
	 */
	void connect();
	
	/**
	 * Disconnects the application with the robot
	 */
	void disconnect();
	
	boolean isConnected();
	
	/**
	 * Sets the default values of the robot
	 */
	void initialize();
	
	void setLinearCorrection(double newValue);
	double getLinearCorrection();
	
	void setAngularCorrection(double newValue);
	double getAngularCorrection();
	
	
	
	/**
	 * Moves the robot forward until it receives the next command
	 */
	void moveForward();
	
	/**
	 * Moves the robot backward until it receives the next command
	 */
	void moveBackward();
	
	/**
	 * Moves the robot forward by the given distance. The move cancels if the
	 * robot receives the next command
	 * 
	 * @param distance_cm
	 *            The distance which the robot should move in centimeter
	 */
	void moveDistance(int distance_cm);
	
	/**
	 * Sets the given velocity to the corresponding wheel
	 * @param left the velocity of the left wheel
	 * @param right the velocity of the right wheel
	 */
	void setVelocity(byte left, byte right);
	
	/**
	 * Stops the current movement of the robot
	 */
	void stop();

	/**
	 * Turns the robot in the given direction by the given degrees
	 * 
	 * @param direction
	 *            The direction in which the robot should move
	 * @param degrees
	 *            The amount of degrees the robot should turn
	 */
	void turn(Direction direction, int degrees);
	
	/**
	 * Turns the robot to the left side by 90 degrees
	 */
	void turnLeft();
	
	/**
	 * Turns the robot to the right side by 90 degrees
	 */
	void turnRight();

	
	
	
	/**
	 * Sets the bar to the given angle
	 * 
	 * The lowest possible position is 0 degrees. The highest possible position
	 * is 90 degrees
	 * 
	 * @param degrees
	 *            the new angle of the bar
	 * @throws IllegalArgumentException
	 *             if the argument is not in the range form 0 - 90
	 */
	void setBar(int degrees);
	
	/**
	 * Sets the bar to the lowest possible angle
	 */
	void barLower();
	
	/**
	 * Sets the bar to the highest possible angle
	 */
	void barRise();
	
	
	
	
	/**
	 * Gets an array of IDistancesSensor representing the distance sensors of the robot.
	 * Each sensor has a name and a particular value.
	 * 
	 * Values between 10 - 80 can be interpreted as distance in cm to the detected obstacle
	 * Value 0 can be interpreted as too close to the obstacle for measurement
	 * Value 99 can be interpreted as no obstacle detected
	 * 
	 * @return Array of distance sensors
	 */
	List<IDistanceSensor> getSensors();
	
	
	
	
	/**
	 * Sets the values of the the corresponding LED
	 * @param red the brightness of the red led
	 * @param blue the brightness of the blue led
	 */
	void setLeds(byte red, byte blue);
	
		
	/**
	 * 
	 * @return
	 */
	String getOdomentry();
	void setOdomentry(byte xlow, byte xheigh, byte ylow, byte yheigh,
			byte alphalow, byte alphaheigh);


	
}
