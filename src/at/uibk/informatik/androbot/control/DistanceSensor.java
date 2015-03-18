package at.uibk.informatik.androbot.control;

import at.uibk.informatik.androbot.contracts.IDistanceSensor;

public class DistanceSensor implements IDistanceSensor {

	private String name;
	private int distance;
	
	public DistanceSensor(String name, int value) {
		this.name = name;
		this.setCurrentDistance(value);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getCurrentDistance() {
		return this.distance;
	}
	
	private void setCurrentDistance(int value){
		if(value < 10)
			this.distance = 0;
		else if(value <= 80)
			this.distance = value;
		else 
			this.distance = 99;
	}
}
