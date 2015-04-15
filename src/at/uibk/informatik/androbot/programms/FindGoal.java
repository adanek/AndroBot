package at.uibk.informatik.androbot.programms;

import java.util.List;
import java.util.Observable;

import android.content.Context;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IDistanceSensor;
import at.uibk.informatik.androbot.contracts.IPosition;
import at.uibk.informatik.androbot.contracts.IRobot;
import at.uibk.informatik.androbot.contracts.IRobotResponseCallback;
import at.uibk.informatik.androbot.control.Position;

public class FindGoal extends ProgrammBase {

	private static final String LOG_TAG = "FindGoal";
	private double X;
	private double Y;
	private double TH;
	private IPosition target;
	private IPosition current;
	private PropertyChangedEvent propChanged;
	
	public FindGoal(Context context, IRobotResponseCallback listener) {
		super(context, listener);
		
		this.propChanged = new PropertyChangedEvent();
		this.current = Position.RootPosition();
		this.target = Position.RootPosition();
	}	

	@Override
	protected void onExecute() {

		//Log
		Log.d(LOG_TAG, "Find Goal started");
		
		IRobot rob = getRobot();
		
		rob.setOdomentry(Position.RootPosition());
		rob.requestCurrentPosition();

	}
	
	public IPosition getTarget(){
		return this.target;
	}
	
	public void setTarget(IPosition target){
		this.target = target;
	}

	public int getDistanceToTarget(){
		
		double x = Math.abs(target.getX()-current.getX());
		double y = Math.abs(target.getY()-current.getY());
		
		double dis = Math.sqrt(Math.pow(x, 2) +Math.pow(y, 2));
		
		return (int) dis;
	}
	
	public int getAngleToTarget(){
		
		double x = Math.abs(target.getX()-current.getX());
		double y = Math.abs(target.getY()-current.getY());
		
		if(x == 0)
			return 0;
		else
		return (int) Math.atan(y / x);		
	}
	
	
	
	public Observable PropertyChanged(){
		return this.propChanged;
		
	}
	
	
	
	@Override
	public void onPositionReceived(IPosition position) {
		
		if(!this.current.equals(position)){
			this.current = position;
			this.propChanged.onChanged();
		}		
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

	@Override
	public void onSensorDataReceived(List<IDistanceSensor> sensors) {
		// TODO Auto-generated method stub
		
	}

	private class PropertyChangedEvent extends Observable{
		
		public PropertyChangedEvent(){
			
		}
		
		public void onChanged(){
			this.setChanged();
			this.notifyObservers();
		}
	}
	
}
