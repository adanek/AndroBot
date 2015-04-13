package at.uibk.informatik.androbot.control;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
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

	private void setCurrentDistance(int value) {
		if (value < 10)
			this.distance = 0;
		else if (value <= 80)
			this.distance = value;
		else
			this.distance = 99;
	}

	public static List<IDistanceSensor> parse(String data) {
		
		List<IDistanceSensor> result = new ArrayList<IDistanceSensor>();
		String[] fields = data.split(" ");
		
		if(fields.length != 9)
			return null;

		result.add(new DistanceSensor("Front-Left", Integer.decode(fields[3])));
		result.add(new DistanceSensor("Front-Middle", Integer.decode(fields[6])));
		result.add(new DistanceSensor("Front-Right", Integer.decode(fields[4])));
		result.add(new DistanceSensor("Front-Bumber", Integer.decode(fields[8])));

		return result;

	}

}
