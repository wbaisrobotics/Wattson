package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
 * @author Aaron Shappell, edited/rewritten by Orian Leitersdorf
 */
public class Robot extends IterativeRobot {

	/**Robot components**/

	public final double PERIODIC_DELAY = 0.005f;

	//Air compressor
	private Compressor compressor;

	//Gyro
	private ADXRS450_Gyro gyro;
	//private double kp = 0.03f;

	//Speed controllers, Servos

	private Victor victor0; /** Climber 1 **/
	private Victor victor1; /** Climber 2 **/

	private Victor victor3; /** Shooter Wheel **/
	private Victor victor4; /** Shooter feeder **/
	private CANTalon canTalon6; /** Shooter Agitator **/

	private CANTalon canTalon5; /** Ball Elevator Belt **/
	private Victor victor2; /** Ball Elevator Sweeper **/

	private Servo servo5; /** Ball Shelf Left Pin **/
	private Servo servo6; /** Ball Shelf Right Pin **/
	private Servo servo8; /** Camera Mounted **/

	private CANTalon canTalon1; /** Left Drive 1 **/
	private CANTalon canTalon2; /** Left Drive 2 **/
	private CANTalon canTalon3; /** Right Drive 1 **/
	private CANTalon canTalon4; /** Right Drive 2 **/
	
	//Sensors
	private DigitalInput input0; //Gear Catcher Trigger
	
	//Pneumatics
	private DoubleSolenoid doubleSolenoid07; //Gear catcher
	private DoubleSolenoid doubleSolenoid16; //Left Shifter
	private DoubleSolenoid doubleSolenoid25; //Right Shifter

	//Drive
	private RobotDrive drive;

	//Robot systems
	private BallElevator ballElevator;
	private BallShelf ballShelf;
	private Shooter shooter;
	private GearCatcher gearCatcher;
	private Climber climber;

	//LED relays
	private DigitalOutput gearRelay;
	private DigitalOutput ballRelay;

	/**Autonomous**/

	//Choices names
	private static final String AUTO_CHOICE_DEFAULT = "DEFAULT";
	private static final String AUTO_CHOICE_FOLLOW_TAPE_TEST = "FOLLOW_TAPE_TEST";

	//The currently selected autonomous mode, according to the choice's name
	String autoSelected;
	//The chooser that is sent to the Smart Dashboard for the auto modes
	SendableChooser<String> chooser = new SendableChooser<>();

	/**Teleoperated*/

	//Controllers
	private JoystickController pilot;
	private JoystickController copilot;

	private boolean state; //false if gear side, true if ball side



	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		/** Autonomous Options **/
		chooser.addDefault("Default", AUTO_CHOICE_DEFAULT); 
		chooser.addObject("Follow Tape Test", AUTO_CHOICE_FOLLOW_TAPE_TEST);
		SmartDashboard.putData("Auto choices", chooser);

		/** Speed controllers, servos, sensors, outputs initialization **/

		victor0 = new Victor (0);
		victor1 = new Victor (1);
		victor2 = new Victor (2);
		victor3 = new Victor (3);
		victor4 = new Victor (4);

		canTalon1 = new CANTalon(1);
		canTalon2 = new CANTalon(2);
		canTalon3 = new CANTalon(3);
		canTalon4 = new CANTalon(4);
		canTalon5 = new CANTalon(5);
		canTalon6 = new CANTalon(6);

		servo5 = new Servo (5);
		servo6 = new Servo (6);
		servo8 = new Servo (8);
		
		input0 = new DigitalInput(0);
		
		gearRelay = new DigitalOutput(1);
		ballRelay = new DigitalOutput(2);

		/** Pneumatics **/
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		doubleSolenoid07 = new DoubleSolenoid(0, 7);
		doubleSolenoid16 = new DoubleSolenoid(1, 6);
		doubleSolenoid25 = new DoubleSolenoid(2, 5);

		/** Gyro **/
		gyro = new ADXRS450_Gyro();
		

