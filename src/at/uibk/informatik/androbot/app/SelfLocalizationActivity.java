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
import at.uibk.informatik.androbot.data.Beacon;
import at.uibk.informatik.androbot.data.Element;
import at.uibk.informatik.androbot.enums.Colors;
import at.uibk.informatik.androbot.programms.ColorBlobDetector;

public class SelfLocalizationActivity extends Activity implements OnTouchListener,
		CvCameraViewListener2 {
	private static final String TAG = "SelfLocalization";

	private boolean mIsColorSelected = false;
	private Mat mRgba;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;

	public static Mat homoMat;
	private Scalar red;
	private Scalar blue;
	private Scalar yellow;
	private Scalar white;
	private Colors current;
	private int frame = 0;
	private List<Beacon> beacons;
	private List<Element> elements;

	private CameraBridgeViewBase mOpenCvCameraView;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView.setOnTouchListener(SelfLocalizationActivity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public SelfLocalizationActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.color_blob_detection_surface_view);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);

		//set colors
		red = BeaconDetectionActivity.red;
		blue = BeaconDetectionActivity.blue;
		yellow = BeaconDetectionActivity.yellow;
		white = BeaconDetectionActivity.white;
		
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
		
		initializeBeacons();
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

		// begin with red
		mBlobColorHsv = red;
		mDetector.setHsvColor(mBlobColorHsv);
		mIsColorSelected = true;
		
		frame = 0;
		current = Colors.RED;
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

		Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

		mIsColorSelected = true;

		touchedRegionRgba.release();
		touchedRegionHsv.release();

		return false; // don't need subsequent touch events
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		mRgba = inputFrame.rgba();

		frame++;
		
		//begin test
		
		//change color to blue
		if(frame == 100){
			mBlobColorHsv = blue;
			mDetector.setHsvColor(mBlobColorHsv);
			current = Colors.BLUE;
		//change color to yellow
		} else if(frame == 200){
			mBlobColorHsv = yellow;
			mDetector.setHsvColor(mBlobColorHsv);
			current = Colors.YELLOW;
		//change color to white
		} else if(frame == 300){
			mBlobColorHsv = white;
			mDetector.setHsvColor(mBlobColorHsv);
			current = Colors.WHITE;
		}
		
		//end test
		
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
			
			
			//check all contours
			for(int i = 0; i < contours.size(); i++){
				
				//get current contour
				MatOfPoint mat = contours.get(i);
				
				//get point list
				List<Point> list = mat.toList();
				
            	double minX = Double.MAX_VALUE;
            	double maxX = Double.MIN_VALUE;
            	double minY = Double.MAX_VALUE;
            	Point lowest = new Point();
            	for(Point p: list){
            		
            		//lowest y
            		if(p.y < minY){
            			minY = p.y;
            		}
            		
            		//minimum X
            		if(p.x < minX){
            			minX = p.x;
            		}
            		
            		//maximum X
            		if(p.x > maxX){
            			minX = p.x;
            		}
            		
            	}
            	
            	//calculate middle X
            	lowest.y = minY;
            	lowest.x = (maxX + minX) / 2;
				
				Element elem = new Element(current,lowest);
				
				//add element to elements list
				elements.add(elem);
			}

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
	
	//initialize beacons
	private void initializeBeacons(){
		
		//refresh beacons
		if(beacons != null && beacons.isEmpty() == false){
			beacons.clear();
		}
		
		//create beacon list
		beacons.add(addBeacon(125,125,Colors.RED,Colors.YELLOW));
		beacons.add(addBeacon(125,0,Colors.WHITE,Colors.RED));
		beacons.add(addBeacon(125,-125,Colors.YELLOW,Colors.RED));
		beacons.add(addBeacon(0,-125,Colors.RED,Colors.BLUE));
		beacons.add(addBeacon(-125,-125,Colors.YELLOW,Colors.BLUE));
		beacons.add(addBeacon(-125,0,Colors.WHITE,Colors.BLUE));
		beacons.add(addBeacon(-125,125,Colors.BLUE,Colors.YELLOW));
		beacons.add(addBeacon(0,125,Colors.BLUE,Colors.RED));
			
	}
	
	//create new beacon
	private Beacon addBeacon(int x, int y, Colors upper, Colors lower){
		
		return new Beacon(x,y,upper,lower);
	
	}
	
	//get beacon by color
	private Beacon getBeaconByColor(Colors upper, Colors lower){
		
		Beacon b = null;
		for(int i = 0; i < beacons.size(); i++){
			
			b = beacons.get(i);
			
			if(b.getUpper().equals(upper) && b.getLower().equals(lower)){
				break;
			}
			
		}
		
		return b;
		
	}

}
