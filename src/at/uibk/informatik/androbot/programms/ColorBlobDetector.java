package at.uibk.informatik.androbot.programms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;
import at.uibk.informatik.androbot.data.ColorRange;

public class ColorBlobDetector {
	

	private static final String LOG_TAG = "ColorBlobDetector";
	// Lower and Upper bounds for range checking in HSV color space
	private Scalar mLowerBound = new Scalar(0);
	private Scalar mUpperBound = new Scalar(0);
	// Minimum contour area in percent for contours filtering
	private static double mMinContourArea = 0.1;
	// Color radius for range checking in HSV color space
	private Scalar mColorRadius = new Scalar(25, 50, 50, 0);
	private Mat mSpectrum = new Mat();
	private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

	// color range
	public static int Hmin = 0;
	public static int Hmax = 255;
	public static int Smin = 0;
	public static int Smax = 255;
	public static int Vmin = 0;
	public static int Vmax = 255;

	// elements for noise filter
	Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
	Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
	
	// different pictures
	Mat RGB;
	Mat HSV;
	Mat BIN;
	
	private Mat homoMat;

	// Cache
	Mat mPyrDownMat = new Mat();
	Mat mHsvMat = new Mat();
	Mat mMask = new Mat();
	Mat mDilatedMask = new Mat();
	Mat mHierarchy = new Mat();
	public Point d;

//	public void setColorRadius(Scalar radius) {
//		mColorRadius = radius;
//	}
//
//	public Scalar getmColorRadius() {
//		return mColorRadius;
//	}
//
//	public void setmColorRadius(Scalar mColorRadius) {
//		this.mColorRadius = mColorRadius;
//	}
//
	
	public void setColor(ColorRange color){
		
		Hmin = color.getHmin();
		Hmax = color.getHmax();
		Smin = color.getSmin();
		Smax = color.getSmax();
		Vmin = color.getVmin();
		Vmax = color.getVmax();		
	}
	
	public void setHsvColor(Scalar hsvColor) {
		double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0] - mColorRadius.val[0] : 0;
		double maxH = (hsvColor.val[0] + mColorRadius.val[0] <= 255) ? hsvColor.val[0] + mColorRadius.val[0] : 255;

		mLowerBound.val[0] = minH;
		mUpperBound.val[0] = maxH;

		mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
		mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

		mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
		mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

		mLowerBound.val[3] = 0;
		mUpperBound.val[3] = 255;

		Mat spectrumHsv = new Mat(1, (int) (maxH - minH), CvType.CV_8UC3);

		for (int j = 0; j < maxH - minH; j++) {
			byte[] tmp = { (byte) (minH + j), (byte) 255, (byte) 255 };
			spectrumHsv.put(0, j, tmp);
		}

		Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
	}
//
	public Mat getSpectrum() {
		return mSpectrum;
	}
//
//	public void setMinContourArea(double area) {
//		mMinContourArea = area;
//	}

	public void process(Mat rgbaImage) {

		RGB = rgbaImage;
		HSV = new Mat();
		BIN = new Mat();
		
		// temp mat for findcontours
		Mat tmp = new Mat();
		
		
		// Convert rgba to hsv color space
		Imgproc.cvtColor(rgbaImage, HSV, Imgproc.COLOR_BGR2HSV);
		
		// Apply filter to mthe image
		Scalar lower = new Scalar(Hmin, Smin, Vmin);
		Scalar upper = new Scalar(Hmax, Smax, Vmax);
		Core.inRange(HSV, lower, upper, BIN);
		
		// Reduce the noise
		Imgproc.erode(BIN, BIN, erodeElement);
		Imgproc.erode(BIN, BIN, erodeElement);
		Imgproc.dilate(BIN, BIN, dilateElement);
		Imgproc.dilate(BIN, BIN, dilateElement);		

		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		BIN.copyTo(tmp);
		Imgproc.findContours(tmp, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Log.d(LOG_TAG, String.format("Found %d contours in frame", contours.size()));
//		// Find max contour area
//		double maxArea = 0;
		Iterator<MatOfPoint> each = contours.iterator();
//		while (each.hasNext()) {
//			MatOfPoint wrapper = each.next();
//			double area = Imgproc.contourArea(wrapper);
//			if (area > maxArea)
//				maxArea = area;
//		}

		// Filter contours by area and resize to fit the original image size
		mContours.clear();
		each = contours.iterator();
		while (each.hasNext()) {
			MatOfPoint contour = each.next();
			mContours.add(contour);
//			if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
//				//Core.multiply(contour, new Scalar(4, 4), contour); //?
//				mContours.add(contour);
//			}
		}
	}

	public List<MatOfPoint> getContours() {
		return mContours;
	}

	
	// Converts a point in screen coordinates in a point in world coordinates
	public Point getWorldCoordinates(Point ps) {
		Mat src = new Mat(1, 1, CvType.CV_32FC2);
		Mat dest = new Mat(1, 1, CvType.CV_32FC2);
		src.put(0, 0, new double[] { ps.x, ps.y }); // ps is a point in image coordinates
		Core.perspectiveTransform(src, dest, getHomoMat()); // homography is your homography matrix
		Point dest_point = new Point(dest.get(0, 0)[0], dest.get(0, 0)[1]);

		d = new Point(dest_point.y / 10, dest_point.x / 10 * -1);
		Log.d(LOG_TAG, String.format("Ball @world x:%f y:%f", d.x, d.y));

		return d;
	}

	public Mat getHomoMat() {
		return homoMat;
	}

	public void setHomoMat(Mat homoMat) {
		this.homoMat = homoMat;
	}
	
	public Mat getRgbImage(){
		return RGB;
	}
	
	public Mat getHsvImage(){
		return HSV;		
	}
	
	public Mat getBinImage(){
		return BIN;
	}
}
