package vision.src.main.java;

import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;
import edu.wpi.cscore.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;

/*

  |\      _,,,,--,,_
  /,`.-'`'    -,  ;-;,
 |,4-  ) ),,__ ) /;  ;;  Code cat is tired
'---''(.'--'  (.'`.) `'

~meow

*/

/**
 * Vision.java - Handles the vision processing to look for the retrorelfective tape target on the tower.
 *
 * @author Aaron Shappell
 */
public class Vision{
	private NetworkTable sd;

	//Diagonal fov of 68.5 degrees
	private UsbCamera gearCamera;
	private UsbCamera ballCamera;
	private int width = 640; //Probably change to 320
	private int height = 480; //Probably change to 240
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
	
	private boolean state = false; //False=gear, true=ball

	/**
	 * Default constructor.
	 *
	 */
	public Vision(){
		// Loads our OpenCV library. This MUST be included
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		NetworkTable.setClientMode();
		// Set your team number here
		NetworkTable.setTeam(4338);
		//Get the SmartDashboard networktable
		sd = NetworkTable.getTable("SmartDashboard");
		//Set default filter values FOR TESTING!
		sd.putNumber("lowerH", 110);
		sd.putNumber("lowerS", 10);
		sd.putNumber("lowerV", 100);
		sd.putNumber("upperH", 130);
		sd.putNumber("upperS", 255);
		sd.putNumber("upperV", 255);

		//Initialize cameras
		gearCamera = new UsbCamera("Gear Camera", 1); //0 or 1?
		gearCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, width, height, fps);
		gearCamera.setBrightness(0);
		gearCamera.setExposureManual(0);
		ballCamera = new UsbCamera("Ball Camera", 0); //0 or 1?
		ballCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, width, height, fps);
		ballCamera.setBrightness(0);
		ballCamera.setExposureManual(0);

		//Initialize cv sink and source to use the gear camera
		cvSink = new CvSink("CV Image Grabber");
		cvSink.setSource(gearCamera);
		cvSource = new CvSource("CV Image Source", VideoMode.PixelFormat.kMJPEG, width, height, fps);

		//Initialize streams to use gear camera
		sd.putBoolean("state", false);
		rawStream = new MjpegServer("Raw Server", rawStreamPort);
		rawStream.setSource(gearCamera);
		cvStream = new MjpegServer("CV Server", cvStreamPort);
		cvStream.setSource(cvSource);
	}

	/**
	 * Updates the direction state from the SmartDashboard.
	 */
	private void updateState(){
		try{
			state = sd.getBoolean("state", false); //Default to processGear
		} catch(TableKeyNotDefinedException e){
			System.out.println("Error: key \"state\" not found");
			e.printStackTrace();
		}
	}

	/**
	 * Manages which vision process is running based on the direction state.
	 */
	public void process(){
		//Always running (change this?)
		while(true){
			//Update the direction state
			updateState();
			if(state){ //Process Boiler
				//Set the camera stream to the ball camera
				if(rawStream.getSource() != ballCamera){
					rawStream.setSource(ballCamera);
					cvSink.setSource(ballCamera);
				}
				//run the boiler target vision process (not done)
				//processBoiler();
			} else{ //Process Gear
				//Set the camera stream to the gear camera
				if(rawStream.getSource() != gearCamera){
					rawStream.setSource(gearCamera);
					cvSink.setSource(gearCamera);
				}
				//run the gear target vision process
				processGear();
			}
		}
	}

	/**
	 * Sorts an array of contours by area from largest to smallest.
	 * Sorting from largest to smallest eliminates checking contours from the result of noise.
	 *
	 * @param contours the list of contours to sort
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

	/**
	 * The gear vision process.
	 * Searches for the gear target on the tower by looking for rectangles of the correct color and aspect ratio.
	 */
	public void processGear(){
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

		double adjustValue;

		//Processing loop
		while(!state){
			// Grab a frame. If it has a frame time of 0, there was an error.
			// Just skip and continue
			long frameTime = cvSink.grabFrame(frame);
			if (frameTime == 0) continue;

			lower = new Scalar(sd.getNumber("lowerH", 0), sd.getNumber("lowerS", 0), sd.getNumber("lowerV", 0));
			upper = new Scalar(sd.getNumber("upperH", 0), sd.getNumber("upperS", 0), sd.getNumber("upperV", 0));
			//lower = new Scalar(110, 10, 110);
			//upper = new Scalar(130, 255, 255);

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
				target = findGearTarget(contours);
				Imgproc.drawContours(hsv, contours, -1, contourColor);

				if(target != null){ //Draw target if it exists and update adjust value
					Imgproc.rectangle(hsv, new Point(target.x, target.y), new Point(target.x + target.width, target.y + target.height), targetColor);
					//adjustValue = (target.x + target.width / 2) - width / 2;
					//adjustValue = -1f + (target.x + width / 2f) * 2f / width;

					//Calculate the adjust value
					double r = 290f / target.height;
					int offset = (target.x + target.width / 2) - width / 2;
					double offsetFeet = offset * 5f / 12f / target.height;
					adjustValue = Math.toDegrees(offsetFeet / r);
					System.out.println(adjustValue);
				} else{
					System.out.println("Error: could not find gear target");
					adjustValue = -1000; //This means does not exist
				}

				//Update network table
				sd.putNumber("adjustValue", adjustValue);
			}

			//Put modified cv image on cv source to be streamed
			cvSource.putFrame(hsv);
			//Update the state
			updateState();
		}
	}

	/**
	 * Searches a list of contours for the gear target on the tower by looking for the right aspect ratios.
	 * If a target is not found null is returned.
	 *
	 * @param contours list of contours to search
	 * @return the gear target
	 */
	private Rect findGearTarget(ArrayList<MatOfPoint> contours){
		//Aspect ratio to look for
		double targetAspectRatio = 2f / 5f;
		double foundAspectRatio;
		//Give an error range as the aspect ratio won't be exact
		double fudge = 0.2f;
		//Targets
		Rect target = null;
		Rect left = null;
		Rect right = null;

		//Iterate through contour list
		for(MatOfPoint contour : contours){
			//Get bounding rect of the current contour
			Rect temp = Imgproc.boundingRect(contour);
			//Calculate its aspect ratio
			foundAspectRatio = (double) temp.width / (double) temp.height;
			//Check if it is a correct aspect ratio
			if(foundAspectRatio < targetAspectRatio * (1f + fudge) && foundAspectRatio > targetAspectRatio * (1f - fudge)){
				if(left == null){ //If no left tape has been found save the left one
					left = temp;
				} else if(right == null){ //if no right tape has been found save the right one
					right = temp;
				} else{ //Once both tapes have been found stop searching
					break;
				}
			}
		}
		
		//Create the target if the left and right tape is found
		if(left != null && right != null){
			target = new Rect(new Point(left.x, left.y), new Point(right.x + right.width, right.y + right.height));
		}

		return target;
	}

	/**
	 * The boiler vision process.
	 * Searches for the boiler target by looking for rectangles of the correct color and aspect ratio.
	 *
	 * NOT DONE OR CURRENTLY USED.
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

		double adjustValue;

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

			//Find target
			if(contours.size() > 0){
				//Merge sort the contours
				sortContours(contours);
				//Find the matching target
				target = findBoilerTarget(contours);

				//Draw findings
				Imgproc.drawContours(hsv, contours, -1, contourColor);

				/*
				if(target.doesExist()){ //Draw target if it exists
					for(int i = 0; i < 4; i++){
						//Fix this line, targetVerts out of scope!
						Imgproc.line(hsv, targetVerts[i], targetVerts[(i + 1) % 4], target.getColor());
					}
				}
				*/

				//Update network table with the target data
				sd.putBoolean("targetExists", target.doesExist());
				sd.putNumber("adjustValue", target.getNormalizedHorizontalOffset(width));

				//Imgproc.rectangle(hsv, new Point(target.x, target.y), new Point(target.x + target.width, target.y + target.height), targetColor);

				//adjustValue = (target.x + target.width / 2) - width / 2;
				//Update network table
				//sd.putNumber("adjustValue", adjustValue);
			}

			//Put modified cv image on cv source to be streamed
			cvSource.putFrame(hsv);
		}
	}

	/**
	 * Searches a list of contours for a boiler target.
	 * NOT DONE OR CURRENTLY USED.
	 *
	 * @param contours the list of contours to search
	 * @return the boiler target
	 */
	private BoilerTarget findBoilerTarget(ArrayList<MatOfPoint> contours){
		BoilerTarget target = new BoilerTarget();
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

	/**
	 * Gets the largest contour of a list of contours.
	 * NOT DONE OR CURRENTLY USED
	 *
	 * @param contours the list of contours to search
	 * @return the larget contour in the list
	 */
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
		vision.process();
	}
}
