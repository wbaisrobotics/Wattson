package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Victor;

public class BallElevator{
    private Victor sweeper;
    private CANTalon belt;

    public BallElevator(){
        sweeper = new Victor(2);
        belt = new CANTalon(5);
    }

    public void set(double sweeperSpeed, double beltSpeed){
        sweeper.set(sweeperSpeed);
        belt.set(beltSpeed);
    }
}
