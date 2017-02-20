package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Servo;

public class BallShelf{
    private Servo leftPin;
    private Servo rightPin;

    public BallShelf(){
        leftPin = new Servo(); //ASSIGN VALUES!
        rightPin = new Servo();
    }

    public void release(){
        leftPin.setAngle(); //ASSIGN VALUES!
        rightPin.setAngle();
    }
}
