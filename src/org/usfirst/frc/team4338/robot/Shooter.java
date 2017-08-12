package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

/**
 * Shooter.java - robot component of the shooting mechanism.
 *
 * @author Aaron Shappell
 */
public class Shooter{
    private Victor wheel;
    private Victor feeder;
    private CANTalon agitator;
    
    private double start;

    /**
     * Default constructor.
     * Initializes the wheel, feeder, and agitator motors.
     */
    public Shooter(){
        wheel = new Victor(3);
        feeder = new Victor(4);
        agitator = new CANTalon(5);
    }

    /**
     * Stops the shooting motors.
     */
    public void stop(){
    	wheel.set(0f);
    	feeder.set(0f);
    	agitator.set(0f);
    }

    /**
     * Sets the speed of the wheel motor.
     *
     * @param wheelSpeed the wheel motor speed
     */
    public void setWheel(double wheelSpeed){
    	wheel.set(wheelSpeed);
    }

    /**
     * Sets the speed of the feeder motor.
     *
     * @param feederSpeed the feeder motor speed
     */
    public void setFeeder(double feederSpeed){
    	feeder.set(feederSpeed);
    }

    /**
     * Sets the speed of the agitator motor.
     *
     * @param agitateSpeed the agitator motor speed
     */
    public void setAgitator(double agitateSpeed){
        agitator.set(agitateSpeed);
    }

    /**
     * Gets the speed of the wheel motor.
     *
     * @return the wheel motor speed
     */
    public double getWheelSpeed(){
    	return wheel.get();
    }

    /**
     * Resets the time delay for shooting.
     */
    public void resetDelay(){
    	start = Timer.getFPGATimestamp();
    }

    /**
     * Gets whether the shooting mechanism can start feeding balls into the fly wheel.
     *
     * @return the feed state
     */
    public boolean canFeed(){
    	return Timer.getFPGATimestamp() - start > 2f;
    }
}
