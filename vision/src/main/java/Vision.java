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
		//Mess with these values!
		sd.putNumber("lowerH", 80);
		sd.putNumber("lowerS", 10);
		sd.putNumber("lowerV", 254);
		sd.putNumber("upperH", 90);
		sd.putNumber("upperS", 255);
		sd.putNumber("upperV", 255);

		//Initialize Camera
		camera = new UsbCamera("CoprocessorCamera", 0);
		camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, width, height, fps);
		camera.setBrightness(0);
		camera.setExposureManual(0);

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
				sortContours(contours, 0, contours.size() - 1);
				//Find the matching target
				target = findBoilerTarget(contours);

				//Draw findings
				Imgproc.drawContours(hsv, contours, -1, contourColor);
				if(target.doesExist()){ //Draw target if it exists
					for(int i = 0; i < 4; i++){
						//Fix this line, targetVerts out of scope!
						Imgproc.line(hsv, targetVerts[i], targetVerts[(i + 1) % 4], target.getColor());
					}
				}

				//Update network table with the target data
				sd.putBoolean("targetExists", target.doesExist());
				sd.putNumber("adjustValue", target.getNormalizedHorizontalOffset(width));
			}

			//Put modified cv image on cv source to be streamed
			cvSource.putFrame(hsv);
		}
	}

	//NOT DONE
	private BoilerTarget findBoilerTarget(ArrayList<MatOfPoint> contours){
		BoilerTarget target;
		Point[] targetVerts = new Point[4];
		double targetRatio = 3.75f; //15in / 4in
		double fudge = 0.1f; //Maybe ~10% uncertainty

		for(MatOfPoint contour : contours){
			//target = Imgproc.minAreaRect(contour);
			//target.points(targetVerts);
			//target.getAspectRatio();
		}

		return target;
	}
	*/

	private void sortContours(ArrayList<MatOfPoint> contours){
		for(int i = 1; i < contours.size(); i++){
			int j = i;
			while(j > 0){
				if(Imgproc.contourArea(contours.get(j)) > Imgproc.contourArea(contours.get(j-1))){
					MatOfPoint temp = contours.get(j-1);
					contours.set(j-1, contours.get(j));
					contours.set(j, temp);
				}
				j--;
			}
		}
	}

	public void processBlue(){
		// All Mats and Lists should be stored outside the loop to avoid allocations
		// as they are expensive to create
		Mat frame = new Mat();
		Mat hsv = new Mat();

		//Range to filter
		Scalar lower;
		Scalar upper;

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

			lower = new Scalar(sd.getNumber("lowerH", 0), sd.getNumber("lowerS", 0), sd.getNumber("lowerV", 0));
			upper = new Scalar(sd.getNumber("upperH", 0), sd.getNumber("upperS", 0), sd.getNumber("upperV", 0));
			System.out.println("L:" + lower);
			System.out.println("U:" + upper);

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
				sortContours(contours);
				target = Imgproc.boundingRect(contours.get(0));
				Imgproc.drawContours(hsv, contours, -1, contourColor);
				Imgproc.rectangle(hsv, new Point(target.x, target.y), new Point(target.x + target.width, target.y + target.height), targetColor);

				System.out.print("S(" + contours.size() + "): ");
				for(MatOfPoint contour : contours){
					System.out.print(Imgproc.contourArea(contour) + ", ");
				}
				System.out.println();

				adjustValue = (target.x + target.width / 2) - width / 2;
				//Update network table
				sd.putNumber("adjustValue", adjustValue);
			}

			//Put modified cv image on cv source to be streamed
			cvSource.putFrame(hsv);
		}
	}

	public static void main(String[] args){
		Vision vision = new Vision();
		vision.processBlue();
	}
}
