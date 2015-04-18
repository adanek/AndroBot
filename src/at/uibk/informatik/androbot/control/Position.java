package at.uibk.informatik.androbot.control;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;
import at.uibk.informatik.androbot.contracts.IPosition;

public class Position implements IPosition {

	private static final String LOG_TAG = "PositionParser";
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
	
		// Ordering of the values according to robot.c
		// xlow, xheigh, ylow, yheigh, alphalow, alphaheigh
		// low byte first -> ByteOrder LITTLE_ENDIAN
		
		// Expected String for position 100 200 300:
		// "odometry: 0x64 0x00 0xC8 0x00 0x2C 0x01"
		String[] fields = data.split(" ");
		
		// Check if the data is valid
		if(fields.length != 7){
			Log.w(LOG_TAG, String.format("Invalid data received: %s", data));
			return null;
		}		
		
		// Extract bytes from string
		byte[] bytes = new byte[fields.length - 1];
		for(int i = 1; i < fields.length; i++){
			bytes[i-1] = Integer.decode(fields[i]).byteValue();
		}
		
		// Rebuild values
		ByteBuffer buf = ByteBuffer.allocate(bytes.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.put(bytes);
		
		int x = buf.getShort(0);
		int y = buf.getShort(2);
		int a = buf.getShort(4);
		
		return new Position(x, y, a);
	}
	
	public static IPosition RootPosition(){
		
		return new Position(0, 0, 0);
	}

}
