package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;

public class GearCatcher{
    DigitalInput trigger;
    DigitalOutput reverseRelay;
    DigitalOutput forwardRelay;

    public GearCatcher(){
        trigger = new DigitalInput(0);
        reverseRelay = new DigitalOutput(1);
        forwardRelay = new DigitalOutput(2);
        close();
    }

    public void open(){
        forwardRelay.set(true);
        reverseRelay.set(false);
    }

    public void close(){
    	forwardRelay.set(false);
    	reverseRelay.set(true);
    }

    public boolean getTriggerState(){
        return trigger.get();
    }
}
