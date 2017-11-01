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
 * @author Aaron Shappell, demo edited by Orian Leitersdorf
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
	private Shooter shooter;
	private GearCatcher gearCatcher;
	
	//LED relays
	private DigitalOutput gearRelay;
	private DigitalOutput ballRelay;
	
	private boolean allFunctionsEnabled = false;


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
		shooter = new Shooter();
		gearCatcher = new GearCatcher();
		
		//Initialize LED relays
		gearRelay = new DigitalOutput(1);
		ballRelay = new DigitalOutput(2);
		gearRelay.set(true);
		ballRelay.set(false);
		
	}


	/**
	 * This function is called periodically during operator control
	 * 
	 * 	RightJoyX = X Direction Drive
		RightJoyY = Y Direction Drive
		RightTrigger = Shake
		LeftTrigger = Prepare Shoot
		LB = Start Shooting
		X = Close Gear Catcher
		Y = Open Gear Catcher
	 * 
	 */
	@Override
	public void teleopPeriodic() {
		
		if (pilot.getButtonA() && pilot.getButtonB() && pilot.getButtonX()) {
			allFunctionsEnabled = true;
		}
		else if (pilot.getButtonY()) {
			allFunctionsEnabled = false;
		}
		
		gearRelay.set(true);
		ballRelay.set(false);
		

		
		//Driving
		double x = pilot.getRightJoyX();
		x = Math.signum(x) * Math.pow(x, 2);
		double y = pilot.getRightJoyY();
		y = Math.signum(y) * Math.pow(y, 2);
		
		shiftLow();
			
			
		if(pilot.getRightTrigger() > 0 && allFunctionsEnabled){ //Shake
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
		if(pilot.getLeftTrigger() > 0 && allFunctionsEnabled){
			shooter.setWheel(-0.75f);
			if(pilot.getButtonLB()){
				shooter.setFeeder(-0.55f);
				if(pilot.getButtonX()) {
					shooter.setAgitator(-0.4f);
				}
			} else{
				shooter.setFeeder(0f);
				shooter.setAgitator(0f);
			}
		} else{
			shooter.stop();
		}
		
		
		//Manual gear catcher controls
		if(pilot.getButtonX() && allFunctionsEnabled){
			gearCatcher.close();
		}
		if(pilot.getButtonY() && allFunctionsEnabled){
			gearCatcher.open();
		}

		Timer.delay(PERIODIC_DELAY);
	}

	private void shiftLow(){
		leftShifter.set(DoubleSolenoid.Value.kReverse);
		rightShifter.set(DoubleSolenoid.Value.kReverse);
	}
	
}
