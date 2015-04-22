package at.uibk.informatik.androbot.programms;

import java.util.List;

import android.content.Context;
import android.graphics.Path.Direction;
import android.util.Log;
import at.uibk.informatik.androbot.control.Robot;

public class Settings extends ProgrammBase {

	public Settings(Context context) {
		super(context);
	}

	private static final String LOG_TAG = "Settings";

	private int distance;
	private int degrees;
	
	public int test;

	@Override
	protected void onExecute() {				

	}
	
	public void runLinearCorrectionTest(int distance){
		Log.d(LOG_TAG, "performing " + distance + " distance test");
		//getRobot().moveDistance(distance);
	}
	
	public void setLinearCorrectionValue(float newValue){
		getRobot().setLinearCorrection(newValue);
	}
	
	public void runLinearRuntimeTest(int distance){		
		//getRobot().moveDistance(distance);
		Log.d(LOG_TAG, "Linear runtime test started");
	}
	
	public void stopLinearRuntimeTest(){
		getRobot().stop(true);
		Log.d(LOG_TAG, "Linear runtime test finished");
	}
	
	public void setLinearRuntime(float runtime){
		getRobot().setLinearRuntimePerCentimeter(runtime);
	}
	
	public void runAngularCorrectionTest(int factor){

	}
	
	//get distance
	public int getDistance() {
		return distance;
	}

	//set distance
	public void setDistance(int distance) {
		this.distance = distance;
	}

	//get degrees
	public int getDegrees() {
		return degrees;
	}

	//set degrees
	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}
	
	public void runAngularRuntimeTest(){
		//getRobot().turn(100);
	}
	
	public void setAngularCorrectionValue(float newValue){
		getRobot().setAngularCorrection(newValue);
	}
	
	public void setAngularRuntime(float runtime){
		getRobot().setAngularRuntimePerDegree(runtime);
	}	
}
