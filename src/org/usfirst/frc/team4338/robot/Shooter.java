package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Victor;

public class Shooter{
    private Victor wheel;
    private Victor belt;

    public Shooter(){
        wheel = new Victor(6);
        belt = new Victor(7);
    }

    public void set(double wheelSpeed, double beltSpeed){
        wheel.set(wheelSpeed);
        belt.set(beltSpeed);
    }
}
