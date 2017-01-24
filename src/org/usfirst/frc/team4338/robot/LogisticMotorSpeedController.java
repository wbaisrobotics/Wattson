/*
	models a motor speed on the following equation:

			   s
		-----------------
			   -k(x - p)
		  1 + e
	
	k and p are based on the desired time to reach a speed
*/

public class LogisticMotorSpeedContoller{
	private double speed = 1f;
	private double time = 1f;
	private double k = 10f;
	private double offset = 5f;

	public void setTime(double time){
		this.time = time;
		k = 10f / time;
		offset = time / 2f;
	}

	public double getCurrentSpeed(double currentTime){
		return speed / (1f + Math.pow(Math.E, -k(currentTime - offset)));
	}

	public double getTime(){
		return time;
	}
}