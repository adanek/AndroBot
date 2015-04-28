package at.uibk.informatik.androbot.app;

import java.util.LinkedList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.RadioButton;
import at.uibk.informatik.androbot.programms.ColorBlobDetector;

public class ColorBlobDetectionActivity extends ProgramActivityBase implements
		CvCameraViewListener2 {
	private static final String TAG = "ColorBlobDetectionActivty";

	private boolean mIsColorSelected = false;
	private Mat mRgba;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;

	//destination point
	Point dest_point = null;
	
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

	public ColorBlobDetectionActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// get HSV color scalar from blob activity
		mDetector.setHsvColor(BlobActivity.getColor()); // green or red

		// color is selected
		mIsColorSelected = true;

		setContentView(R.layout.color_blob_detection_surface_view);

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

		mSpectrum = new Mat();
		mBlobColorRgba = new Scalar(255);
		mBlobColorHsv = new Scalar(255);
		SPECTRUM_SIZE = new Size(200, 64);
		CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();

		if (mIsColorSelected) {
			mDetector.process(mRgba);
			List<MatOfPoint> contours = mDetector.getContours();

			if (contours.size() > 0) {
				MatOfPoint mat = contours.get(0);

				List<Point> list = mat.toList();

				Point min = new Point(Double.MIN_VALUE, Double.MIN_VALUE);
				for (Point p : list) {
					if (p.y > min.y) {
						min = p;
					}
				}
				Log.d(TAG,
						String.format("Min Position x: %f y: %f", min.x, min.y));

				// map from image plane to ground plane
				if (min.x != Double.MIN_VALUE && min.y != Double.MIN_VALUE) {
					Mat src = new Mat(1, 1, CvType.CV_32FC2);
					Mat dest = new Mat(1, 1, CvType.CV_32FC2);
					src.put(0, 0, new double[] { min.x, min.y }); // ps is a
																	// point in
																	// image
					// coordinates
					Core.perspectiveTransform(src, dest, homoMat); // homography
																	// is
					// your homography matrix
					dest_point = new Point(dest.get(0, 0)[0], dest.get(0,
							0)[1]);
				}

			}
			Log.e(TAG, "Contours count: " + contours.size());
			Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

			Mat colorLabel = mRgba.submat(4, 68, 4, 68);
			colorLabel.setTo(mBlobColorRgba);

			Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70,
					70 + mSpectrum.cols());
			mSpectrum.copyTo(spectrumLabel);
		}

		//ball located and position determined
		if(dest_point != null){
			// disable view
			mOpenCvCameraView.disableView();
			
			//log target coordinates
			Log.d(TAG, "Target coordinates: x = " + dest_point.x + "; y = " + dest_point.y);
		}
		
		return mRgba;
	}

	// on start
	public void onStart(View v) {

		// start ball catching algo
		Log.d("START", "started");
		
		
		

	}

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL,
				4);

		return new Scalar(pointMatRgba.get(0, 0));
	}
}
