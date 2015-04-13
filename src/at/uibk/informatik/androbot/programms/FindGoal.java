package at.uibk.informatik.androbot.programms;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;

public class FindGoal extends ProgrammBase {

	private static final String LOG_TAG = "FindGoal";
	private double X;
	private double Y;
	private double TH;
	
	public FindGoal(Context context, IRobotResponseCallback listener) {
		super(context, listener);
	}	

	@Override
	protected void onExecute() {

		//Log
		Log.d(LOG_TAG, "Find Goal started");



	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
	}

	public double getTH() {
		return TH;
	}

	public void setTH(double tH) {
		TH = tH;
	}
	
}
