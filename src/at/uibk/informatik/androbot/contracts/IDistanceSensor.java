package at.uibk.informatik.androbot.contracts;

public interface IDistanceSensor {

	/**
	 * Returns the name of the sensor
	 * @return
	 */
	String getName();
	
	/**
	 * Returns the current value of the sensor
	 * 
	 * Valid range: 10cm - 80cm
	 * If the sensor is closer than 10cm to an obstacle, the value is set to 0
	 * If the sensor does not detect an obstacle the value is set to 99.
	 */
	int getCurrentDistance();
}
