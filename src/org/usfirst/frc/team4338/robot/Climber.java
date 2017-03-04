package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Victor;

public class Climber {
    private Victor motor1;
    private Victor motor2;

    public Climber(){
        motor1 = new Victor(0);
        motor2 = new Victor(1);
    }

    public void up(){
        motor1.set(1f);
        motor2.set(1f);
    }

    public void down(){
        motor1.set(-1f);
        motor2.set(-1f);
    }

    public void stop(){
        motor1.set(0f);
        motor2.set(0f);
    }
}
