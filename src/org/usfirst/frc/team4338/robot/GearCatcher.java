package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class GearCatcher{
    DigitalInput trigger;
    DoubleSolenoid pistons;

    public GearCatcher(){
        trigger = new DigitalInput(0);
        pistons = new DoubleSolenoid(0, 1);
    }

    public void open(){
        pistons.set(DoubleSolenoid.Value.kReverse);
    }

    public void close(){
        pistons.set(DoubleSolenoid.Value.kForward);
    }

    public boolean getTriggerState(){
        return trigger.get();
    }
}
