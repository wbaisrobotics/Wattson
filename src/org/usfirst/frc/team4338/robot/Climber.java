package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Victor;

/**
 * Climber.java - robot component of the climbing system to climb the rope.
 *
 * @author Aaron Shappell
 */
public class Climber {
    private Victor motor1;
    private Victor motor2;

    /**
     * Default constructor.
     * Initializes climbing motors.
     */
    public Climber(){
        motor1 = new Victor(0);
        motor2 = new Victor(1);
    }

    /**
     * Sets the motors to climb up.
     */
    public void up(){
        motor1.set(1f);
        motor2.set(1f);
    }

    /**
     * Sets the motors to climb down.
     */
    public void down(){
        motor1.set(-1f);
        motor2.set(-1f);
    }

    /**
     * Stops the motors.
     */
    public void stop(){
        motor1.set(0f);
        motor2.set(0f);
    }
}
