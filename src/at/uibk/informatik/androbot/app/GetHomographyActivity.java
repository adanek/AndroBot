package at.uibk.informatik.androbot.app;

import java.util.LinkedList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
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
import at.uibk.informatik.androbot.programms.ColorBlobDetector;

public class GetHomographyActivity extends Activity implements
		CvCameraViewListener2 {
	private static final String TAG = "GetHomography";

	private boolean mIsColorSelected = false;
	private Mat mRgba;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;

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

	public GetHomographyActivity() {
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

	public Mat getHomographyMatrix(Mat mRgba) {
		final Size mPatternSize = new Size(6, 9); // number of inner corners in
													// the used chessboard
													// pattern
		float x = -48.0f; // coordinates of first detected inner corner on
							// chessboard
		float y = 309.0f;
		float delta = 12.0f; // size of a single square edge in chessboard
		LinkedList<Point> PointList = new LinkedList<Point>();

		// Define real-world coordinates for given chessboard pattern:
		for (int i = 0; i < mPatternSize.height; i++) {
			y = 309.0f;
			for (int j = 0; j < mPatternSize.width; j++) {
				PointList.addLast(new Point(x, y));
				y += delta;
			}
			x += delta;
		}
		MatOfPoint2f RealWorldC = new MatOfPoint2f();
		RealWorldC.fromList(PointList);

		// Detect inner corners of chessboard pattern from image:
		Mat gray = new Mat();
		Imgproc.cvtColor(mRgba, gray, Imgproc.COLOR_RGBA2GRAY); // convert image
																// to grayscale
		MatOfPoint2f mCorners = new MatOfPoint2f();
		boolean mPatternWasFound = Calib3d.findChessboardCorners(gray,
				mPatternSize, mCorners);

		Log.d(TAG, String.format("Pattern was found: %b", mPatternWasFound));

		// Calculate homography:
		if (mPatternWasFound) {
			// Calib3d.drawChessboardCorners(mRgba, mPatternSize, mCorners,
			// mPatternWasFound); // for visualization
			return Calib3d.findHomography(mCorners, RealWorldC);
		} else
			return new Mat();
	}

	int frame = 0;

	// on camera frame
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();

		frame++;
		Log.d(TAG, "Pattern " + frame);
		if ((frame % 20) == 0) {

			Log.d(TAG, "Pattern Started");

			for (int i = 0; i < 10; i++) {

				// get homo matrix
				Mat homoMat = getHomographyMatrix(mRgba);

				// homo matrix was filled
				if (homoMat.empty() == false) {
					// set homo matrix
					BlobActivity.setHomoMat(homoMat);
					BeaconDetectionActivity.setHomoMat(homoMat);

					//mOpenCvCameraView.disableView();
					
					// go back to beacon activity
					Intent beacon = new Intent(this, BeaconDetectionActivity.class);
					beacon.putExtra("result", "found");
					setResult(RESULT_OK,beacon);
					finish();
					break;
				} else {
					break;
				}
			}
		}

		return mRgba;
	}
}
