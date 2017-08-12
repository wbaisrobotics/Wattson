package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Victor;

/**
 * BallElevator.java - robot component of the ball elevator to pick up balls from the floor
 *
 * @author Aaron Shappell
 */
public class BallElevator{
    private Victor sweeper;
    private CANTalon belt;

    /**
     * Default constructor.
     * Initialized sweeper and belt motors.
     */
    public BallElevator(){
        sweeper = new Victor(2);
        belt = new CANTalon(5);
    }

    /**
     * Sets the speed of the sweeper and belt motors.
     *
     * @param sweeperSpeed the speed of the sweeper motor
     * @param beltSpeed the speed of the belt motor
     */
    public void set(double sweeperSpeed, double beltSpeed){
        sweeper.set(sweeperSpeed);
        belt.set(beltSpeed);
    }
}
