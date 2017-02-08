import org.opencv.core.*;

public class BoilerTarget extends RotatedRect{
    private final double taretAspectRatio = 3.75f; //15in / 4in
    private boolean exists;
    private Scalar color;
    private Point[] verts;

    public BoilerTarget(){
        exists = false;
        color = new Scalar(255, 255, 255);
        verts = new Point[4];
    }

    public double getNormalizedHorizontalOffset(double camWidth){
        return -1 + center.x * 2 / camWidth;
    }

    public double getHorizontalOffset(double camWidth){
        return (center.x + size.width / 2) - camWidth / 2;
    }

    public double getAspectRatio(){
        return size.width / size.height;
    }

    public Scalar getColor(){
        return color;
    }

    public Point[] getVerts(){ //Change this
        return verts;
    }

    public boolean doesExist(){
        return exists;
    }
}
