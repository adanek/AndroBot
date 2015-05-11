package at.uibk.informatik.androbot.app;

import java.util.List;

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
import at.uibk.informatik.androbot.programms.BeaconDetection;
import at.uibk.informatik.androbot.programms.BlobDetection;
import at.uibk.informatik.androbot.data.Element;

public class BeaconDetectionActivity extends Activity{

	private static final String LOG_TAG = "BeaconDetectionActivity";

	private BeaconDetection prog;
	private static Mat homoMat;
	public static Scalar red = new Scalar(252.0,225.0,216.0,0.0);
	public static Scalar blue = new Scalar(149.0,188.0,100.0,0.0);
	public static Scalar yellow = new Scalar(35.0,200.0,180.0,0.0);
	public static Scalar white = new Scalar(134.0,209.0,215.0,0.0);
	public static Position current;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon);
		
		prog = new BeaconDetection(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	//on start
	public void onStart(View v){
		
		//check if homography matrix is set
//		if(homoMat == null || homoMat.empty() == true){
//			Log.d(LOG_TAG, "Homography matrix not set");
//			return;
//		}
		
		Log.d(LOG_TAG, "Beacon Detection started");
		
		//log color values
		Log.d(LOG_TAG, "Red: " + red.val[0] + " " + red.val[1] + " " + red.val[2]);
		Log.d(LOG_TAG, "Blue: " + blue.val[0] + " " + blue.val[1] + " " + blue.val[2]);
		Log.d(LOG_TAG, "Yellow: " + yellow.val[0] + " " + yellow.val[1] + " " + yellow.val[2]);
		Log.d(LOG_TAG, "White: " + white.val[0] + " " + white.val[1] + " " + white.val[2]);
		
		//call self localization activity
		Intent sl = new Intent(this, SelfLocalizationActivity.class);
		startActivity(sl);
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
	
	public void onGetColor(View v){
		
		//call color detection activity
		Intent color = new Intent(this, GetColorActivity.class);
		startActivity(color);
	}
	
	//get homography matrix
	public static Mat getHomoMat() {
		return homoMat;
	}

	//set homography matrix
	public static void setHomoMat(Mat newMat) {
		homoMat = newMat;
	}

}
