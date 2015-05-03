package at.uibk.informatik.androbot.app;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class BlobActivity extends Activity{

	private static final String LOG_TAG = "BlobActivity";
	private static Scalar color = new Scalar(85, 255, 75, 0.0);
	private static Mat homoMat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blob);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		RadioButton red = (RadioButton) findViewById(R.id.radioRed);
		RadioButton green = (RadioButton) findViewById(R.id.radioGreen);
		RadioGroup gr = (RadioGroup) findViewById(R.id.colorGroup);
		
		//check green
		if(color.equals(new Scalar(85, 255, 75, 0.0))){
			gr.check(green.getId());
		//check red
		}else{
			gr.check(red.getId());
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	
	}
	
	//on start
	public void onStart(View v){
		
		//if homography matrix is available
		if(homoMat.empty() == false){
			//set matrix in color blob detection activty
			ColorBlobDetectionActivity.homoMat = homoMat;
			
			//call homography activity
			Intent colorblob = new Intent(this, ColorBlobDetectionActivity.class);
			startActivity(colorblob);
			
		}

	}
	
	//on color toggle
	public void onRadioButtonToggle(View v){
		
		Log.d(LOG_TAG, "changed");
		
		RadioButton red = (RadioButton) findViewById(R.id.radioRed);
		
		//red ball
		if(red.isChecked()){
			color = new Scalar(0, 255, 200, 0.0); 
		//green ball
		}else {
			color = new Scalar(85, 255, 75, 0.0); 
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
