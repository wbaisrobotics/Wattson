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
 * @author Aaron Shappell
 */
public class Robot extends IterativeRobot {
	//Red autonomous choices
	final String nothing = "Nothing";
	final String redA0 = "Red A0";
	final String redA1 = "Red A1";
	final String redB0 = "Red B0";
	final String redB1 = "Red B1";
	final String redB2 = "Red B2";
	final String redC0 = "Red C0";
	final String redC1 = "Red C1";
	final String redC2 = "Red C2";
	final String blueA2 = "Blue A2";

	//Selected autonomous choice
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	//Delay for teleop loop
	public final double PERIODIC_DELAY = 0.005f;

	//Controllers
	private Controller pilot;
	private Controller copilot;
	
	//Air compressor
	private Compressor compressor;
	
	//Gyro
	private ADXRS450_Gyro gyro;
	//private AnalogGyro gyro;
	private double angle;
	private double kp = 0.03f;

	//Drive
	private DoubleSolenoid leftShifter;
	private DoubleSolenoid rightShifter;
	private CANTalon leftCAN1;
	private CANTalon leftCAN2;
	private CANTalon rightCAN1;
	private CANTalon rightCAN2;
	private RobotDrive drive;

	//Robot components
	private BallElevator ballElevator;
	private BallShelf ballShelf;
	private Shooter shooter;
	private GearCatcher gearCatcher;
	private Climber climber;
	
	//LED relays
	private DigitalOutput gearRelay;
	private DigitalOutput ballRelay;
	
	//Direction state, false = gear side, true = ball side
	private boolean state = false;
	//Button debouncing
	private double lastDebounceTime = 0f;
	private double debounceDelay = 0.05f;
	private boolean toggleState = false;
	private boolean lastToggleState = false;

	//Whether the robot has exited an autonomous function or not (useful for errors and preventing damage)
	private boolean autoStop = false;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Add autonomous options to the Smartdashboard (on pilot computer)
		chooser.addDefault("Nothing", nothing);
		chooser.addObject("Red A0", redA0);
		chooser.addObject("Red A1", redA1);
		chooser.addObject("Red B0", redB0);
		chooser.addObject("Red B1", redB1);
		chooser.addObject("Red B2", redB2);
		chooser.addObject("Red C0", redC0);
		chooser.addObject("Red C1", redC1);
		chooser.addObject("Red C2", redC2);
		chooser.addObject("Blue A2", blueA2);
		SmartDashboard.putData("Auto choices", chooser);
		
		//Set initial state to gear side
		SmartDashboard.putBoolean("state", false);

		//Initialize controllers
		pilot = new Controller(0);
		copilot = new Controller(1);
		
		//Initialize air compressor
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		
		//Initialize gyro
		gyro = new ADXRS450_Gyro();
		//gyro = new AnalogGyro(0);

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
		ballElevator = new BallElevator();
		ballShelf = new BallShelf();
		shooter = new Shooter();
		gearCatcher = new GearCatcher();
		climber = new Climber();
		
