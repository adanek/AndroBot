package at.uibk.informatik.androbot.app;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.TextView;
import at.uibk.informatik.androbot.programms.ColorBlobDetector;

public class GetColorActivity extends Activity implements OnTouchListener,
		CvCameraViewListener2, SeekBar.OnSeekBarChangeListener {
	private static final String TAG = "ColorDetection";

	private boolean mIsColorSelected = false;
	private Mat mRgba;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;

	public static Mat homoMat;
	private Scalar defaultColor = new Scalar(120.0, 255.0, 110.0, 0.0);

	private CameraBridgeViewBase mOpenCvCameraView;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView.setOnTouchListener(GetColorActivity.this);
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
		SeekBar h = (SeekBar) findViewById(R.id.seekH);
		SeekBar s = (SeekBar) findViewById(R.id.seekS);
		SeekBar v = (SeekBar) findViewById(R.id.seekV);

		SeekBar hp = (SeekBar) findViewById(R.id.seekHplus);
		SeekBar sp = (SeekBar) findViewById(R.id.seekSplus);
		SeekBar vp = (SeekBar) findViewById(R.id.seekVplus);
		
		// register listeners
		h.setOnSeekBarChangeListener(this);
		s.setOnSeekBarChangeListener(this);
		v.setOnSeekBarChangeListener(this);
		hp.setOnSeekBarChangeListener(this);
		sp.setOnSeekBarChangeListener(this);
		vp.setOnSeekBarChangeListener(this);

		// set default values
		h.setProgress((int) defaultColor.val[0]);
		s.setProgress((int) defaultColor.val[1]);
		v.setProgress((int) defaultColor.val[2]);
		
		// set default values
		hp.setProgress(25);
		sp.setProgress(50);
		vp.setProgress(50);
		
		TextView lblH = (TextView) findViewById(R.id.txtH);
		TextView lblS = (TextView) findViewById(R.id.txtS);
		TextView lblV = (TextView) findViewById(R.id.txtV);

		// fill values on screen
		lblH.setText(Integer.toString(h.getProgress()));
		lblS.setText(Integer.toString(s.getProgress()));
		lblV.setText(Integer.toString(v.getProgress()));

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
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
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
		CONTOUR_COLOR = new Scalar(255, 0, 0, 255);

		// default color
		mBlobColorHsv = defaultColor;
		mDetector.setHsvColor(mBlobColorHsv);
		mIsColorSelected = true;
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	public boolean onTouch(View v, MotionEvent event) {
		int cols = mRgba.cols();
		int rows = mRgba.rows();

		int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
		int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

		int x = (int) event.getX() - xOffset;
		int y = (int) event.getY() - yOffset;

		if ((x < 0) || (y < 0) || (x > cols) || (y > rows))
			return false;

		Rect touchedRect = new Rect();

		touchedRect.x = (x > 4) ? x - 4 : 0;
		touchedRect.y = (y > 4) ? y - 4 : 0;

		touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols
				- touchedRect.x;
		touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows
				- touchedRect.y;

		Mat touchedRegionRgba = mRgba.submat(touchedRect);

		Mat touchedRegionHsv = new Mat();
		Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv,
				Imgproc.COLOR_RGB2HSV_FULL);

		// Calculate average color of touched region
		mBlobColorHsv = Core.sumElems(touchedRegionHsv);
		int pointCount = touchedRect.width * touchedRect.height;
		for (int i = 0; i < mBlobColorHsv.val.length; i++)
			mBlobColorHsv.val[i] /= pointCount;

		Log.d(TAG, String.format("Touched values: %f %f %f %f",
				mBlobColorHsv.val[0], mBlobColorHsv.val[1],
				mBlobColorHsv.val[2], mBlobColorHsv.val[3]));
		mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

		Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", "
				+ mBlobColorRgba.val[1] + ", " + mBlobColorRgba.val[2] + ", "
				+ mBlobColorRgba.val[3] + ")");

		mDetector.setHsvColor(mBlobColorHsv);

		//set seek bar values
		// get seek bar objects
		SeekBar h   = (SeekBar) findViewById(R.id.seekH);
		SeekBar s   = (SeekBar) findViewById(R.id.seekS);
		SeekBar vau = (SeekBar) findViewById(R.id.seekV);

		// set default values
		h.setProgress((int) mBlobColorHsv.val[0]);
		s.setProgress((int) mBlobColorHsv.val[1]);
		vau.setProgress((int) mBlobColorHsv.val[2]);
		
		Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

		mIsColorSelected = true;

		touchedRegionRgba.release();
		touchedRegionHsv.release();

		return false; // don't need subsequent touch events
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		mRgba = inputFrame.rgba();

		if (mIsColorSelected) {
			mDetector.process(mRgba);
			List<MatOfPoint> contours = mDetector.getContours();

			Log.e(TAG, "Contours count: " + contours.size());
			Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

			Mat colorLabel = mRgba.submat(4, 68, 4, 68);
			colorLabel.setTo(mBlobColorRgba);

			Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70,
					70 + mSpectrum.cols());
			mSpectrum.copyTo(spectrumLabel);

		}

		return mRgba;

	}

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL,
				4);

		return new Scalar(pointMatRgba.get(0, 0));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		if (mDetector == null) {
			return;
		}

		Log.d(TAG, "SeekBar: " + seekBar.getId() + "; Progress: " + progress);

		switch (seekBar.getId()) {
		case (R.id.seekH):
			mBlobColorHsv.val[0] = (double) progress;
			TextView lblH = (TextView) findViewById(R.id.txtH);
			lblH.setText(Integer.toString(progress));
			break;
		case (R.id.seekS):
			mBlobColorHsv.val[1] = (double) progress;
			TextView lblS = (TextView) findViewById(R.id.txtS);
			lblS.setText(Integer.toString(progress));
			break;
		case (R.id.seekV):
			mBlobColorHsv.val[2] = (double) progress;
			TextView lblV = (TextView) findViewById(R.id.txtV);
			lblV.setText(Integer.toString(progress));
			break;
		case (R.id.seekHplus):
			mDetector.setColorRadius(new Scalar((double) progress, mDetector.getmColorRadius().val[1],mDetector.getmColorRadius().val[2]));
			break;
		case (R.id.seekSplus):
			mDetector.setColorRadius(new Scalar(mDetector.getmColorRadius().val[0], (double) progress,mDetector.getmColorRadius().val[2]));
			break;
		case (R.id.seekVplus):
			mDetector.setColorRadius(new Scalar(mDetector.getmColorRadius().val[0], mDetector.getmColorRadius().val[1], (double) progress));
			break;
		}

		// set new color in color detector
		mDetector.setHsvColor(mBlobColorHsv);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
	
	//select as red
	public void onSetRed(View v){
		
		BeaconDetectionActivity.red = mBlobColorHsv;
		
	}
	
	//select as blue
	public void onSetBlue(View v){
		
		BeaconDetectionActivity.blue = mBlobColorHsv;
		
	}
	//select as yellow
	public void onSetYellow(View v){
		
		BeaconDetectionActivity.yellow = mBlobColorHsv;
		
	}
	//select as white
	public void onSetWhite(View v){
		
		BeaconDetectionActivity.white = mBlobColorHsv;
		
	}
	
    private Mat converScalarRgba2Hsv(Scalar rgbaColor) {
        Mat pointMatHsv = new Mat();
        Mat pointMatRgba = new Mat(1, 1, CvType.CV_8UC3, rgbaColor);
        Imgproc.cvtColor(pointMatRgba, pointMatHsv, Imgproc.COLOR_RGB2HSV, 4);

        //return new Scalar(pointMatHsv.get(0, 0));
        return pointMatHsv;
    }

}
