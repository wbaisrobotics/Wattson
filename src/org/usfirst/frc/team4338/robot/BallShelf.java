package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

/**
 * BallShelf.java - robot component of the ball shelf to more easily pick up ball from a hopper.
 *
 * @author Aaron Shappell
 */
public class BallShelf{
    private Servo leftPin;
    private Servo rightPin;

    /**
     * Default constructor.
     * Initializes servos to release the shelf pins.
     */
    public BallShelf(){
        leftPin = new Servo(5);
        rightPin = new Servo(6);
    }

    /**
     * Releases the ball shelf.
     */
    public void release(){
        leftPin.set(0.4f);
        rightPin.set(0.6f);
    }

    /**
     * Retries releasing the ball shelf if it gets stuck.
     */
    public void retry(){
    	leftPin.set(0.5f);
    	rightPin.set(0.5f);
    	Timer.delay(1f);
    	release();
    }
}
