package Sprint;

public class Position implements IPosition {

	private byte x;
	private byte y;
	private byte alpha;

	public Position(byte x, byte y, byte alpha) {
		this.x = x;
		this.y = y;
		this.alpha = alpha;
	}
	
	@Override
	public byte getX() {
		return x;
	}

	@Override
	public byte getY() {
		return y;
	}

	@Override
	public byte getOrientation() {
		// TODO Auto-generated method stub
		return alpha;
	}
	
	public static IPosition parse(String data){
		// xlow,  xheigh,  ylow,  yheigh,  alphalow,  alphaheigh
		// odometry: 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x
		
		String[] fields = data.split(" ");		
		return new Position(Byte.decode(fields[1]), Byte.decode(fields[3]), Byte.decode(fields[5]));
	}

}
