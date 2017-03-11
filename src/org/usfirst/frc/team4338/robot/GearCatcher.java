package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;

public class GearCatcher{
    DigitalInput trigger;
    DigitalOutput reverseRelay;
    DigitalOutput forwardRelay;

    private boolean enabled = false;
    private double openTime = 5;

    public GearCatcher(){
        trigger = new DigitalInput(0);
        reverseRelay = new DigitalOutput(1);
        forwardRelay = new DigitalOutput(2);
        close();
    }

    public void open(){
        forwardRelay.set(true);
        reverseRelay.set(false);
        openTime = Timer.getFPGATimestamp();
    }

    public void close(){
        forwardRelay.set(false);
        reverseRelay.set(true);
    }

    public double getOpenTime(){
        return openTime;
    }

    public boolean getTriggerState(){
        return trigger.get();
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void enable(){
        enabled = true;
    }

    public void disable(){
        enabled = false;
    }
}