package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

public class Shooter{
    private Victor wheel;
    private Victor feeder;
    private CANTalon agitator;
    
    private double start;

    public Shooter(){
        wheel = new Victor(3);
        feeder = new Victor(4);
        agitator = new CANTalon(5);
    }
    
    public void stop(){
    	wheel.set(0f);
    	feeder.set(0f);
    	agitator.set(0f);
    }
    
    public void setWheel(double wheelSpeed){
    	wheel.set(wheelSpeed);
    }
    
    public void setFeeder(double feederSpeed){
    	feeder.set(feederSpeed);
    }

    public void setAgitator(double agitateSpeed){
        agitator.set(agitateSpeed);
    }
    
    public double getWheelSpeed(){
    	return wheel.get();
    }
    
    public void resetDelay(){
    	start = Timer.getFPGATimestamp();
    }
    
    public boolean canFeed(){
    	return Timer.getFPGATimestamp() - start > 2f;
    }
}
