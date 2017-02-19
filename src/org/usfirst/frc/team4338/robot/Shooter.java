package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Victor;

public class Shooter{
    private Victor wheel;
    private Victor feeder;

    public Shooter(){
        wheel = new Victor(3);
        feeder = new Victor(4);
    }

    public void set(double wheelSpeed, double feederSpeed){
        wheel.set(wheelSpeed);
        feeder.set(feederSpeed);
    }
}
