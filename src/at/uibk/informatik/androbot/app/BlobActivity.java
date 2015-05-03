package at.uibk.informatik.androbot.app;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.programms.BlobDetection;

public class BlobActivity extends Activity{

	private static final String LOG_TAG = "BlobActivity";
	private static Scalar color =  new Scalar(255.0, 255.0, 220.0, 0.0); 
	private static Mat homoMat;
	public static Point ball = null;
	public static boolean running = false;
	
	private BlobDetection prog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blob);
		
		prog = new BlobDetection(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		RadioButton red = (RadioButton) findViewById(R.id.radioRed);
		RadioButton green = (RadioButton) findViewById(R.id.radioGreen);
		RadioGroup gr = (RadioGroup) findViewById(R.id.colorGroup);
		
		//check red
		if(color.equals( new Scalar(255.0, 255.0, 220.0, 0.0) )){
			gr.check(red.getId());
		//check green
		}else{
			gr.check(green.getId());
		}
		
		if(running){
			onRunning();
		}
		
	}

	private void onRunning() {
		if(ball == null){
			Log.d(LOG_TAG, "Robot turn");
			
			prog.turn(45);			
			searchBall();
			return;
		} else{
			Log.d(LOG_TAG, "Robot dirve to ball");
			
			int x = (int) Math.round(ball.x);
			int y = (int) Math.round(ball.y);
			
			// Catch the ball
			int ang = prog.getAngle(new Position(), new Position(x, y, 0));
			prog.turn(ang);
			
			int dis = prog.getDistanceToTarget(new Position(), new Position(x, y, 0)) -15; // 15 cm  vor dem Ball stehen bleiben.
			prog.moveDistance(dis);	
			
			prog.getRobot().lowerBar();
			
			// Fahre zu der Ablieferposition
			
			ang = prog.getAngle(prog.getCurrent(), prog.target);
			prog.turn(ang);
			
			dis = prog.getDistanceToTarget(prog.getCurrent(), prog.target);
			prog.moveDistance(dis);		
			
			prog.getRobot().raiseBar();
			
			Position tar = new Position();
			
			ang = prog.getAngle(prog.getCurrent(),tar);
			prog.turn(ang);
			
			dis = prog.getDistanceToTarget(prog.getCurrent(), tar);
			prog.moveDistance(dis);	
			
			// Turn to x
			if (prog.getCurrent().getTh() != 0){
				prog.turn(prog.getCurrent().getTh() * -1);
			}			
		}
		
		running  = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}
	
	//on start
	public void onStart(View v){
		
		searchBall();
	}

	private void searchBall() {
		//if homography matrix is available
		if(homoMat != null && homoMat.empty() == false){
			//set matrix in color blob detection activty
			ColorBlobDetectionActivity.homoMat = homoMat;
			ColorBlobDetectionActivity.color = color;
			
			//call homography activity
			
			running = true;
			Intent colorblob = new Intent(this, ColorBlobDetectionActivity.class);
			startActivity(colorblob);			
		}
	}
	
	//on color toggle
	public void onRadioButtonToggle(View v){
		
		//mBlobColorHsv = new Scalar(105.0, 255.0, 130.0, 0.0); //now green red: 255 255 220 0
		
		Log.d(LOG_TAG, "changed");
		
		RadioButton red = (RadioButton) findViewById(R.id.radioRed);
		
		//red ball
		if(red.isChecked()){
			color = new Scalar(255.0, 255.0, 220.0, 0.0); 
		//green ball
		}else {
			color = new Scalar(105.0, 255.0, 130.0, 0.0); 
		}
		
	}
	
	//call homography activity
	public void onHomography(View v){
		
		//reset homoMat
		if(homoMat != null){
		
			homoMat.release();
		}
		
		//call homography activity
		Intent homo = new Intent(this, GetHomographyActivity.class);
		startActivity(homo);
		
	}

	//get homography matrix
	public static Mat getHomoMat() {
		return homoMat;
	}

	//set homography matrix
	public static void setHomoMat(Mat newMat) {
		homoMat = newMat;
	}

	//return color scalar
	public static Scalar getColor() {
		return color;
	}

	//set color scalar
	public static void setColor(Scalar color) {
		BlobActivity.color = color;
	}

}
