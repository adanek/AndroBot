package at.uibk.informatik.androbot.data;

import org.opencv.core.Point;

import at.uibk.informatik.androbot.enums.Colors;

public class Element implements Comparable {

	private Colors col;
	private Point p;
	
	//constructor
	public Element(Colors col, Point p){
		
		this.col = col;
		this.p   = p;
		
	}

	public Colors getCol() {
		return col;
	}

	public void setCol(Colors col) {
		this.col = col;
	}

	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}
	
	@Override
	public String toString() {
		return String.format("%s x:%f y:%f\n", col.toString(), p.x, p.y);		
	}

	@Override
	public int compareTo(Object arg0) {

		Element other = (Element) arg0;
		return (int) Math.round( this.p.x - other.p.x);		
	}
	
}
