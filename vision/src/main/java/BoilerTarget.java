import org.opencv.core.*;

public class BoilerTarget extends RotatedRect{
    private boolean exists;
    private Scalar color;

    public BoilerTarget(){
        exists = false;
        color = new Scalar(255, 255, 255);
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

    public boolean exists(){
        return exists;
    }
}
