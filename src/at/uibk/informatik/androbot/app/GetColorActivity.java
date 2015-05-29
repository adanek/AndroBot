package at.uibk.informatik.androbot.app;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import at.uibk.informatik.androbot.data.ColorRange;
import at.uibk.informatik.androbot.programms.ColorBlobDetector;

public class GetColorActivity extends Activity implements CvCameraViewListener2, SeekBar.OnSeekBarChangeListener {
	private static final String TAG = "ColorDetection";

	private boolean mIsColorSelected = false;
	private Mat mRgba;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;
	private boolean rgb = true;

	public static Mat homoMat;

	private CameraBridgeViewBase mOpenCvCameraView;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public GetColorActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.color_identification);

		// get seek bar objects
		SeekBar hmin = (SeekBar) findViewById(R.id.seekHmin);
		SeekBar smin = (SeekBar) findViewById(R.id.seekSmin);
		SeekBar vmin = (SeekBar) findViewById(R.id.seekVmin);

		SeekBar hmax = (SeekBar) findViewById(R.id.seekHmax);
		SeekBar smax = (SeekBar) findViewById(R.id.seekSmax);
		SeekBar vmax = (SeekBar) findViewById(R.id.seekVmax);

		// register listeners
		hmin.setOnSeekBarChangeListener(this);
		smin.setOnSeekBarChangeListener(this);
		vmin.setOnSeekBarChangeListener(this);
		hmax.setOnSeekBarChangeListener(this);
		smax.setOnSeekBarChangeListener(this);
		vmax.setOnSeekBarChangeListener(this);

		// set default values - begin with red
		hmin.setProgress(BeaconDetectionActivity.red.getHmin());
		smin.setProgress(BeaconDetectionActivity.red.getSmin());
		vmin.setProgress(BeaconDetectionActivity.red.getVmin());

		hmax.setProgress(BeaconDetectionActivity.red.getHmax());
		smax.setProgress(BeaconDetectionActivity.red.getSmax());
		vmax.setProgress(BeaconDetectionActivity.red.getVmax());

		TextView lblHmin = (TextView) findViewById(R.id.txtHmin);
		TextView lblSmin = (TextView) findViewById(R.id.txtSmin);
		TextView lblVmin = (TextView) findViewById(R.id.txtVmin);

		TextView lblHmax = (TextView) findViewById(R.id.txtHmax);
		TextView lblSmax = (TextView) findViewById(R.id.txtSmax);
		TextView lblVmax = (TextView) findViewById(R.id.txtVmax);

		// fill values on screen
		lblHmin.setText(Integer.toString(hmin.getProgress()));
		lblSmin.setText(Integer.toString(smin.getProgress()));
		lblVmin.setText(Integer.toString(vmin.getProgress()));

		lblHmax.setText(Integer.toString(hmax.getProgress()));
		lblSmax.setText(Integer.toString(smax.getProgress()));
		lblVmax.setText(Integer.toString(vmax.getProgress()));

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mDetector = new ColorBlobDetector();
		mDetector.setHomoMat(homoMat);
		mSpectrum = new Mat();
		mBlobColorRgba = new Scalar(255);
		mBlobColorHsv = new Scalar(255);
		SPECTRUM_SIZE = new Size(200, 64);
		CONTOUR_COLOR = new Scalar(255, 255, 255, 255);

		// default color - red
		mDetector.Hmin = BeaconDetectionActivity.red.getHmin();
		mDetector.Hmax = BeaconDetectionActivity.red.getHmax();
		mDetector.Smin = BeaconDetectionActivity.red.getSmin();
		mDetector.Smax = BeaconDetectionActivity.red.getSmax();
		mDetector.Vmin = BeaconDetectionActivity.red.getVmin();
		mDetector.Vmax = BeaconDetectionActivity.red.getVmax();
		mIsColorSelected = true;
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		mRgba = inputFrame.rgba();

		mDetector.process(mRgba);
		List<MatOfPoint> contours = mDetector.getContours();

		Log.e(TAG, "Contours count: " + contours.size());
		Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR, 2);

		// Mat colorLabel = mRgba.submat(4, 68, 4, 68);
		// colorLabel.setTo(mBlobColorRgba);
		//
		// Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70,
		// 70 + mSpectrum.cols());
		// mSpectrum.copyTo(spectrumLabel);

		if(rgb){
			return mDetector.getRgbImage();
		}else{
			return mDetector.getBinImage();
		}
	}

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

		return new Scalar(pointMatRgba.get(0, 0));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		if (mDetector == null) {
			return;
		}

		Log.d(TAG, "SeekBar: " + seekBar.getId() + "; Progress: " + progress);

		switch (seekBar.getId()) {
		case (R.id.seekHmin):
			mDetector.Hmin = progress;
			TextView lblH = (TextView) findViewById(R.id.txtHmin);
			lblH.setText(Integer.toString(progress));
			break;
		case (R.id.seekSmin):
			mDetector.Smin = progress;
			TextView lblS = (TextView) findViewById(R.id.txtSmin);
			lblS.setText(Integer.toString(progress));
			break;
		case (R.id.seekVmin):
			mDetector.Vmin = progress;
			TextView lblV = (TextView) findViewById(R.id.txtVmin);
			lblV.setText(Integer.toString(progress));
			break;
		case (R.id.seekHmax):
			mDetector.Hmax = progress;
			TextView lblHmax = (TextView) findViewById(R.id.txtHmax);
			lblHmax.setText(Integer.toString(progress));
			break;
		case (R.id.seekSmax):
			mDetector.Smax = progress;
			TextView lblSmax = (TextView) findViewById(R.id.txtSmax);
			lblSmax.setText(Integer.toString(progress));
			break;
		case (R.id.seekVmax):
			mDetector.Vmax = progress;
			TextView lblVmax = (TextView) findViewById(R.id.txtVmax);
			lblVmax.setText(Integer.toString(progress));
			break;
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	// select as red
	public void onSetRed(View v) {

		BeaconDetectionActivity.red = new ColorRange(mDetector.Hmin, mDetector.Hmax, mDetector.Smin, mDetector.Smax,
				mDetector.Vmin, mDetector.Vmax);

	}

	// select as blue
	public void onSetBlue(View v) {

		BeaconDetectionActivity.blue = new ColorRange(mDetector.Hmin, mDetector.Hmax, mDetector.Smin, mDetector.Smax,
				mDetector.Vmin, mDetector.Vmax);

	}

	// select as yellow
	public void onSetYellow(View v) {

		BeaconDetectionActivity.yellow = new ColorRange(mDetector.Hmin, mDetector.Hmax, mDetector.Smin, mDetector.Smax,
				mDetector.Vmin, mDetector.Vmax);

	}

	// select as white
	public void onSetWhite(View v) {

		BeaconDetectionActivity.white = new ColorRange(mDetector.Hmin, mDetector.Hmax, mDetector.Smin, mDetector.Smax,
				mDetector.Vmin, mDetector.Vmax);

	}

	public void onSetBall(View v) {
		// Log.d("Color", mBlobColorHsv.toString());
		BeaconDetectionActivity.ballColor = new ColorRange(mDetector.Hmin, mDetector.Hmax, mDetector.Smin,
				mDetector.Smax, mDetector.Vmin, mDetector.Vmax);
	}

	private Mat converScalarRgba2Hsv(Scalar rgbaColor) {
		Mat pointMatHsv = new Mat();
		Mat pointMatRgba = new Mat(1, 1, CvType.CV_8UC3, rgbaColor);
		Imgproc.cvtColor(pointMatRgba, pointMatHsv, Imgproc.COLOR_RGB2HSV, 4);

		// return new Scalar(pointMatHsv.get(0, 0));
		return pointMatHsv;
	}

    
    public void onRGB(View v){
    	
    	CheckBox rgb = (CheckBox) findViewById(R.id.chkRGB);
    	
    	//show rgb frame
    	if(rgb.isChecked()){
    		this.rgb = true;
    	//show binary frame
    	}else{
    		this.rgb = false;
    	}
    	
    }
}
