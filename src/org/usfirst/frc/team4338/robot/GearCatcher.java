package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Servo;

public class GearCatcher{
    private Servo bottom;
    private Servo left;
    private Servo right;

    public GearCatcher(){
        bottom = new Servo(7);
        left = new Servo(8);
        right = new Servo(9);
    }

    public void close(){ //CHANGE THESE
        bottom.setAngle(0);
        left.setAngle(0);
        right.setAngle(0);
    }

    public void open(){ //CHANGE THESE
        bottom.setAngle(20);
        left.setAngle(20);
        right.setAngle(20);
    }
}
