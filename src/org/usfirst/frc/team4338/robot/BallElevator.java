package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Victor;

public class BallElevator{
    private Victor sweeper;
    private Victor belt;

    public BallElevator(){
        sweeper = new Victor(5);
        belt = new Victor(4);
    }

    public void set(double sweeperSpeed, double beltSpeed){
        sweeper.set(sweeperSpeed);
        belt.set(beltSpeed);
    }
}