		/** Drive System **/
		drive = new RobotDrive(canTalon1, canTalon2, canTalon3, canTalon4);
		drive.setExpiration(0.1f);
		
		
		/** Robot Systems **/
		ballElevator = new BallElevator(victor2, canTalon5);
		ballShelf = new BallShelf(servo5, servo6);
		shooter = new Shooter(victor3, victor4, canTalon6);
		gearCatcher = new GearCatcher(doubleSolenoid07, input0);
		climber = new Climber(victor0, victor1);
		
	}
	
	
	
	/**---------------------------- Autonomous ----------------------------**/

	
	/**
	 * Called in the beginning of autonomous it prepares the robot and the raspberry pi
	 */
	public void autonomousInit() {
		
		autoSelected = chooser.getSelected(); //Retrieves the selected mode
		
		System.out.println("Auto selected: " + autoSelected);
		
		ballShelf.release();
		gyro.reset();
		
		/** Raspberry Pi Vision Configurations **/
		
		/* Values for the color filtering */
		SmartDashboard.putNumber("hueLow", 75);
		SmartDashboard.putNumber("hueHigh", 105);
		SmartDashboard.putNumber("saturationLow", 200);
		SmartDashboard.putNumber("saturationHigh", 255);
		SmartDashboard.putNumber("brightnessLow", 245);
		SmartDashboard.putNumber("brightnessHigh", 255);

		/* Tells Raspberry Pi to start processing */
		SmartDashboard.putBoolean("run", true);
		SmartDashboard.putBoolean("end", false);
		
		
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	public void autonomousPeriodic() {
		if (autoSelected.equals(AUTO_CHOICE_FOLLOW_TAPE_TEST)) {
			followTapePeriodic();
		}
		else if (autoSelected.equals(AUTO_CHOICE_DEFAULT)) {
			
		}
		else {
			System.out.println("Error in finding autonomous mode");
		}
	}

	/**
	 * Should be called periodically when the robot wants to follow tape (Currently only testing)
	 */
	private void followTapePeriodic () {
		
	}
	
	
	
	/**---------------------------- Teleoperated ----------------------------**/
	
	

	@Override
	public void teleopInit(){

		//Initialize Controllers
		pilot = new JoystickController(0);
		copilot = new JoystickController(1);

		ballShelf.release();
		gyro.reset();
		
		updateRelays();
		
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		/**--------------- PILOT CONTROLS ---------------**/
		
		
		/** Driving - State controls **/

		if(pilot.getXButton()) { //If x button is pressed, gear side becomes the front
			state = false;
		}
		else if (pilot.getYButton()) { //If y button is pressed, ball side becomes the front
			state = true;
		}
		updateRelays();

		/** Driving - Movement controls **/ 

		double x = pilot.getRightJoyXAxis();
		double y = pilot.getRightJoyYAxis();
		x = Math.signum(x) * Math.pow(x, 2);
		y = Math.signum(y) * Math.pow(y, 2);

		y *= state? 1:-1; //Changes according to current state

		if(pilot.getRightTrigger() > 0){ //Shake
			double turn = Math.sin(20f * Timer.getFPGATimestamp());
			x = 0.7 * turn; //Use x so the wheels turn opposite
			y = 0;
		} 
		else {

			if(pilot.getLeftTrigger() > 0){ //High gear driving
				
				shiftHigh();
				
				x *= getXScale(y);
				
			} 
			else{ //Low gear driving

				shiftLow();
				
				x *= 0.7;
				y *= 0.8;
				
			}
		}
		
		drive.tankDrive(y - x, y + x);
		
		

		/**--------------- COPILOT CONTROLS ---------------**/
		
		
		/** Ball Elevator **/
		
		if(copilot.getAButton()){ //If a is pressed, start collecting balls
			ballElevator.start();
		} 
		else if (copilot.getBButton()){ //If b is pressed, stop collecting balls
			ballElevator.stop();
		}

		/** Shooting **/
		if(copilot.getLeftTrigger() > 0){ //If left trigger is pressed, start shooting
			shooter.prepareShooting();
		} 
		else if(copilot.getLSButton()){ //If the left stick is pressed, unjam the shooter
			shooter.unjamShooter();
		} 
		else if(copilot.getLBButton()){ //If the left button (above the trigger) is pressed, stop shooting
			shooter.stop();
		}

		//Climbing
		switch (copilot.getPOVEnum()) {
		case NORTH:
			climber.up();
		case SOUTH:
			climber.down();
		default:
			climber.stop();
		}

		//Retry shelf extension
		if(copilot.getRSButton()){ //If the right stick is pressed, retry the shelf extension
			ballShelf.retry();
		}

		//Gear catching controls
		if(copilot.getXButton()){
			gearCatcher.close();
		}
		else if(copilot.getYButton()){
			gearCatcher.open();
		}
		else if(copilot.getStartButton()) {
			gearCatcher.enable();
		}

		Timer.delay(PERIODIC_DELAY);

	}

	/**
	 * Gets an x value from a given y value on a logistic function.
	 * Limits turning when moving forward faster.
	 *
	 * @param y	the y value of a controller joystick
	 */
	private double getXScale(double y){
		//Logistic function to limit turning speed based on forward speed
		return 1 - 0.5f / (1 + Math.pow(Math.E, -10 * (Math.abs(y) - 0.5f)));
	}

	/**
	 * Shifts the drive gears high.
	 */
	private void shiftHigh(){
		doubleSolenoid16.set(DoubleSolenoid.Value.kForward);
		doubleSolenoid25.set(DoubleSolenoid.Value.kForward);
	}

	/**
	 * Shifts the drive gears low.
	 */
	private void shiftLow(){
		doubleSolenoid16.set(DoubleSolenoid.Value.kReverse);
		doubleSolenoid25.set(DoubleSolenoid.Value.kReverse);
	}
	
	private void updateRelays () {
		if(state) {
			gearRelay.set(false);
			ballRelay.set(true);
		}
		else {
			gearRelay.set(true);
			ballRelay.set(false);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		
	}
}
