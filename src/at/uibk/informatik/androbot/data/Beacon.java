package at.uibk.informatik.androbot.data;

import at.uibk.informatik.androbot.enums.Colors;

public class Beacon {

	private int X;
	private int Y;
	private Colors upper;
	private Colors lower;
	
	//constructor
	public Beacon(int x, int y, Colors upper, Colors lower){
		this.X = x;
		this.Y = y;
		this.upper = upper;
		this.lower = lower;
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
	
}
