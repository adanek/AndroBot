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
import at.uibk.informatik.androbot.programms.BeaconDetection;
import at.uibk.informatik.androbot.programms.BlobDetection;

public class BeaconDetectionActivity extends Activity{

	private static final String LOG_TAG = "BeaconDetectionActivity";

	private BeaconDetection prog;
	private static Mat homoMat;
	
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
		
		Log.d(LOG_TAG, "Beacon Detection started");
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

}
