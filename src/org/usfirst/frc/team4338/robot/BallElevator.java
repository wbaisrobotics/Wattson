package org.usfirst.frc.team4338.robot;


import edu.wpi.first.wpilibj.SpeedController;


/**
 * BallElevator.java - robot component of the ball elevator to pick up balls from the floor
 *
 * @author Aaron Shappell, edited by Orian Leitersdorf
 */
public class BallElevator{
    private SpeedController sweeper;
    private SpeedController belt;
    
    private final double SWEEPER_SPEED = 0.75;
    private final double BELT_SPEED = -1;
    private final double STOP_SPEED = 0;

    /**
     * Default constructor.
     * Given sweeper and belt motors
     */
    public BallElevator(SpeedController sweeper, SpeedController belt){
        this.sweeper = sweeper;
        this.belt = belt;
    }

    /**
     * Starts the ball collection process
     */
    public void start() {
    		sweeper.set(SWEEPER_SPEED);
    		belt.set(BELT_SPEED);
    }
    
    /**
     * Stops the ball collection process
     */
    public void stop() {
    		sweeper.set(STOP_SPEED);
    		belt.set(STOP_SPEED);
    }
}
