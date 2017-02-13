package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Victor;

public class BallElevator{
    private Victor sweeper;
    private Victor elevator;

    public BallElevator(){
        sweeper = new Victor(0);
        elevator = new Victor(1);
    }

    public void set(double sweeperSpeed, double elevatorSpeed){
        sweeper.set(sweeperSpeed);
        elevator.set(elevatorSpeed);
    }
}
