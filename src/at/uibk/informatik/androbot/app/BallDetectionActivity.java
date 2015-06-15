package at.uibk.informatik.androbot.app;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.data.ColorRange;
import at.uibk.informatik.androbot.programms.BeaconDetection;
import at.uibk.informatik.androbot.programms.ColorBlobDetector;

public class BallDetectionActivity extends Activity implements CvCameraViewListener2 {
	private static final String TAG = "OCVSample::Activity";

	private Mat mRgba;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Scalar CONTOUR_COLOR;

	public static Mat homoMat;

	// The color of the ball
	public static ColorRange color;
	private int frames;

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

	public BallDetectionActivity() {
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
		this.frames = 0;
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
		new Size(200, 64);
		CONTOUR_COLOR = new Scalar(255, 100, 100, 255);

		mBlobColorHsv = color.getColor();
		mDetector.setHsvColor(mBlobColorHsv);
		mDetector.setColor(color);		
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		mRgba = inputFrame.rgba();
		frames++;

		if (frames < 10) {
			return mRgba;
		}

		mDetector.process(mRgba);
		List<MatOfPoint> contours = mDetector.getContours();

		if (contours.size() > 0) {
			MatOfPoint mat = contours.get(0);

			List<Point> list = mat.toList();

			double min = Double.MAX_VALUE;
			Point ball = null;
			for (Point p : list) {
				if (p.y < min) {
					min = p.y;
					ball = p;
				}
			}

			Log.d(TAG, String.format("Ball @Screen x:%f y:%f\n", ball.x, ball.y));

			Point pos = mDetector.getWorldCoordinates(ball);
			
			if(pos.x < 0){
				return mRgba;
			}
				
			BeaconDetection.ball = new Position((int) pos.x, (int) pos.y, 0);
			Core.circle(mRgba, ball, 10, new Scalar(25,25,25), -1);

			// go back to beacon activity
			Intent beacon = new Intent(this, BeaconDetectionActivity.class);
			setResult(RESULT_OK, beacon);
			finish();

		}

		Log.e(TAG, "Contours count: " + contours.size());
		Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR, 2);

		Mat colorLabel = mRgba.submat(4, 68, 4, 68);
		colorLabel.setTo(mBlobColorRgba);

		Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
		mSpectrum.copyTo(spectrumLabel);

		if (frames > 25) {
			BeaconDetection.ball = null;
			// go back to beacon activity
			Intent beacon = new Intent(this, BeaconDetectionActivity.class);
			setResult(RESULT_OK, beacon);
			finish();
		}

		return mRgba;
	}

}
