package org.usfirst.frc.team4338.robot;

/*
	models a motor speed on the following equation:

			   s
		-----------------
			   -k(x - p)
		  1 + e
	
	k and p are based on the desired time to reach a speed
*/

public class LogisticMotorSpeedController{
	private double speed;
	private double time;
	private double k;
	private double offset;

	public LogisticMotorSpeedController(){
		speed = 1f;
		time = 1f;
		k = 10f;
		offset = 5f;
	}

	public void set(double speed, double time){
		this.speed = speed;
		this.time = time;
		k = 10f / time;
		offset = time / 2f;
	}

	public double getCurrentSpeed(double currentTime){
		return speed / (1f + Math.pow(Math.E, -k * (currentTime - offset)));
	}
}