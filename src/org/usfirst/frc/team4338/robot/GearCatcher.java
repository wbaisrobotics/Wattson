package org.usfirst.frc.team4338.robot;

public class GearCatcher{
    private Servo bottom;
    private Servo left;
    private Servo right;

    public GearCatcher(){
        bottom = new Servo(7);
        left = new Servo(8);
        right = new Servo(9);
    }

    public void close(){
        bottom.setAngle();
        left.setAngle();
        right.setAngle();
    }

    public void open(){
        bottom.setAngle();
        left.setAngle();
        right.setAngle();
    }
}
