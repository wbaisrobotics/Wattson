import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;
import edu.wpi.cscore.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.List;

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

		//Range to filter
		Scalar lower = new Scalar(110, 50, 50);
		Scalar upper = new Scalar(130, 255, 255);

		List<MatOfPoint> contours = new List<MatOfPoint>();
		Mat hierarchy = new Mat();
		Scalar contourColor = new Scalar(110, 255, 255);
		
		while(true){
			// Grab a frame. If it has a frame time of 0, there was an error.
			// Just skip and continue
			long frameTime = cvSink.grabFrame(frame);
			if (frameTime == 0) continue;
			
			//Convert to HSV for easier filtering
			Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);
			//Filter image by color
			Core.inRange(hsv, lower, upper, hsv);

			//TODO Maybe blur the image before looking for contours
			Imgproc.findContours(hsv, contours, hierarcy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			Imgproc.drawContours(hsv, contours, -1, contourColor);
			
			//Put modified cv image on cv source to be streamed
			cvSource.putFrame(hsv);
		}
	}

	public static void main(String[] args){
		Vision vision = new Vision();
		vision.process();
	}
}