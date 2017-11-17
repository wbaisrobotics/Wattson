package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Climber.java - robot component of the climbing system to climb the rope.
 *
 * @author Aaron Shappell, edited by Orian Leitersdorf
 */
public class Climber {
	
    private SpeedController firstMotor;
    private SpeedController secondMotor;
    
    private static final double UP_SPEED = 1;
    private static final double DOWN_SPEED = -1;
    private static final double STOP_SPEED = 0;

    /**
     * Default constructor.
     * Initializes climbing motors.
     */
    public Climber(SpeedController firstMotor, SpeedController secondMotor){
        this.firstMotor = firstMotor;
        this.secondMotor = secondMotor;
    }

    /**
     * Sets the motors to climb up.
     */
    public void up(){
        firstMotor.set(UP_SPEED);
        secondMotor.set(UP_SPEED);
    }

    /**
     * Sets the motors to climb down.
     */
    public void down(){
        firstMotor.set(DOWN_SPEED);
        secondMotor.set(DOWN_SPEED);
    }

    /**
     * Stops the motors.
     */
    public void stop(){
        firstMotor.set(STOP_SPEED);
        secondMotor.set(STOP_SPEED);
    }
}
