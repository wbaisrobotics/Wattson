package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 * GearCatcher.java - robot component for the gear catching system to catch and release gears.
 *
 * @author Aaron Shappell
 */
public class GearCatcher{
    private DigitalInput trigger;
    private DoubleSolenoid pistons;
    
    private boolean enabled = false;
    private double openTime = 5; //>3

    /**
     * Default constructor.
     * Initializes the plate trigger and pistons.
     */
    public GearCatcher(){
        trigger = new DigitalInput(0);
        pistons = new DoubleSolenoid(0, 7);
    }

    /**
     * Opens the gear catcher.
     */
    public void open(){
        pistons.set(DoubleSolenoid.Value.kReverse);
        openTime = Timer.getFPGATimestamp();
    }

    /**
     * Closes the gear catcher.
     */
    public void close(){
        pistons.set(DoubleSolenoid.Value.kForward);
    }

    /**
     * Gets the time the gear catcher has been open.
     *
     * @return the time since opened
     */
    public double getOpenTime(){
    	return openTime;
    }

    /**
     * Gets the state of the plate trigger.
     *
     * @return the plate trigger state
     */
    public boolean getTriggerState(){
        return trigger.get();
    }

    /**
     * Gets if the gear catcher is enabled or not.
     *
     * @return enabled or disabled state
     */
    public boolean isEnabled(){
    	return enabled;
    }

    /**
     * Enables the gear catcher.
     */
    public void enable(){
    	enabled = true;
    }

    /**
     * Disables the gear catcher.
     */
    public void disable(){
    	enabled = false;
    }
}
