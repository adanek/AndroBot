package at.uibk.informatik.androbot.app;

import java.util.ArrayList;
import java.util.Collections;
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
import at.uibk.informatik.androbot.control.Position;
import at.uibk.informatik.androbot.data.Beacon;
import at.uibk.informatik.androbot.data.ColorRange;
import at.uibk.informatik.androbot.data.Element;
import at.uibk.informatik.androbot.enums.Colors;
import at.uibk.informatik.androbot.programms.ColorBlobDetector;

public class SelfLocalizationActivity extends Activity implements CvCameraViewListener2 {
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
	//private Scalar red;
	//private Scalar blue;
	//private Scalar yellow;
	//private Scalar white;
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

		// set colors
		//red = BeaconDetectionActivity.red;
		//blue = BeaconDetectionActivity.blue;
		//yellow = BeaconDetectionActivity.yellow;
		//white = BeaconDetectionActivity.white;

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
		CONTOUR_COLOR = new Scalar(255, 255, 255, 255);

		// begin with red
		mDetector.Hmin = BeaconDetectionActivity.red.getHmin();
		mDetector.Hmax = BeaconDetectionActivity.red.getHmax();
		mDetector.Smin = BeaconDetectionActivity.red.getSmin();
		mDetector.Smax = BeaconDetectionActivity.red.getSmax();
		mDetector.Vmin = BeaconDetectionActivity.red.getVmin();
		mDetector.Vmax = BeaconDetectionActivity.red.getVmax();
		
		mDetector.setHsvColor(mBlobColorHsv);
		
		mIsColorSelected = true;

		frame = 0;
		current = Colors.RED;
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		mRgba = inputFrame.rgba();

		frame++;

		if (frame < 20) {
			return mRgba;
		}

		// create element list
		elements = new ArrayList<Element>();

		if (mIsColorSelected) {

			// switch color
			for (int j = 0; j < 4; j++) {
				switch (j) {
				case 0:
					setColor(Colors.RED);
					break;
				case 1:
					setColor(Colors.BLUE);
					break;
				case 2:
					setColor(Colors.YELLOW);
					break;
				case 3:
					setColor(Colors.WHITE);
					break;
				}

				mDetector.process(mRgba);
				List<MatOfPoint> contours = mDetector.getContours();

				Log.e(TAG, "Contours count: " + contours.size());
				Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

				Mat colorLabel = mRgba.submat(4, 68, 4, 68);
				colorLabel.setTo(mBlobColorRgba);

				Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
				mSpectrum.copyTo(spectrumLabel);

				// check all contours
				for (int i = 0; i < contours.size(); i++) {

					// get current contour
					MatOfPoint mat = contours.get(i);

					// get point list
					List<Point> list = mat.toList();

					double minX = Double.MAX_VALUE;
					double maxX = Double.MIN_VALUE;
					double minY = Double.MAX_VALUE;
					Point lowest = new Point();
					for (Point p : list) {

						// lowest y
						if (p.y < minY) {
							minY = p.y;
						}

						// minimum X
						if (p.x < minX) {
							minX = p.x;
						}

						// maximum X
						if (p.x > maxX) {
							maxX = p.x;
						}

					}

					// calculate middle X
					lowest.y = minY;
					lowest.x = (maxX + minX) / 2;

					Element elem = new Element(current, lowest);

					Log.d("Blue cow", elem.toString());

					// add element to elements list
					elements.add(elem);
				}
			}
		}

		// sort beacons by X value
		Collections.sort(elements);

		List<Beacon> foundBeacons = getBeaconsFromElements(elements);

		Position pos;
		// more than one beacon found?
		if (foundBeacons.size() > 1) {
			pos = calculatePosition(foundBeacons);
		} else {
			pos = null;
		}

		// set beacon numbers
		if (foundBeacons.size() > 0) {
			BeaconDetectionActivity.leftBeaconNo = foundBeacons.get(0).getId();
		}
		if (foundBeacons.size() > 1) {
			BeaconDetectionActivity.rightBeaconNo = foundBeacons.get(1).getId();
		}

		// set current position in BeaconDetectionActivity
		BeaconDetectionActivity.current = pos;

		String result = pos == null ? "Null" : pos.toString();
		// go back to beacon activity
		Intent beacon = new Intent(this, BeaconDetectionActivity.class);
		beacon.putExtra("result", result);
		setResult(RESULT_OK, beacon);
		finish();

