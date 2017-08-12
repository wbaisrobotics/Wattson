import org.opencv.core.*;

/**
 * BoilerTarget.java - the definition of what a boiler target is.
 * Not done.
 *
 * @author Aaron Shappell
 */
public class BoilerTarget extends RotatedRect{
    private final double taretAspectRatio = 3.75f; //15in / 4in
    private boolean exists;
    private Scalar color;
    private Point[] verts;

    /**
     * Default constructor.
     *
     */
    public BoilerTarget(){
        exists = false;
        color = new Scalar(255, 255, 255);
        verts = new Point[4];
    }

    /**
     * Gets the horizontal offset of the target and normalizes it between -1 and 1.
     *
     * @param camWidth the width of the camera in pixels
     * @return the normalized horizontal offset
     */
    public double getNormalizedHorizontalOffset(double camWidth){
        return -1 + center.x * 2 / camWidth;
    }

    /**
     * Gets the horizontal offset of the target in pixels.
     *
     * @param camWidth the width of the camera in pixels.
     * @return the horizontal offset in pixels
     */
    public double getHorizontalOffset(double camWidth){
        return (center.x + size.width / 2) - camWidth / 2;
    }

    /**
     * Gets the aspect ratio of the target.
     *
     * @return the aspect ratio
     */
    public double getAspectRatio(){
        return size.width / size.height;
    }

    /**
     * Gets the color of the target.
     *
     * @return the target color
     */
    public Scalar getColor(){
        return color;
    }

    /**
     * Gets the array of points that makes up the target.
     *
     * @return the target vertices
     */
    public Point[] getVerts(){ //Change this
        return verts;
    }

    /**
     * Gets if the target exists or not.
     *
     * @return existence of the target
     */
    public boolean doesExist(){
        return exists;
    }
}
