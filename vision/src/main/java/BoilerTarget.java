public class BoilerTarget extends RotatedRect{
    private boolean exists;

    private Scalar color;

    public BoilerTarget(){
        exists = false;
        color = new Scalar(255, 255, 255);
    }

    public double getNormalizedHorizontalOffset(double camWidth){
        return -1 + x * 2 / camWidth;
    }

    public double getHorizontalOffset(double camWidth){
        return (x + width / 2) - camWidth / 2;
    }

    public boolean exists(){
        return exists;
    }
}