		return mRgba;
	}

	// calculate robots position from found beacons
	private Position calculatePosition(List<Beacon> foundBeacons) {

		// set homography matrix in detector
		mDetector.setHomoMat(homoMat);

		// sort beacons by id
		Collections.sort(foundBeacons);

		Beacon beaconLeft = foundBeacons.get(0);
		Beacon beaconRight = foundBeacons.get(1);

		if ((beaconLeft.getId() + 1) != beaconRight.getId()) {
			if (beaconLeft.getId() == 1) {
				beaconRight = foundBeacons.get(foundBeacons.size() - 1);
				if (beaconRight.getId() != 8) {
					Log.d("Beacon found", "Beacon order is wrong");
					return null;
				} else {
					beaconLeft = beaconRight;
					beaconRight = foundBeacons.get(0);
				}
			} else {
				Log.d("Beacon found", "Beacon order is wrong");
				return null;
			}
		}

		Point Br = mDetector.getWorldCoordinates(beaconLeft.getPos());
		Point Ar = mDetector.getWorldCoordinates(beaconRight.getPos());

		Point Aw = new Point(beaconRight.getX(), beaconRight.getY()); // given
		int beaconId = beaconRight.getId();

		Point Cr = new Point(0, 0);

		double a = Math.sqrt(Math.pow(Br.x, 2) + Math.pow(Br.y, 2));
		double b = Math.sqrt(Math.pow(Ar.x, 2) + Math.pow(Ar.y, 2));

		// space between the beacons
		double c = 125;

		// law of cosinus c2 = a2 + b2 - 2ab cosgamma or
		// a2 = b2 + c2 - 2bc cos(alpha)
		// alpha = acos((-(a2) + b2 + c2) / 2bc)
		double alphaRad = (-1 * Math.pow(a, 2)) + Math.pow(b, 2) + Math.pow(c, 2);
		alphaRad = Math.acos(alphaRad / (2 * b * c));
		double alpha = Math.toDegrees(alphaRad);

		// Kompliment to 90 degrees
		double alpha1 = Math.toRadians(90 - alpha);

		// sin(alpha1) = v / b
		double u = Math.cos(alpha1) * b;

		// cos(alpha1) = u / b;
		double v = Math.sin(alpha1) * b;

		double dx = -1 * v;
		double dy = -1 * u;

		switch (beaconId) {
		case 1:
		case 8:
			dx = v * -1;
			dy = u * -1;
			break;
		case 2:
		case 3:
			dx = u * -1;
			dy = v;
			break;
		case 4:
		case 5:
			dx = v;
			dy = u;
			break;
		case 6:
		case 7:
			dx = u;
			dy = v * -1;
			break;
		}

		// Triangle in world coordinates
		Point Cw = new Point(Aw.x + dx, Aw.y + dy);

		// Geradengleichung der 2 Beacons lösen aus ego Sicht Robot
		// y = k * x + d
		double k = (Ar.x - Br.x) == 0 ? 0 : (Ar.y - Br.y) / (Ar.x - Br.x);
		double d = Ar.y - k * Ar.x;

		// Calculate x for: 0 = k * x + d
		double a1 = k == 0 ? Ar.x : -d / k;
		Point S = new Point(a1, 0);

		// Calculate distance between S an Ar
		double dx1 = S.x - Ar.x;
		double dy1 = S.y - Ar.y;
		double c2 = Math.sqrt(Math.pow(dx1, 2) + Math.pow(dy1, 2));

		// Use the law of the cosinus to get gamma1
		double gamma2 = Math.toDegrees(Math.acos((Math.pow(a1, 2) + Math.pow(b, 2) - Math.pow(c2, 2)) / (2 * a1 * b)));

		double offset = 0;

		switch (beaconId) {
		case 4:
		case 5:
			offset = 0;
			break;
		case 2:
		case 3:
			offset = 90;
			break;
		case 1:
		case 8:
			offset = 180;
			break;
		case 6:
		case 7:
			offset = 270;
			break;
		}

		double theta = offset + alpha - gamma2;

		// Second half is negative angle
		theta = theta > 180 ? 0 - (360 - theta) : theta;
		System.out.println(String.format("x: %f y: %f th: %f", Cw.x, Cw.y, theta));

		Position current = new Position((int) Math.round(Cw.x), (int) Math.round(Cw.y), (int) Math.round(theta));
		return current;

	}

	public int getDistanceToTarget(Position current, Position target) {
		return (int) Math.sqrt(Math.pow(target.getX() - current.getX(), 2)
				+ Math.pow(target.getY() - current.getY(), 2));
	}

	private List<Beacon> getBeaconsFromElements(List<Element> elem) {

		List<Beacon> beacon = new ArrayList<Beacon>();

		int threshold = 30;

		for (int i = 0; i < (elem.size() - 1); i++) {

			// get difference
			int diff = (int) Math.round(Math.abs(elem.get(i).getP().x - elem.get(i + 1).getP().x));

			// in threshold??
			if (diff > threshold) {
				continue;
			}

			double val1 = elem.get(i).getP().y;
			double val2 = elem.get(i + 1).getP().y;

			Element lower = val1 < val2 ? elem.get(i) : elem.get(i + 1);
			Element upper = val1 > val2 ? elem.get(i) : elem.get(i + 1);

			Beacon beacon1 = getBeaconByColor(upper.getCol(), lower.getCol());

			beacon1.setPos(lower.getP());

			Log.d("Beacon found", "Lower color: " + beacon1.getLower() + " , upper color: " + beacon1.getUpper()
					+ " ; X: " + beacon1.getX() + " , Y: " + beacon1.getY() + " , Position: " + beacon1.getPos());

			// add to beacon list
			beacon.add(beacon1);

			i++;
		}

		return beacon;
	}

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

		return new Scalar(pointMatRgba.get(0, 0));
	}

	// initialize beacons
	private void initializeBeacons() {

		beacons = new ArrayList<Beacon>();

		// refresh beacons
		if (beacons != null && beacons.isEmpty() == false) {
			beacons.clear();
		}

		// begin test list
		beacons.add(addBeacon(125, 125, Colors.RED, Colors.YELLOW, 1));
		beacons.add(addBeacon(125, 0, Colors.WHITE, Colors.BLUE, 2));
		beacons.add(addBeacon(125, -125, Colors.YELLOW, Colors.RED, 3));
		beacons.add(addBeacon(0, -125, Colors.RED, Colors.BLUE, 4));
		beacons.add(addBeacon(-125, -125, Colors.YELLOW, Colors.BLUE, 5));
		beacons.add(addBeacon(-125, 0, Colors.WHITE, Colors.RED, 6));
		beacons.add(addBeacon(-125, 125, Colors.BLUE, Colors.YELLOW, 7));
		beacons.add(addBeacon(0, 125, Colors.BLUE, Colors.RED, 8));
		// end test list

		// create beacon list
		// beacons.add(addBeacon(125, 125, Colors.RED, Colors.YELLOW, 1));
		// beacons.add(addBeacon(125, 0, Colors.WHITE, Colors.RED, 2));
		// beacons.add(addBeacon(125, -125, Colors.YELLOW, Colors.RED, 3));
		// beacons.add(addBeacon(0, -125, Colors.RED, Colors.BLUE, 4));
		// beacons.add(addBeacon(-125, -125, Colors.YELLOW, Colors.BLUE, 5));
		// beacons.add(addBeacon(-125, 0, Colors.WHITE, Colors.BLUE, 6));
		// beacons.add(addBeacon(-125, 125, Colors.BLUE, Colors.YELLOW,7));
		// beacons.add(addBeacon(0, 125, Colors.BLUE, Colors.RED,8));

	}

	// create new beacon
	private Beacon addBeacon(int x, int y, Colors upper, Colors lower, int id) {

		return new Beacon(x, y, upper, lower, id);

	}

	// get beacon by color
	private Beacon getBeaconByColor(Colors upper, Colors lower) {

		Beacon b = null;
		for (int i = 0; i < beacons.size(); i++) {

			b = beacons.get(i);

			if (b.getUpper().equals(upper) && b.getLower().equals(lower)) {
				break;
			}

		}

		return b;

	}

	public void setColor(Colors col) {

		ColorRange color = null;
		
		switch (col) {
		case RED:
			color = BeaconDetectionActivity.red;
			break;
		case BLUE:
			color = BeaconDetectionActivity.blue;
			break;
		case YELLOW:
			color = BeaconDetectionActivity.yellow;
			break;
		case WHITE:
			color = BeaconDetectionActivity.white;
			break;
		}

		mDetector.Hmin = color.getHmin();
		mDetector.Hmax = color.getHmax();
		mDetector.Smin = color.getSmin();
		mDetector.Smax = color.getSmax();
		mDetector.Vmin = color.getVmin();
		mDetector.Vmax = color.getVmax();
		current = col;

	}

}
