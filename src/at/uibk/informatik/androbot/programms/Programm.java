package at.uibk.informatik.androbot.programms;

import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.BluetoothConnection;
import at.uibk.informatik.androbot.control.Robot;

public abstract class Programm {
	
	private static double angularCorr;
	private static double linearCorr;
	private static String macAddress;
	
	private Robot robot;
	public Programm(){
		this.robot = new Robot(new BluetoothConnection(macAddress));
		
		System.out.println("linear correction " + linearCorr);
		System.out.println("angular correction " + angularCorr);
		System.out.println("Mac Address " + macAddress);
		
		//set correction data
		robot.setAngularCorrection(angularCorr);
		//
		
		
	}
	
	public void start(){
		this.getRobot().connect();
		this.ExecutionPlan();
		this.end();
	}
	
	public abstract void ExecutionPlan();
	
	public void end(){
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
		Programm.angularCorr = angularCorr;
	}

	public static double getLinearCorr() {
		return linearCorr;
	}

	public static void setLinearCorr(double linearCorr) {
		Programm.linearCorr = linearCorr;
	}

	public static String getMacAddress() {
		return macAddress;
	}

	public static void setMacAddress(String macAddress) {
		Programm.macAddress = macAddress;
	}
	
}
