package org.usfirst.frc.team4338.robot;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.Servo;

/**
 * BallShelf.java - robot component of the ball shelf to more easily pick up ball from a hopper.
 *
 * @author Aaron Shappell
 */
public class BallShelf{
	
	private Timer timer = new Timer();
	
	private Servo leftPin;
	private Servo rightPin;

	/**
	 * Default constructor.
	 * Sets servos
	 */
	public BallShelf(Servo leftPin, Servo rightPin){
		this.leftPin = leftPin;
		this.rightPin = rightPin;
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
		timer.schedule(new TimerTask () {
			public void run() {
				release();
			}
		}, 1000);
	}
}
