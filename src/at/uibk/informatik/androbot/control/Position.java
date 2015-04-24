package at.uibk.informatik.androbot.control;

public class Position {

	int x;
	int y;
	int th;
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getTh() {
		return th;
	}
	public void setTh(int th) {
		this.th = th;
	}
	
	public Position(int x, int y, int th) {
		super();
		this.x = x;
		this.y = y;
		this.th = th;
	}
	
	public Position(){
		this(0,0,0);
	}
	
	@Override
	public boolean equals(Object o) {	
		
		Position other = (Position) o;
		
		int dX = Math.abs(this.getX() - other.getX());
		int dY = Math.abs(this.getY() - other.getY());
		int tol = 3;
		
		if(dX < tol && dY < tol){
			return true;
		}
		
		return false;
	}
}
