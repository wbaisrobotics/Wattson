package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Robot.java - The main class for the Robot, contains other components of the robot
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * @author Aaron Shappell
 */
public class Robot extends IterativeRobot {

	//Delay for teleop loop
	public final double PERIODIC_DELAY = 0.005f;

	//Controllers
	private Controller pilot;
	
	//Air compressor
	private Compressor compressor;

	//Drive
	private DoubleSolenoid leftShifter;
	private DoubleSolenoid rightShifter;
	private CANTalon leftCAN1;
	private CANTalon leftCAN2;
	private CANTalon rightCAN1;
	private CANTalon rightCAN2;
	private RobotDrive drive;

	//Robot components
	private BallShelf ballShelf;
	private Shooter shooter;
	private GearCatcher gearCatcher;
	
	//LED relays
	private DigitalOutput gearRelay;
	private DigitalOutput ballRelay;
	
	private ADXRS450_Gyro gyro;
	//private AnalogGyro gyro;
	private double angle;
	private double kp = 0.03f;
	
	private boolean autoStop = false;


	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		//Set initial state to gear side
		SmartDashboard.putBoolean("state", false);

		//Initialize controllers
		pilot = new Controller(0);
		
		//Initialize air compressor
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		

		//Initialize local motors (excluding those in other classes)
		leftShifter = new DoubleSolenoid(1, 6);
		rightShifter = new DoubleSolenoid(2, 5);
		leftCAN1 = new CANTalon(1);
		leftCAN2 = new CANTalon(2);
		rightCAN1 = new CANTalon(3);
		rightCAN2 = new CANTalon(4);
		//Initialize the drive system
		drive = new RobotDrive(leftCAN1, leftCAN2, rightCAN1, rightCAN2);
		drive.setExpiration(0.1f);

		//Initialize robot components
		ballShelf = new BallShelf();
		shooter = new Shooter();
		gearCatcher = new GearCatcher();
		
		//Initialize LED relays
		gearRelay = new DigitalOutput(1);
		ballRelay = new DigitalOutput(2);
		gearRelay.set(true);
		ballRelay.set(false);
		
		gyro = new ADXRS450_Gyro();
	}
	private void autoMove(double speed, double time){
		//Don't do anything if autoStop has been set
		if(!autoStop){
			gyro.reset();
			double start = Timer.getFPGATimestamp();

			//Move forward for the given amount of time
			while(Timer.getFPGATimestamp() - start < time){
				angle = gyro.getAngle();
				SmartDashboard.putNumber("angle", angle);
				//y + x, y - x
				drive.tankDrive(speed + angle * kp, speed - angle * kp);
				Timer.delay(PERIODIC_DELAY);
			}
		}

		drive.tankDrive(0f, 0f);
	}
	/**
	 * Places a gear on a peg and moves back.
	 * The peg must be through the gear.
	 */
	private void placeGear(){
		gearCatcher.open();
		Timer.delay(0.5f);
		autoMove(-0.75f, 1.25f);
		gearCatcher.close();
	}
	
	@Override
	public void teleopInit(){
		ballShelf.release();
		gyro.reset();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		SmartDashboard.putNumber("angle", gyro.getAngle());
		
		if(gearCatcher.isEnabled() && gearCatcher.getTriggerState()){
			placeGear();
			gearCatcher.disable();
		}
		

		gearRelay.set(true);
		ballRelay.set(false);
		
		//RightJoyX = X Direction Drive
		//RightJoyY = Y Direction Drive
		//RightTrigger = Shake
		//LeftTrigger = Prepare Shoot
		//LB = `Start Shooting
		//X = Close Gear Catcher
		//Y = Open Gear Catcher
		//A = Enable Gear Catcher
		
		//Driving
		double x = pilot.getRightJoyX();
		x = Math.signum(x) * Math.pow(x, 2);
		double y = pilot.getRightJoyY();
		y = Math.signum(y) * Math.pow(y, 2);
		
		shiftLow();
			
			
		if(pilot.getRightTrigger() > 0){ //Shake
				double turn = Math.sin(20f * Timer.getFPGATimestamp());
				//drive.tankDrive(0.7f * turn, 0.7f * -turn);
				x = 0.7f * turn; //Use x so the wheels turn opposite
				y = 0;
		} else{
				x *= 0.7f;
				y *= -0.8f;
			}
		
		drive.tankDrive((y - x), (y + x));
		
		//Shooting
		if(pilot.getLeftTrigger() > 0){
			shooter.setWheel(-0.75f);
			if(pilot.getButtonLB()){ //CHANGE THIS TO AUTO START WITH DELAY
				shooter.setFeeder(-0.55f);
				shooter.setAgitator(0.4f);
			} else{
				shooter.setFeeder(0f);
				shooter.setAgitator(0f);
			}
		} else{
			shooter.stop();
		}
		
		
		//Manual gear catcher controls if needed
		if(pilot.getButtonX()){
			gearCatcher.close();
		}
		if(pilot.getButtonY()){
			gearCatcher.open();
		}
		if(Timer.getFPGATimestamp() - gearCatcher.getOpenTime() > 2){
			gearCatcher.close();
		}
		//Enable the gear catcher
		if(pilot.getButtonA()){
			gearCatcher.enable();
		}

		Timer.delay(PERIODIC_DELAY);
	}

	private void shiftLow(){
		leftShifter.set(DoubleSolenoid.Value.kReverse);
		rightShifter.set(DoubleSolenoid.Value.kReverse);
	}
	
}
