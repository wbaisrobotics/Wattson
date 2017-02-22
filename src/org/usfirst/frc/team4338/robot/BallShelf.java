package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

public class BallShelf{
    private Servo leftPin;
    private Servo rightPin;

    public BallShelf(){
        leftPin = new Servo(5);
        rightPin = new Servo(6);
    }
    
    public void release(){
        leftPin.set(0.4f);
        rightPin.set(0.6f);
    }
    
    public void retry(){
    	leftPin.set(0.5f);
    	rightPin.set(0.5f);
    	Timer.delay(1f);
    	release();
    }
}
