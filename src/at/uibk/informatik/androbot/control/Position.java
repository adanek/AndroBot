package at.uibk.informatik.androbot.control;

import at.uibk.informatik.androbot.contracts.IPosition;

public class Position implements IPosition {

	private int x;
	private int y;
	private int alpha;

	public Position(int x, int y, int alpha) {
		this.x = x;
		this.y = y;
		this.alpha = alpha;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getOrientation() {
		// TODO Auto-generated method stub
		return alpha;
	}

	@Override
	public String toString() {
		return String.format("x=%04X\ty=%04X\talpha=%04X", this.x, this.y, this.alpha);
	}

	public static IPosition parse(String data) {
				
		// xlow, xheigh, ylow, yheigh, alphalow, alphaheigh
		// odometry: 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x
		String[] fields = data.split(" ");
		
		if(fields.length != 7)
			return null;

		int x = Integer.decode(fields[1]) + (Integer.decode(fields[2]) << 8);
		int y = Integer.decode(fields[3]) + (Integer.decode(fields[4]) << 8);
		int a = Integer.decode(fields[5]) + (Integer.decode(fields[6]) << 8);

		return new Position(x, y, a);
	}
	
	public static IPosition RootPosition(){
		
		return new Position(0, 0, 0);
	}

}
