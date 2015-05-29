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
import android.widget.TextView;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.programms.BeaconDetection;
import at.uibk.informatik.androbot.programms.BlobDetection;
import at.uibk.informatik.androbot.data.ColorRange;
import at.uibk.informatik.androbot.data.Element;

public class BeaconDetectionActivity extends Activity{

	private static final String LOG_TAG = "BeaconDetectionActivity";

	private BeaconDetection prog;
	private static Mat homoMat;
	
	//beacon default colors
	//public static ColorRange red = new ColorRange(116,133,218,255,111,186);
	//public static ColorRange yellow = new ColorRange(78,107,79,158,185,255);
	//public static ColorRange blue = new ColorRange(10,28,255,255,119,255);
	//public static ColorRange white = new ColorRange(154,192,116,170,78,231);
	
	public static ColorRange red = new ColorRange(116,133,193,252,115,255);
	public static ColorRange yellow = new ColorRange(78,107,79,158,185,255);
	public static ColorRange blue = new ColorRange(10,28,255,255,119,255);
	public static ColorRange white = new ColorRange(154,192,116,170,78,231);
	
	//ball default color
	public static ColorRange ballColor = new ColorRange(35,58,207,255,103,197);
	
	public static Position current;
	public static int leftBeaconNo = 0;
	public static int rightBeaconNo = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon);
		
		prog = new BeaconDetection(this);
		
		TextView x = (TextView) findViewById(R.id.txtPositionX);
		TextView y = (TextView) findViewById(R.id.txtPositionY);
		TextView t = (TextView) findViewById(R.id.txtPositionTH);
		
		//initialization
		if(current == null){
			x.setText(Integer.toString(0));
			y.setText(Integer.toString(0));
			t.setText(Integer.toString(0));
		//position is set
		}else{
			x.setText(Integer.toString(current.getX()));
			y.setText(Integer.toString(current.getY()));
			t.setText(Integer.toString(current.getTh()));
		}
		
		TextView lB = (TextView) findViewById(R.id.txtBeacon1);
		TextView rB = (TextView) findViewById(R.id.txtBeacon2);
		
		lB.setText(Integer.toString(leftBeaconNo));
		rB.setText(Integer.toString(rightBeaconNo));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		TextView x = (TextView) findViewById(R.id.txtPositionX);
		TextView y = (TextView) findViewById(R.id.txtPositionY);
		TextView t = (TextView) findViewById(R.id.txtPositionTH);
		
		//initialization
		if(current == null){
			x.setText(Integer.toString(0));
			y.setText(Integer.toString(0));
			t.setText(Integer.toString(0));
		//position is set
		}else{
 			x.setText(Integer.toString(current.getX()));
			y.setText(Integer.toString(current.getY()));
			t.setText(Integer.toString(current.getTh()));
		}
		
		TextView lB = (TextView) findViewById(R.id.txtBeacon1);
		TextView rB = (TextView) findViewById(R.id.txtBeacon2);
		
		lB.setText(Integer.toString(leftBeaconNo));
		rB.setText(Integer.toString(rightBeaconNo));
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
		if(homoMat == null || homoMat.empty() == true){
			Log.d(LOG_TAG, "Homography matrix not set");
			return;
		}
		
		SelfLocalizationActivity.homoMat = homoMat;
		
		Log.d(LOG_TAG, "Beacon Detection started");
		
		//reset beacon numbers
		leftBeaconNo = 0;
		rightBeaconNo = 0;
		
		//call self localization activity
		//prog.start();
		prog.startSelfLocalization();
	}
	
	//call homography activity
	public void onHomography(View v){
		
		//reset homoMat
		if(homoMat != null){
		
			homoMat.release();
		}
		
		//call homography activity
		Intent homo = new Intent(this, GetHomographyActivity.class);
		startActivityForResult(homo, 2);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 2){
			String result = data.getStringExtra("result");
			System.out.println("Returned from homography with code " + result);
		}else if(requestCode == 3){
			String result = data.getStringExtra("result");
			prog.testMethod(result);
		//ball detection
		}else if(requestCode == 4){
			prog.ballDetectionCallback();
		}
	}
	
	public void onGetColor(View v){
		
		//call color detection activity
		Intent color = new Intent(this, GetColorActivity.class);
		startActivityForResult(color, 1);
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
