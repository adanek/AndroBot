package at.uibk.informatik.androbot.data;

import org.opencv.core.Scalar;

public class ColorRange {
	
	int hmin;
	int hmax;
	int smin;
	int smax;
	int vmin;
	int vmax;
	
	public ColorRange(){}
	
	public ColorRange(int hmin, int hmax, int smin, int smax, int vmin, int vmax) {
		super();
		this.hmin = hmin;
		this.hmax = hmax;
		this.smin = smin;
		this.smax = smax;
		this.vmin = vmin;
		this.vmax = vmax;
	}

	public int getHmin() {
		return hmin;
	}

	public void setHmin(int hmin) {
		this.hmin = hmin;
	}

	public int getHmax() {
		return hmax;
	}

	public void setHmax(int hmax) {
		this.hmax = hmax;
	}

	public int getSmin() {
		return smin;
	}

	public void setSmin(int smin) {
		this.smin = smin;
	}

	public int getSmax() {
		return smax;
	}

	public void setSmax(int smax) {
		this.smax = smax;
	}

	public int getVmin() {
		return vmin;
	}

	public void setVmin(int vmin) {
		this.vmin = vmin;
	}

	public int getVmax() {
		return vmax;
	}

	public void setVmax(int vmax) {
		this.vmax = vmax;
	}
	
	public Scalar getLower(){
		return new Scalar(hmin, smin, vmin);
	}
	
	public Scalar getUpper(){
		return new Scalar(hmax, smax, vmax);
	}	

}
