import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;
import edu.wpi.cscore.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.List;
import java.util.ArrayList;

/*

  |\      _,,,,--,,_
  /,`.-'`'    -,  ;-;,
 |,4-  ) ),,__ ) /;  ;;  Code cat is tired
'---''(.'--'  (.'`.) `'

~meow

*/

public class Vision{
	private NetworkTable sd;
	private double adjustValue; //- for left, 0 on, + for right

	//Diagonal fov of 68.5 degrees
	private UsbCamera camera;
	private int width = 640;
	private int height = 480;
	private int fps = 30;

	// This is the network port you want to stream the raw and cv images to
	// By rules, must be between 1180 and 1190
	private int rawStreamPort = 1185;
	private int cvStreamPort = 1186;
	private MjpegServer rawStream;
	private MjpegServer cvStream;

	//Cv sink to grab frames to process
	private CvSink cvSink;
	//Cv source to output to the cv stream
	private CvSource cvSource;

	public Vision(){
		// Loads our OpenCV library. This MUST be included
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		NetworkTable.setClientMode();
		// Set your team number here
		NetworkTable.setTeam(4338);
		//Get the SmartDashboard networktable
		sd = NetworkTable.getTable("SmartDashboard");

		//Initialize Camera
		camera = new UsbCamera("CoprocessorCamera", 0);
		camera.setPixelFormat(VideoMode.PixelFormat.kMJPEG);
		camera.setResolution(width, height);
		camera.setFPS(fps);

		//Initialize cv sink and source
		cvSink = new CvSink("CV Image Grabber");
		cvSink.setSource(camera);
		cvSource = new CvSource("CV Image Source", VideoMode.PixelFormat.kMJPEG, width, height, fps);

		//Initialize streams
		rawStream = new MjpegServer("Raw Server", rawStreamPort);
		rawStream.setSource(camera);
		cvStream = new MjpegServer("CV Server", cvStreamPort);
		cvStream.setSource(cvSource);
	}

	/*
	TODO work on identifying targets not just the largest contour
	http://wpilib.screenstepslive.com/s/4485/m/24194/l/288985-identifying-and-processing-the-targets

	TODO:
		implement sorting contours by area size, not just the largest
			needs to be as fast as possible
			quicksort?
		Look through sorted list of contours for potential targets
			target based on aspect ratio and size?
			to find the two tape bands on the boiler (2 contours)
		Larger bounding box encompassing the two target contours??
	*/
	public void processBoiler(){
		Mat frame = new Mat();
		Mat hsv = new Mat();

		//Range to filter
		Scalar lower = new Scalar(110, 50, 50);
		Scalar upper = new Scalar(130, 255, 255);

		//Kernel size to blur with
		Size blurAmount = new Size(5, 5);

		Mat hierarchy = new Mat();
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Scalar contourColor = new Scalar(110, 255, 255);

		BoilerTarget target;

		while(true){
			// Grab a frame. If it has a frame time of 0, there was an error.
			// Just skip and continue
			long frameTime = cvSink.grabFrame(frame);
			if(frameTime == 0) continue;

			//Convert to HSV for easier filtering
			Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);
			//Filter image by color
			Core.inRange(hsv, lower, upper, hsv);

			//Clear contours list
			contours.clear();
			//Blur frame to omit extra noise
			Imgproc.blur(hsv, hsv, blurAmount);
			//Find contours
			Imgproc.findContours(hsv, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

			//Find target
			if(contours.size() > 0){
				//Merge sort the contours
				sortContours(contours);
				//Find the matching target
				target = findBoilerTarget(contours);

				//Draw findings
				Imgproc.drawContours(hsv, contours, -1, contourColor);
				if(target.exists()){ //Draw target if it exists
					for(int i = 0; i < 4; i++){
						//Fix this line
						Improc.line(hsv, targetVerts[i], targetVerts[(j + 1) % 4], targetColor);
					}
				}

				//Update network table with the target data
				sd.putBoolean("targetExists", target.exists);
				sd.putNumber("adjustValue", target.getNormalizedHorizontalOffset(width));
			}

			//Put modified cv image on cv source to be streamed
			cvSource.putFrame(hsv);
		}
	}

