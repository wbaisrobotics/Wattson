package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * GearCatcher.java - robot component for the gear catching system to catch and release gears.
 *
 * @author Aaron Shappell, edited by Orian Leitersdorf
 */
public class GearCatcher{

	private DigitalInput trigger;
	private DoubleSolenoid pistons;

	private boolean enabled = false;

	/**
	 * Default constructor.
	 * Initializes the plate trigger and pistons.
	 */
	public GearCatcher(DoubleSolenoid pistons, DigitalInput trigger){
		trigger = new DigitalInput(0);
		pistons = new DoubleSolenoid(0, 7);
	}

	/**
	 * Opens the gear catcher.
	 */
	public void open(){
		pistons.set(DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Closes the gear catcher.
	 */
	public void close(){
		pistons.set(DoubleSolenoid.Value.kForward);
	}
	
	/**
	 * Enables the trigger mechanism
	 */
	public void enable () {
		enabled = true;
	}
	
	/**
	 * Disables the trigger mechanism
	 */
	public void disable () {
		enabled = false;
	}

	/**
	 * Periodically called, opens if trigger it hit, closes if not (only happens when enabled)
	 */
	public void periodic() {
		if(enabled) {
			if(trigger.get()) {
				open();
				enabled = false;
			}
			else {
				close();
			}
		}
	}


}
