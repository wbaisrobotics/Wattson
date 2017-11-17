package org.usfirst.frc.team4338.robot;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Shooter.java - robot component of the shooting mechanism.
 *
 * @author Aaron Shappell, edited by Orian Leitersdorf
 */
public class Shooter{

	private Timer timer = new Timer();

	private SpeedController wheel;
	private SpeedController feeder;
	private SpeedController agitator;

	private final double SHOOTING_SPEED = -0.75;
	private final double FEEDER_SPEED = -0.55;
	private final double AGITATOR_SPEED = 0.4;
	
	private final double UNJAMMING_SHOOTING_SPEED = 1;
	private final double UNJAMMING_FEEDER_SPEED = 0.55;
	private final double UNJAMMING_AGITATOR_SPEED = 0;
	
	private final double STOP_SPEED = 0;

	private boolean currentlyShooting = false;

	/**
	 * Default constructor.
	 * Initializes the wheel, feeder, and agitator motors.
	 */
	public Shooter(SpeedController wheel, SpeedController feeder, SpeedController agitator){
		this.wheel = wheel;
		this.feeder = feeder;
		this.agitator = agitator;
	}


	/**
	 * Stops the system
	 */
	public void stop(){
		wheel.set(STOP_SPEED);
		feeder.set(STOP_SPEED);
		agitator.set(STOP_SPEED);
		timer.cancel();
		currentlyShooting = false;
	}

	/**
	 * Prepares the shooting system by starting the wheel, and the after a delay set here shooting will begin
	 */
	public void prepareShooting() {
		if(!currentlyShooting) {
			wheel.set(SHOOTING_SPEED);
			timer.schedule(new TimerTask () {
				public void run() {
					startShooting();
				}
			}, 1000);
			currentlyShooting = true;
		}

	}

	/**
	 * Starts the feeder and the agitator
	 */
	private void startShooting() {
		feeder.set(FEEDER_SPEED);
		agitator.set(AGITATOR_SPEED);
	}
	
	/**
	 * Unjams the shooter
	 */
	public void unjamShooter() {
		stop();
		wheel.set(UNJAMMING_SHOOTING_SPEED);
		feeder.set(UNJAMMING_FEEDER_SPEED);
		agitator.set(UNJAMMING_AGITATOR_SPEED);
	}


}