		//Initialize LED relays
		gearRelay = new DigitalOutput(1);
		ballRelay = new DigitalOutput(2);
		gearRelay.set(true);
		ballRelay.set(false);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		//Get selected autonomous option from the Smartdashboard
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);

		//Reset the autoEnd boolean
		SmartDashboard.putBoolean("autoEnd", false);
		//Release the ball shelf
		ballShelf.release();
		//Reset the gyro to 0
		gyro.reset();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Run the selected autonomous method
		switch (autoSelected) {
			case nothing: autoEnd(); break;
			case redA0: autoRedA0(); break;
			case redA1: autoRedA1(); break;
			case redB0: autoRedB0(); break;
			case redB1: autoRedB1(); break;
			case redB2: autoRedB2(); break;
			case redC0: autoRedC0(); break;
			case redC1: autoRedC1(); break;
			case redC2: autoRedC2(); break;
			case blueA2: autoBlueA2(); break;
			default: break;
		}
	}

	/**
	 * The autonomous function for position A0 of the red side of the field.
	 */
	private void autoRedA0(){
		autoMove(1f, 3f);

		autoEnd();
	}

	/**
	 * The autonomous function for position A1 of the red side of the field.
	 */
	private void autoRedA1(){
		autoAGear();
		autoTurn(-70);
		autoMove(1f, 4f);
		
		autoEnd();
	}

	/**
	 * The autonomous function for position B0 of the red side of the field.
	 */
	private void autoRedB0(){
		autoBGear();

		autoEnd();
	}
	
	/**
	 * The autonomous function for position B1 of the red side of the field.
	 */
	private void autoRedB1(){
		autoBGear();
		autoTurn(-80);
		autoMove(1f, 2f);
		autoTurn(90);
		autoMove(1f, 4f);
		
		autoEnd();
	}
	
	/**
	 * The autonomous function for position B2 of the red side of the field.
	 */
	private void autoRedB2(){
		autoBGear();
		autoTurn(80);
		autoMove(1f, 2.4f);
		autoTurn(-80);
		autoMove(1f, 4f);
		
		autoEnd();
	}

	/**
	 * The autonomous function for position C0 of the red side of the field.
	 */
	private void autoRedC0(){
		autoMove(1f, 3f);

		autoEnd();
	}
	
	/**
	 * The autonomous function for position C1 of the red side of the field.
	 */
	private void autoRedC1(){
		autoCGear();
		autoTurn(70);
		autoMove(1f, 4f);
		
		autoEnd();
	}
	
	/**
	 * The autonomous function for position C2 of the red side of the field.
	 */
	private void autoRedC2(){
		autoCGear();
		autoTurn(33f);
		autoMove(-1f, 0.8f);
		if(!autoStop){
			shooter.setWheel(1f);
			Timer.delay(0.25f);
			shooter.setWheel(-0.75f);
			Timer.delay(1f);
			shooter.setFeeder(-0.55f);
			shooter.setAgitator(0.4f);
			ballElevator.set(0f, 1f);
			Timer.delay(6f);
			shooter.stop();
			ballElevator.set(0f, 0f);
		}
		
		autoEnd();
	}
	
	/**
	 * The autonomous function for position A2 of the blue side of the field.
	 */
	private void autoBlueA2(){
		autoAGear();
		autoTurn(-33f);
		autoMove(-1f, 0.8f);
		//Don't do anything if autoStop has been set
		if(!autoStop){
			shooter.setWheel(1f);
			Timer.delay(0.25f);
			shooter.setWheel(-0.75f);
			Timer.delay(1f);
			shooter.setFeeder(-0.55f);
			shooter.setAgitator(0.4f);
			ballElevator.set(0f, 1f);
			Timer.delay(6f);
			shooter.stop();
			ballElevator.set(0f, 0f);
		}
		
		autoEnd();
	}

	/**
	 * Autonomously places a gear for position A.
	 */
	private void autoAGear(){
		autoMove(0.85f, 2.2f);
		autoTurn(60);
		autoAdjustAngleLeft();
		//autoMove(0.7f, 0.2f);
		//autoAdjustAngleLeft();
		autoDeliverGear(5f);
		autoMove(-0.75f, 0.5f);
	}
	
	/**
	 * Autonomously places a gear for position B.
	 */
	private void autoBGear(){
		autoMove(0.7f, 1.7f);
		autoAdjustAngleLeft();
		autoDeliverGear(5f);
	}
	
	/**
	 * Autonomously places a gear for position C.
	 */
	private void autoCGear(){
		autoMove(0.85f, 2f);
		autoTurn(-60);
		autoAdjustAngleRight();
		//autoMove(0.7f, 0.2f);
		//autoAdjustAngleRight();
		autoDeliverGear(5f);
		autoMove(-0.75f, 0.5f);
	}
	
	/**
	 * Autonomously adjusts the robots angle based on the vision processing for the right side of the tower.
	 */
	private void autoAdjustAngleRight(){
		SmartDashboard.putBoolean("targetFound", false);
		//Don't do anything if autoStop has been set
		if(!autoStop) {
			//Wait half a sec for the vision processing to find a target
			Timer.delay(0.5f);
			//Get the adjustment value from the vision processing
			double adjustValue = SmartDashboard.getNumber("adjustValue", -1000);

			//If a target wasn't found search for it
			if(adjustValue == -1000){
				//Turn in small increments and check if a target is found
				for(int i = 0; i < 3; i++){
					autoTurn(10f);
					Timer.delay(0.5f);
					adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
					if(adjustValue != -1000){
						SmartDashboard.putBoolean("targetFound", true);
						break;
					}
				}
				//Search in the other direction if a target still hasn't been found
				if(adjustValue == -1000){
					autoTurn(-25f);
					//Turn in small increments and check if a target is found
					for(int i = 0; i < 3; i++){
						autoTurn(-10f);
						Timer.delay(0.5f);
						adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
						if(adjustValue != -1000){
							SmartDashboard.putBoolean("targetFound", true);
							break;
						}
					}
				}
			}
			//Stop autonomous if searching failed
			if(adjustValue == -1000){
				//autoTurn(-9f);
				autoStop = true;
			}

			//Turn based on the adjust value
			if (adjustValue > 0) {
				autoTurn(adjustValue + 2);
			} else if (adjustValue < 0) {
				autoTurn(adjustValue - 2);
			}
		}

		drive.tankDrive(0f, 0f);
	}
	
	/**
	 * Autonomously adjusts the robots angle based on the vision processing for the left side of the tower.
	 */
	private void autoAdjustAngleLeft(){
		SmartDashboard.putBoolean("targetFound", false);
		//Don't do anything if autoStop has been set
		if(!autoStop) {
			Timer.delay(0.5f);
			double adjustValue = SmartDashboard.getNumber("adjustValue", -1000);

			//If a target wasn't found search for it
			if(adjustValue == -1000){
				//Turn in small increments and check if a target is found
				for(int i = 0; i < 3; i++){
					autoTurn(-10f);
					Timer.delay(0.5f);
					adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
					if(adjustValue != -1000){
						SmartDashboard.putBoolean("targetFound", true);
						break;
					}
				}
				//Search in the other direction if a target still hasn't been found
				if(adjustValue == -1000){
					autoTurn(25f);
					//Turn in small increments and check if a target is found
					for(int i = 0; i < 3; i++){
						autoTurn(10f);
						Timer.delay(0.5f);
						adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
						if(adjustValue != -1000){
							SmartDashboard.putBoolean("targetFound", true);
							break;
						}
					}
				}
			}
			//Stop autonomous if searching failed
			if(adjustValue == -1000){
				//autoTurn(-9f);
				autoStop = true;
			}

			//Turn based on the adjust value
			if (adjustValue > 0) {
				autoTurn(adjustValue + 2);
			} else if (adjustValue < 0) {
				autoTurn(adjustValue - 2);
			}
		}

		drive.tankDrive(0f, 0f);
	}

	/**
	 * Autonomously delivers a gear given that the robot is positioned in front of the peg.
	 *
	 * @param timeout	the amount of time before the robot quits trying to place the gear
	 */
	private void autoDeliverGear(double timeout){
		//Don't do anything if autoStop has been set
		if(!autoStop){
			boolean triggered = false;
			gyro.reset();
			double start = Timer.getFPGATimestamp();
			//Move forward until the plate is triggered or the timeout finishes
			while(!triggered){
				//Check if the timeout has finished
				if(Timer.getFPGATimestamp() - start > timeout){
					autoStop = true;
					break;
				}

				//Move forward straight with gyro
				angle = gyro.getAngle();
				drive.tankDrive(0.5f + angle * kp, 0.5f - angle * kp);
				//Check if the plate has been triggered
				if(gearCatcher.getTriggerState()){
					triggered = true;
					placeGear();
				}

				Timer.delay(PERIODIC_DELAY);
			}
		}
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
	
	/**
	 * Moves the robot forward for a given amount of time at a given speed.
	 *
	 * @param speed	the speed to move the robot
	 * @param time	the time to move the robot
	 */
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
	 * Turns the robot for a given angle.
	 *
	 * @param turnAngle	the amount to turn
	 */
	private void autoTurn(double turnAngle){
		//Don't do anything if autoStop has been set
		if(!autoStop){
			gyro.reset();
			angle = gyro.getAngle();

			if(Math.signum(turnAngle) > 0){ //Turn right
				while(angle < turnAngle - 8){
					angle = gyro.getAngle();
					drive.tankDrive(-0.7f, 0.7f);
					Timer.delay(PERIODIC_DELAY);
				}
			} else if(Math.signum(turnAngle) < 0){ //Turn left
				while(angle > turnAngle + 8){
					angle = gyro.getAngle();
					drive.tankDrive(0.7f, -0.7f);
					Timer.delay(PERIODIC_DELAY);
				}
			}
		}

		drive.tankDrive(0f, 0f);
	}
	
	/**
	 * Ends autonomous and stops robot movement for the remainder of the autonomous period.
	 * Updates the autoEnd boolean in the Smartdashboard.
	 */
	private void autoEnd(){
		SmartDashboard.putBoolean("autoEnd", true);
		while(isAutonomous()){
			drive.tankDrive(0f, 0f);
		}
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
		
		//Update the state
		state = SmartDashboard.getBoolean("state", false);
		if(state){
			gearRelay.set(false);
			ballRelay.set(true);
		} else{
			gearRelay.set(true);
			ballRelay.set(false);
		}
		
		//--------------- PILOT CONTROLS ---------------
		//State switching
		boolean toggleReading = pilot.getButtonA();
		if(toggleReading != lastToggleState){
			lastDebounceTime = Timer.getFPGATimestamp();
		}
		if(Timer.getFPGATimestamp() - lastDebounceTime > debounceDelay){
			if(toggleState != toggleReading){
				toggleState = toggleReading;
				if(toggleState){
					state = !state;
					SmartDashboard.putBoolean("state", state);
				}
			}
		}
		lastToggleState = toggleReading;
		
		//Driving
		double x = pilot.getRightJoyX();
		x = Math.signum(x) * Math.pow(x, 2);
		double y = pilot.getRightJoyY();
		y = Math.signum(y) * Math.pow(y, 2);
		
		if(pilot.getLeftTrigger() > 0){ //High gear driving
			shiftHigh();
			x *= getXScale(y);
			y *= state ? 1f : -1f;
		} else{ //Low gear driving
			shiftLow();
			
			if(pilot.getRightTrigger() > 0){ //Shake
				double turn = Math.sin(20f * Timer.getFPGATimestamp());
				//drive.tankDrive(0.7f * turn, 0.7f * -turn);
				x = 0.7f * turn; //Use x so the wheels turn opposite
				y = 0;
			} else if(pilot.getButtonLB()){ //MAX low gear pushing
				x *= 0.7f; //Maybe turn this down
				y *= state ? 1f : -1f;
			} else{
				x *= 0.7f;
				y *= state ? 0.8f : -0.8f;
			}
		}
		drive.tankDrive((y - x) * 0.3, (y + x) * 0.3); //Add damper for driving demos
		
		//--------------- COPILOT CONTROLS ---------------
		//Ball elevator
		if(copilot.getRightTrigger() > 0){
			ballElevator.set(0.75f, -1f);
		} else{
			ballElevator.set(0f, 0f);
		}
		
		//Shooting
		if(copilot.getLeftTrigger() > 0){
			shooter.setWheel(-0.75f);
			if(copilot.getButtonLB()){ //CHANGE THIS TO AUTO START WITH DELAY
				shooter.setFeeder(-0.55f);
				shooter.setAgitator(0.4f);
				ballElevator.set(0f, 1f);
			} else{
				shooter.setFeeder(0f);
				shooter.setAgitator(0f);
			}
		} else if(copilot.getButtonRB()){ //Ball unjamming
			shooter.setWheel(1f);
			shooter.setFeeder(0.55f);
		} else{
			shooter.stop();
		}
		
		/*
		//Shooting *simplified* NEED TO TEST
		if(copilot.getLeftTrigger() > 0){
			if(shooter.getWheelSpeed() == 0){
				shooter.resetDelay();
			}
			shooter.setWheel(-0.82f);
			if(shooter.canFeed()){
				shooter.setFeeder(-0.55f);
				ballElevator.set(0f, 1f);
			}
		} else if(copilot.getButtonRB()){
			shooter.setWheel(1f);
			shooter.setFeeder(0.55f);
		} else{
			shooter.stop();
		}
		*/
		
		//Climber
		if(copilot.getPOV() == Controller.POVUP){
			climber.up();
		} else if(copilot.getPOV() == Controller.POVDOWN){
			climber.down();
		} else{
			climber.stop();
		}
		
		//Retry shelf extension
		if(copilot.getButtonLS()){
			ballShelf.retry();
		}
		
		//Manual gear catcher controls if needed
		if(copilot.getButtonX()){
			gearCatcher.close();
		}
		if(copilot.getButtonY()){
			gearCatcher.open();
		}
		if(Timer.getFPGATimestamp() - gearCatcher.getOpenTime() > 2){
			gearCatcher.close();
		}
		//Enable the gear catcher
		if(copilot.getButtonA()){
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
		leftShifter.set(DoubleSolenoid.Value.kForward);
		rightShifter.set(DoubleSolenoid.Value.kForward);
	}

	/**
	 * Shifts the drive gears low.
	 */
	private void shiftLow(){
		leftShifter.set(DoubleSolenoid.Value.kReverse);
		rightShifter.set(DoubleSolenoid.Value.kReverse);
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
