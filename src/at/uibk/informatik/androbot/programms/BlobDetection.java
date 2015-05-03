package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.control.Position;

public class BlobDetection extends ProgrammBase {

	private static final String LOG_TAG = "Blob detection";	
	private Position current = new Position();
	public Position target = new Position(100, 100, 0);
	
	public BlobDetection(Context context) {
		super(context);
	}	

	@Override
	protected void onExecute() {

		//Log
		Log.d(LOG_TAG, "Blob detection started");
	}
	
	public void turn(int degrees){
		super.turn(degrees);
		getCurrent().setTh(getCurrent().getTh() + degrees);		
	}
	
	public void moveDistance(int dis){
		super.moveDistance(dis);
		setPosition(dis);		
	}

	public void setPosition(int distance) {

		double th = getCurrent().getTh();
		double w = 90;

		double a = getCurrent().getX();
		double b = getCurrent().getY();
		double c = distance;

		if (th > 0 && th <= 90) {
			a += Math.cos(Math.toRadians(th)) * c;
			b += Math.sin(Math.toRadians(th)) * c;
		} else if (th > 90 && th <= 180) {
			w = w - (th - 90);
			a += Math.cos(Math.toRadians(w)) * c * -1;
			b += Math.sin(Math.toRadians(w)) * c;
		} else if (th < 0 && th >= -90) {
			w = th * -1;
			a += Math.cos(Math.toRadians(w)) * c;
			b += Math.sin(Math.toRadians(w)) * c * -1;
		} else {
			w = w - ((th * -1) - 90);
			a += Math.cos(Math.toRadians(w)) * c * -1;
			b += Math.sin(Math.toRadians(w)) * c * -1;
		}

		getCurrent().setX((int) Math.round(a));
		getCurrent().setY((int) Math.round(b));

		Log.d(LOG_TAG, String.format("RED COW x: %d y: %d, th: %d", getCurrent().getX(), getCurrent().getY(), getCurrent().getTh()));
	}

	public Position getCurrent() {
		return current;
	}

	public void setCurrent(Position current) {
		this.current = current;
	}

	
}
