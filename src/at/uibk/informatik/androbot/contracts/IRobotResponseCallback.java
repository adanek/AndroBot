package at.uibk.informatik.androbot.contracts;

import java.util.List;

public interface IRobotResponseCallback {

	void onSensorDataReceived(List<IDistanceSensor> sensors);
	void onPositionReceived(IPosition position);
}
