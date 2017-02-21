package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Servo;

public class BallShelf{
    private Servo leftPin;
    private Servo rightPin;

    public BallShelf(){
        leftPin = new Servo(5);
        rightPin = new Servo(6);
    }
    
    public void upTest(){
    	leftPin.setAngle(20);
    	rightPin.setAngle(20);
    }

    public void release(){
        leftPin.setAngle(0); //ASSIGN VALUES!
        rightPin.setAngle(0);
    }
}
