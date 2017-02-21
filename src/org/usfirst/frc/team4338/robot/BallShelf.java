package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Servo;

public class BallShelf{
    private Servo leftPin;
    private Servo rightPin;

    public BallShelf(){
        leftPin = new Servo(5);
        rightPin = new Servo(6);
    }
    
    public void release(){
        leftPin.set(0.4);
        rightPin.set(0.6);
    }
}