	private BoilerTarget findBoilerTarget(ArrayList<MatOfPoint> contours){
		BoilerTarget target;
		Point[] targetVerts = new Point[4];
		double targetRatio = 3.75f; //15in / 4in
		double fudge = 0.1f; //Maybe ~10% uncertainty

		for(MatOfPoint contour : contours){
			//target = Improc.minAreaRect(contour);
			//target.points(targetVerts);
			//target.getAspectRatio();
		}

		return target;
	}

	public void processGear(){
		Mat frame = new Mat();
		Mat hsv = new Mat();
	}

	public void processTest(){
		// All Mats and Lists should be stored outside the loop to avoid allocations
		// as they are expensive to create
		Mat frame = new Mat();
		Mat hsv = new Mat();

		//Range to filter
		Scalar lower = new Scalar(110, 50, 50);
		Scalar upper = new Scalar(130, 255, 255);

		//Kernel size to blur with
		Size blurAmount = new Size(5, 5);

		Mat hierarchy = new Mat();
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Scalar contourColor = new Scalar(110, 255, 255);
		Rect target;
		Scalar targetColor = new Scalar(255, 255, 255);

		//Processing loop
		while(true){
			// Grab a frame. If it has a frame time of 0, there was an error.
			// Just skip and continue
			long frameTime = cvSink.grabFrame(frame);
			if (frameTime == 0) continue;

			//Convert to HSV for easier filtering
			Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);
			//Filter image by color
			Core.inRange(hsv, lower, upper, hsv);

			//Clear contours list
			contours.clear();
			//Blur frame to omit extra noise
			Imgproc.blur(hsv, hsv, blurAmount);
			//Find contours
			Imgproc.findContours(hsv, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

			//Find largest contour
			if(contours.size() > 0){
				target = findLargestContour(contours);
				Imgproc.drawContours(hsv, contours, -1, contourColor);
				Imgproc.rectangle(hsv, new Point(target.x, target.y), new Point(target.x + target.width, target.y + target.height), targetColor);

				adjustValue = (target.x + target.width / 2) - width / 2;
				//Update network table
				sd.putNumber("adjustValue", adjustValue);
			}

			//Put modified cv image on cv source to be streamed
			cvSource.putFrame(hsv);
		}
	}

	private void sortContours(ArrayList<MatOfPoint> list, int first, int last){
		if(first == last){
		} else if(last - first == 1){
			if(list.get(last) > list.get(first)){ //Put greatest value first for greatest to least
				int temp = list.get(first);
				list.set(first, last);
				list.set(last, temp);
			}
		} else{
			int mid = (first + last) / 2;
			sortContours(list, first, mid);
			sortContours(list, mid + 1, last);
			sortedList = merge(list, first, mid, last);
		}
	}

	private void merge(ArrayList<MatOfPoint> list, int first, int mid, int last){
		ArrayList<MatOfPoint> mergedList = new ArrayList<MatOfPoint>();
		int pointerA = first;
		int pointerB = mid + 1;

		for(int i = 0; i < last - first + 1; i++){
			if(aDone){
				mergedList.add(list.get(pointerB));
				pointerB++;
			} else if(bDone){
				mergedList.add(list.get(pointerA));
				pointerA++;
			} else if(list.get(pointerA) > list.get(pointerB)){
				mergedList.add(list.get(i));
				pointerA++;
			} else{
				mergedList.add(list.get(pointerB));
				pointerB++;
			}

			if(pointerA > mid){
				aDone = true;
			}
			if(pointerB > last){
				bDone = true;
			}
		}

		for(int i = first; i <= last; i++){
			list.set(i, mergedList.get(i));
		}
	}

	private Rect findLargestContour(ArrayList<MatOfPoint> contours){
		double largestArea = Imgproc.contourArea(contours.get(0));
		int largestIndex = 0;

		for(int i = 1; i < contours.size(); i++){
			double nextArea = Imgproc.contourArea(contours.get(i));
			if(nextArea > largestArea){
				largestArea = nextArea;
				largestIndex = i;
			}
		}

		return Imgproc.boundingRect(contours.get(largestIndex));
	}

	public static void main(String[] args){
		Vision vision = new Vision();
		vision.processTest();
	}
}
