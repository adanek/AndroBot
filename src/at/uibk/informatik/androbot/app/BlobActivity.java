package at.uibk.informatik.androbot.app;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
	
	private static int X  = 100;
	private static int Y  = 100;
	private static int TH = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blob);
		
		prog = new BlobDetection(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//get screen elements
		EditText x  = (EditText) findViewById(R.id.inputX);
		EditText y  = (EditText) findViewById(R.id.inputY);
		EditText th = (EditText) findViewById(R.id.inputTH);
		
		//set texts on screen
		x.setText(String.valueOf(X));
		y.setText(String.valueOf(Y));
		th.setText(String.valueOf(TH));
		
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
			Log.d(LOG_TAG, "Robot drive to ball");
			
			//create target position
			Position target = new Position(X,Y,TH);			
			prog.target = target;
			
			int x = (int) Math.round(ball.x);
			int y = (int) Math.round(ball.y);
			
			Log.d(LOG_TAG, String.format("Ball: %d %d", x, y));
			Log.d(LOG_TAG, String.format("Target: %d %d %d", prog.target.getX(),prog.target.getY(),prog.target.getTh()));
			
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
			
			// Catch the ball
			int ang = prog.getAngle(new Position(), new Position(x, y, 0));
			prog.turn(ang);
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
			
			int dis = prog.getDistanceToTarget(new Position(), new Position(x, y, 0)) -15; // 15 cm  vor dem Ball stehen bleiben.
			prog.moveDistance(dis);	
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
			
			prog.getRobot().lowerBar();
			
			// Fahre zu der Ablieferposition
			
			ang = prog.getAngle(prog.getCurrent(), prog.target);
			prog.turn(ang);
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
			
			dis = prog.getDistanceToTarget(prog.getCurrent(), prog.target);
			prog.moveDistance(dis);		
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
			
			prog.getRobot().raiseBar();
			
			Position tar = new Position();
			
			ang = prog.getAngle(prog.getCurrent(),tar);
			prog.turn(ang);
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
			
			dis = prog.getDistanceToTarget(prog.getCurrent(), tar);
			prog.moveDistance(dis);	
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
			
			// Turn to x
			if (prog.getCurrent().getTh() != 0){
				prog.turn(prog.getCurrent().getTh() * -1);
			}
			Log.d(LOG_TAG, String.format("Position: %d %d %d", prog.getCurrent().getX(), prog.getCurrent().getY(), prog.getCurrent().getTh()));
		}
		
		running  = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}
	
	//on start
	public void onStart(View v){
		
		EditText x  = (EditText) findViewById(R.id.inputX);
		EditText y  = (EditText) findViewById(R.id.inputY);
		EditText th = (EditText) findViewById(R.id.inputTH);
		
		X  = Integer.valueOf(x.getText().toString());
		Y  = Integer.valueOf(y.getText().toString());
		TH = Integer.valueOf(th.getText().toString());	
		
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
