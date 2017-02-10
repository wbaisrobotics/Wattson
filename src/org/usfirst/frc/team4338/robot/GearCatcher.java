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

    public void close(){
        bottom.setAngle(30);
        left.setAngle(10);
        right.setAngle(20);
    }

    public void open(){
        bottom.setAngle(90);
        left.setAngle(80);
        right.setAngle(88);
    }
}
