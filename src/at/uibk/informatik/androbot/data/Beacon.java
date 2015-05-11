package at.uibk.informatik.androbot.data;

import org.opencv.core.Point;

import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.enums.Colors;

public class Beacon implements Comparable {

	private int id;
	private int X;
	private int Y;
	private Colors upper;
	private Colors lower;
	private Point pos;
	
	//constructor
	public Beacon(int x, int y, Colors upper, Colors lower, int id){
		this.X = x;
		this.Y = y;
		this.upper = upper;
		this.lower = lower;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public int getX() {
		return X;
	}
	public void setX(int x) {
		X = x;
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}
	public Colors getUpper() {
		return upper;
	}
	public void setUpper(Colors upper) {
		this.upper = upper;
	}
	public Colors getLower() {
		return lower;
	}
	public void setLower(Colors lower) {
		this.lower = lower;
	}
	
	public Position getPosition(){
		
		return new Position(X,Y,0);
		
	}

	@Override
	public int compareTo(Object arg0) {
		Beacon other = (Beacon) arg0;
		return other.id - this.id;
	}
	
}
