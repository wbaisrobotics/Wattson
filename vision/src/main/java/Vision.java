import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;
import edu.wpi.cscore.*;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Vision{
	private NetworkTable sd;

	private UsbCamera camera;

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
    	NetworkTable.initialize();
    	//Get the SmartDashboard networktable
    	sd = NetworkTable.getTable("SmartDashboard");

		//Initialize Camera
    	camera = new UsbCamera("CoprocessorCamera", 0);
    	camera.setPixelFormat(VideoMode.PixelFormat.kMJPEG);
    	camera.setResolution(640, 480);
    	camera.setFPS(30);

    	//Initialize cv sink and source
    	cvSink = new CvSink("CV Image Grabber");
    	cvSink.setSource(camera);
    	cvSource = new CvSource("CV Image Source", VideoMode.PixelFormat.kMJPEG, 640, 480, 30);

    	//Initialize streams
    	rawStream = new MjpegServer("Raw Server", rawStreamPort);
    	rawStream.setSource(camera);
    	cvStream = new MjpegServer("CV Server", cvStreamPort);
    	cvStream.setSource(cvSource);
	}
	
	public void process(){
		// All Mats and Lists should be stored outside the loop to avoid allocations
    	// as they are expensive to create
		Mat frame = new Mat();
		Mat hsv = new Mat();

		while(true){
			// Grab a frame. If it has a frame time of 0, there was an error.
	      	// Just skip and continue
	      	long frameTime = cvSink.grabFrame(frame);
	      	if (frameTime == 0) continue;

	      	// Below is where you would do your OpenCV operations on the provided image
	      	// The sample below just changes color source to HSV
	      	Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);

	      	// Here is where you would write a processed image that you want to restreams
	      	// This will most likely be a marked up image of what the camera sees
	      	// For now, we are just going to stream the HSV image
	      	cvSource.putFrame(hsv);
		}
	}

	public static void main(String[] args){
		Vision vision = new Vision();
		vision.process();
	}
}