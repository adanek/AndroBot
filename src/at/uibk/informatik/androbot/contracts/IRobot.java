package at.uibk.informatik.androbot.contracts;


public interface IRobot {
	
	/**
	 * Message type for callback message Robot response received
	 */
	public static final int ROBOT_RESPONSE_RECEIVED= 200;
	
	/**
	 * Message type for callback message sensor data received
	 */
	public static final int SENSOR_DATA_RECEIVED = 202;
	
	/**
	 * Message type for callback message position received
	 */
    public static final int POSITION_RECEIVED = 203;
    
	/**
	 * Message type for callback message position received
	 */
    public static final int IDLE = 204;
    
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
	
	/**
	 * Sets the correction coefficient for the linear movement
	 * @param newValue The new coefficient
	 */
	void setLinearCorrection(double newValue);
	
	/**
	 * Sets the correction coefficient for turn movements
	 * @param newValue the new coefficient
	 */
	void setAngularCorrection(double newValue);
	
	/**
	 * Sets the runtime for driving 1cm distance
	 * @param linearRuntimePerCentimeter
	 */
	void setLinearRuntimePerCentimeter(double linearRuntimePerCentimeter);
	double getLinearRuntimePerCentimeter();

	/**
	 * Sets the runtime for turning 1 degree
	 * @param angularRuntimePerDegree
	 */
	void setAngularRuntimePerDegree(double angularRuntimePerDegree);
	
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
	void setVelocity(int left, int right);
	

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
	 * Stops the current movement of the robot
	 * @param immediately True if all other requests should be discard
	 */
	void stop(boolean immediately);	
	
	
	/**
	 * Sets the bar to the given angle
	 * 
	 * The lowest possible position is 0 degrees. The highest possible position
	 * is 90 degrees
	 * 
	 * @param degrees
	 *            the new angle of the bar
	 */
	void setBar(int position);
	
	/**
	 * Sets the bar to the lowest possible angle
	 */
	void barLower();
	
	/**
	 * Sets the bar to the highest possible angle
	 */
	void barRise();
	
	/**
	 * Sets the values of the the corresponding LED
	 * @param red the brightness of the red led
	 * @param blue the brightness of the blue led
	 */
	void setLeds(byte red, byte blue);
	
	
	/**
	 * Requests the remote device to sent its current sensor data.
	 * The robot sends a message with the received data to the callback handler
	 * of the caller
	 * 
	 * Message.what = CONSTANTS.DATA_EVENT
	 * Message.arg1 = CONSTANTS.SENSOR_DATA_RECEIVED
	 * Message.object = List<IDistanceSensor>
	 * 
	 * Each sensor has a name and a particular value. 
	 * Values between 10 - 80 can be interpreted as distance in cm to the detected obstacle
	 * Value 0 can be interpreted as too close to the obstacle for measurement
	 * Value 99 can be interpreted as no obstacle detected
	 * 
	 * @param immediately Should the request sent immediately or added to the queue
	 */
	void requestSensorData(boolean immediately);	
	
	
	/**
	 * Requests the remote device to sent its current sensor data.
	 * The robot sends a message with the received data to the callback handler
	 * of the caller
	 * 
	 * Message.what = CONSTANTS.DATA_EVENT
	 * Message.arg1 = CONSTANTS.SENSOR_DATA_RECEIVED
	 * Message.object = List<IDistanceSensor>
	 * 
	 * Each sensor has a name and a particular value. 
	 * Values between 10 - 80 can be interpreted as distance in cm to the detected obstacle
	 * Value 0 can be interpreted as too close to the obstacle for measurement
	 * Value 99 can be interpreted as no obstacle detected
	 * 
	 * Calls requestSensorData(true);
	 */
	void requestSensorData();
	
	/**
	 * Requests the current odomentry data from the remote device
	 * The robot sents a message with the received data to the callback handler
	 * of the device with the following parameters:
	 * 
	 * Message.what = CONSTANTS_DATA_EVENT
	 * Message.arg1 = CONSTANTS_POSITION_RECEIVED
	 * Messgae.object = IPosition
	 */
	void requestCurrentPosition();
		
	/**
	 * Sets the current position of the remote device to the given values
	 * @param position the values to set
	 */
	void setOdomentry(IPosition position);

	void requestCurrentPosition(boolean immediately);
}
