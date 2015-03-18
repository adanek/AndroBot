package at.uibk.informatik.androbot.programms;

import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.control.Robot;

public abstract class Programm {
	
	
	private IRobot robot;
	public Programm(){
		this.robot = new Robot(null);
		
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

	public void setRobot(IRobot robot) {
		this.robot = robot;
	}

}
