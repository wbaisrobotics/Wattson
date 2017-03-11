package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String centerGear = "Center Gear";
	final String leftGear = "Left Gear";
	final String rightGear = "Right Gear";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	public final double PERIODIC_DELAY = 0.005f;

	private Controller pilot;
	private Controller copilot;
	
	private Compressor compressor;
	
	//Gyro
	private ADXRS450_Gyro gyro;
	private double angle;
	private double kp = 0.03f;

	//Drive
	private Servo leftShifter;
	private Servo rightShifter;
	private int leftShifterLowAngle = 50;
	private int leftShifterHighAngle = 125;
	private int rightShifterLowAngle = 5;
	private int rightShifterHighAngle = 95;
	private RobotDrive drive;

	private GearCatcher gearCatcher;
	
	private boolean state = false;
	private double lastDebounceTime = 0f;
	private double debounceDelay = 0.05f;
	private boolean toggleState = false;
	private boolean lastToggleState = false;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Center Gear", centerGear);
		chooser.addObject("Left Gear", leftGear);
		chooser.addObject("Right Gear", rightGear);
		SmartDashboard.putData("Auto choices", chooser);

		pilot = new Controller(0);
		copilot = new Controller(1);
		
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		
		gyro = new ADXRS450_Gyro();

		leftShifter = new Servo(2);
		rightShifter = new Servo(3);
		drive = new RobotDrive(0, 1);
		drive.setExpiration(0.1f);

		gearCatcher = new GearCatcher();
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
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);

		gyro.reset();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
			case centerGear:
				autoCenterGear();
				break;
			case leftGear:
				autoLeftGear();
				break;
			case rightGear:
				autoRightGear();
				break;
			default:
				break;
		}
	}
	
	private void autoCenterGear(){ //ADD Ultrasonic or at least time safety fallback (probably just time)
		//THIS WILL REPLACE THE autoMove(0.6f, 2f); CALL
		/*
		double start = Timer.getFPGATimestamp();
		gyro.reset();
		while(gearSonic.getVoltage() > whatDistance? && Timer.getFPGATimestamp() - start < estimatedTimeToComplete?){
			angle = gyro.getAngle();
			drive.tankDrive(0.6f + angle * kp, 0.6f - angle * kp);
			Timer.delay(PERIODIC_DELAY);
		}
		*/
		autoMove(0.6f, 2f);
		
		boolean triggered = false;
		gyro.reset();
		while(!triggered){
			angle = gyro.getAngle();
			drive.tankDrive(0.5f + angle * kp, 0.5f - angle * kp);
			if(gearCatcher.getTriggerState()){
				triggered = true;
				deliverGear();
			}
			
			Timer.delay(PERIODIC_DELAY);
		}
		autoEnd();
	}

	private void autoLeftGear(){
		autoMove(0.7f, 3.15f);
		autoTurn(30);
		Timer.delay(0.5f);
		double adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		while(adjustValue == -1000 && isAutonomous()){ //Wait for target
			adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		}
		autoTurn(adjustValue);
		boolean triggered = false;
		gyro.reset();
		while(!triggered){
			angle = gyro.getAngle();
			drive.tankDrive(0.5f + angle * kp, 0.5f - angle * kp);
			if(gearCatcher.getTriggerState()){
				triggered = true;
				deliverGear();
			}
			
			Timer.delay(PERIODIC_DELAY);
		}
		autoTurn(-30);
		autoMove(0.7f, 3f);
		autoEnd();
	}
	
	private void autoRightGear(){
		autoMove(0.7f, 3.15f);
		autoTurn(-30);
		Timer.delay(0.5f);
		double adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		while(adjustValue == -1000 && isAutonomous()){ //Wait for target
			adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		}
		autoTurn(adjustValue);
		boolean triggered = false;
		gyro.reset();
		while(!triggered){
			angle = gyro.getAngle();
			drive.tankDrive(0.5f + angle * kp, 0.5f - angle * kp);
			if(gearCatcher.getTriggerState()){
				triggered = true;
				deliverGear();
			}
			
			Timer.delay(PERIODIC_DELAY);
		}
		autoTurn(30);
		autoMove(0.7f, 3f);
		autoEnd();
	}

	private void placeGear(){
		gearCatcher.open();
		autoMove(-0.75f, 1.25f);
		gearCatcher.close();
	}
	
	private void autoMove(double speed, double time){
		gyro.reset();
		double start = Timer.getFPGATimestamp();
		
		while(Timer.getFPGATimestamp() - start < time){
			angle = gyro.getAngle();
			//y + x, y - x
			drive.tankDrive(speed + angle * kp, speed - angle * kp);
			Timer.delay(PERIODIC_DELAY);
		}
		drive.tankDrive(0f, 0f);
	}
	
	private void autoTurn(double turnAngle){
		gyro.reset();
		angle = gyro.getAngle();
		
		if(Math.signum(turnAngle) > 0){ //Turn right
			while(angle < turnAngle - 8){
				angle = gyro.getAngle();
				drive.tankDrive(-0.5f, 0.5f);
				Timer.delay(PERIODIC_DELAY);
			}
		} else if(Math.signum(turnAngle) < 0){ //Turn left
			while(angle > turnAngle + 8){
				angle = gyro.getAngle();
				drive.tankDrive(0.5f, -0.5f);
				Timer.delay(PERIODIC_DELAY);
			}
		}
		
		/*
		while(turnAngle - angle < 0){ //incorrect
			angle = gyro.getAngle();
			drive.tankDrive(0.2f * Math.signum(turnAngle), 0.2f * -Math.signum(turnAngle));
			Timer.delay(PERIODIC_DELAY);
		}
		*/
		drive.tankDrive(0f, 0f);
	}
	
	private void autoEnd(){
		while(isAutonomous()){
			drive.tankDrive(0f, 0f);
		}
	}
	
	private void deliverGear(){
		gearCatcher.open();
		autoMove(-0.7f, 2f);
		gearCatcher.close();
	}

	@Override
	public void teleopInit(){
		gyro.reset();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		if(gearCatcher.isEnabled() && gearCatcher.getTriggerState()){
			placeGear();
			gearCatcher.disable();
		}
		
		//Update the state
		state = SmartDashboard.getBoolean("state", false);
		if(state){
			//gearRelay.set(false);
			//ballRelay.set(true);
		} else{
			//gearRelay.set(true);
			//ballRelay.set(false);
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
		drive.tankDrive(y - x, y + x);
		
		//--------------- COPILOT CONTROLS ---------------
		//Gear catcher controls
		if(copilot.getButtonX()){
			gearCatcher.close();
		}
		if(copilot.getButtonY()){
			gearCatcher.open();
		}
		if(Timer.getFPGATimestamp() - gearCatcher.getOpenTime() > 2){
			gearCatcher.close();
		}
		if(copilot.getButtonA()){
			gearCatcher.enable();
		}

		Timer.delay(PERIODIC_DELAY);
	}

	private double getXScale(double y){ //Logistic function to limit turning speed based on forward speed
		return 1 - 0.5f / (1 + Math.pow(Math.E, -10 * (Math.abs(y) - 0.5f)));
	}

	private void shiftHigh(){
		leftShifter.setAngle(leftShifterHighAngle);
		rightShifter.setAngle(rightShifterHighAngle);
	}

	private void shiftLow(){
		leftShifter.setAngle(leftShifterLowAngle);
		rightShifter.setAngle(rightShifterLowAngle);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
