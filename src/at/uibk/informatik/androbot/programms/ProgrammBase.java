package at.uibk.informatik.androbot.programms;

import at.uibk.informatik.androbot.app.SettingsActivity;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;

public abstract class ProgrammBase {

	private static double angularCorr;
	private static double linearCorr;
	private IRobot robot;

	public ProgrammBase() {
		this.robot = new Robot(new BluetoothConnection(SettingsActivity.MacAddress));

		// set correction data
		robot.setAngularCorrection(SettingsActivity.AngularCorrecion);
		robot.setLinearCorrection(SettingsActivity.LinearCorrection);

	}

	public void start() {
		this.connect();
		this.execute();
		this.end();
	}

	public void connect() {
		this.robot.connect();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void disconnect() {

		this.end();
	}

	protected abstract void execute();

	public void end() {
		this.robot.stop();
		this.getRobot().disconnect();
	}

	public IRobot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public static double getAngularCorr() {
		return angularCorr;
	}

	public static void setAngularCorr(double angularCorr) {
		ProgrammBase.angularCorr = angularCorr;
	}

	public static double getLinearCorr() {
		return linearCorr;
	}

	public static void setLinearCorr(double linearCorr) {
		ProgrammBase.linearCorr = linearCorr;
	}

}
