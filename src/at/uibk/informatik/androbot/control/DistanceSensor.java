package at.uibk.informatik.androbot.control;

import java.util.ArrayList;
import java.util.List;


public class DistanceSensor {

	private String name;
	private int distance;

	public DistanceSensor(String name, int value) {
		this.name = name;
		this.setCurrentDistance(value);
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getCurrentDistance() {
		return this.distance;
	}

	private void setCurrentDistance(int value) {
		if (value < 10)
			this.distance = 0;
		else if (value <= 80)
			this.distance = value;
		else
			this.distance = 99;
	}

	public static List<DistanceSensor> parse(String data) {
		
		List<DistanceSensor> result = new ArrayList<DistanceSensor>();
		String[] fields = data.split(" ");
		
		if(fields.length != 9)
			return null;

		result.add(new DistanceSensor("Front-Left", Integer.decode(fields[3])));
		//result.add(new DistanceSensor("Front-Middle", Integer.decode(fields[6])));
		result.add(new DistanceSensor("Front-Right", Integer.decode(fields[4])));
	
		return result;
	}

}
