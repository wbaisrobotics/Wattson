package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
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
	//Red autonomous choices
	private final String redA1 = "Red A1";
	private final String redB1 = "Red B1";
	private final String redC1 = "Red C1";
	//Blue autonomous choices
	//private final String blueA1 = "Blue A1";
	//private final String blueB1 = "Blue B1";
	//private final String blueC1 = "Blue C1";
	
	private String autoSelected;
	private SendableChooser<String> chooser = new SendableChooser<>();

	public final double PERIODIC_DELAY = 0.005f;

	private Controller pilot;
	private Controller copilot;
	
	private Compressor compressor;
	
	//Gyro
	private ADXRS450_Gyro gyro;
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

	private BallElevator ballElevator;
	private BallShelf ballShelf;
	private Shooter shooter;
	private GearCatcher gearCatcher;
	private Victor climber;
	
	private AnalogInput gearSonic;
	
	private DigitalOutput gearRelay;
	private DigitalOutput ballRelay;
	
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
		chooser.addObject(redA1, redA1);
		chooser.addDefault(redB1, redB1);
		chooser.addObject(redC1, redC1);
		SmartDashboard.putData("Auto choices", chooser);

		pilot = new Controller(0);
		copilot = new Controller(1);
		
		compressor = new Compressor(0);
		compressor.setClosedLoopControl(true);
		
		gyro = new ADXRS450_Gyro();

		leftShifter = new DoubleSolenoid(1, 6);
		rightShifter = new DoubleSolenoid(2, 5);
		leftCAN1 = new CANTalon(1);
		leftCAN2 = new CANTalon(2);
		rightCAN1 = new CANTalon(3);
		rightCAN2 = new CANTalon(4);
		drive = new RobotDrive(leftCAN1, leftCAN2, rightCAN1, rightCAN2);
		drive.setExpiration(0.1f);

		ballElevator = new BallElevator();
		ballShelf = new BallShelf();
		shooter = new Shooter();
		gearCatcher = new GearCatcher();
		climber = new Victor(0);
		
		gearSonic = new AnalogInput(2); //Which channel to use?
		
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
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);

		ballShelf.release();
		gyro.reset();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
			case redA1:
				autoRedA1();
				break;
			case redB1:
				autoRedB1();
				break;
			case redC1:
				autoRedC1();
				break;
			default:
				break;
		}
	}
	
	private void autoRedA1(){
		autoAGear();
		autoTurn(-30);
		autoMove(0.7f, 1f);
		
		autoEnd();
	}
	
	private void autoRedA2(){
		autoAGear();
		autoTurn(-30);
		autoMove(0.7f, 4f);
		
		autoEnd();
	}
	
	private void autoRedA3(){
		autoAGear();
		autoTurn(-30);
		autoMove(0.7f, 1.5f);
		autoTurn(15f);
		autoMove(0.7f, 3f);
		autoTurn(-105f);
		autoMove(0.7f, 1f);
		
		autoEnd();
	}
	
	private void autoRedB1(){
		autoBGear();
		autoTurn(-60);
		
		autoEnd();
	}
	
	private void autoRedC1(){
		autoCGear();
		autoTurn(30);
		autoMove(0.7f, 1f);
		
		autoEnd();
	}
	
	private void autoRedC2(){
		autoCGear();
		autoTurn(30);
		autoMove(0.7f, 4f);
		
		autoEnd();
	}
	
	private void autoRedC3(){
		autoCGear();
		
		autoEnd();
	}

	private void autoAGear(){
		autoMove(0.7f, 3.15f);
		autoTurn(30);
		Timer.delay(0.5f);
		double adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		while(adjustValue == -1000 && isAutonomous()){ //Wait for target
			adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		}
		autoTurn(adjustValue);
		deliverGear();
	}
	
	private void autoBGear(){ //ADD Ultrasonic or at least time safety fallback (probably just time)
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
		//Add angle adjustment?
		deliverGear();
	}
	
	private void autoCGear(){
		autoMove(0.7f, 3.15f);
		autoTurn(-30);
		Timer.delay(0.5f);
		double adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		while(adjustValue == -1000 && isAutonomous()){ //Wait for target
			adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
		}
		autoTurn(adjustValue);
		deliverGear();
	}
	
	private void deliverGear(){ //tweak this
		boolean triggered = false;
		gyro.reset();
		while(!triggered){
			angle = gyro.getAngle();
			drive.tankDrive(0.5f + angle * kp, 0.5f - angle * kp);
			if(gearCatcher.getTriggerState()){
				triggered = true;
				gearCatcher.open(); //Change the release maybe
				autoMove(-0.7f, 2f);
				gearCatcher.close();
			}
			
			Timer.delay(PERIODIC_DELAY);
		}
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
		SmartDashboard.putNumber("gearSonic", gearSonic.getVoltage());
		
		if(gearCatcher.isEnabled() && gearCatcher.getTriggerState()){
			deliverGear();
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
		//IS SOME OF THIS REDUNDENT?
		//State switching
		boolean toggleReading = pilot.getButtonX();
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
		
		//Enable gear catcher
		if(pilot.getButtonA()){
			gearCatcher.enable();
		}
		
		//Driving
		double x = pilot.getRightJoyX();
		x = Math.signum(x) * Math.pow(x, 2);
		double y = pilot.getRightJoyY();
		y = Math.signum(y) * Math.pow(y, 2);
		
		if(pilot.getLeftTrigger() > 0){ //High gear driving
			shiftHigh();
			x *= 0.5f; //decreased from 0.7f, was too fast turning in high gear, Maybe make this lower
			y *= state ? 1f : -1f;
		} else{ //Low gear driving
			shiftLow();
			
			if(pilot.getRightTrigger() > 0){ //Shake
				double turn = Math.sin(20f * Timer.getFPGATimestamp());
				//drive.tankDrive(0.7f * turn, 0.7f * -turn);
				x = 0.7f * turn; //Use x so the wheels turn opposite
				y = 0;
			} else{
				x *= 0.7f;
				y *= state ? 0.8f : -0.8f;
			}
		}
		drive.tankDrive(y - x, y + x);
		
		//--------------- COPILOT CONTROLS ---------------
		//Ball elevator
		if(copilot.getRightTrigger() > 0){
			ballElevator.set(0.75f, -1f);
		} else{
			ballElevator.set(0f, 0f);
		}
		
		//Shooting
		if(copilot.getLeftTrigger() > 0){
			shooter.set(-0.75f, 0f);
			if(copilot.getButtonLB()){ //CHANGE THIS TO AUTO START WITH DELAY
				shooter.set(-0.75f, -0.55f);
				ballElevator.set(0f, 1f);
			}
		} else if(copilot.getButtonRB()){ //Ball unjamming
			shooter.set(1f, 1f);
		} else{
			shooter.set(0f, 0f);
		}
		
		//Shooting
		/*
		if(copilot.getLeftTrigger() > 0){
			if(shooter.getWheelSpeed() == 0){
				shooter.set(-0.82f, 0f);
				shooter.resetStart();
			} else if(shooter.canFeed()){
				shooter.set(-0.82f, -0.75f);
				ballElevator.set(0f, 1f);
			}
		} else{
			shooter.setFeeder(0f);
		}
		if(copilot.getButtonB()){
			shooter.set(0f, 0f);
		}
		if(copilot.getButtonRB()){
			shooter.set(0.7f, 1f);
		} else{
			if(shooter.getWheelSpeed() < 0){ //FIX THIS
				shooter.setFeeder(0f);
			} else{
				shooter.set(0f, 0f);
			}
		}
		*/
		
		//Climber
		if(copilot.getPOV() == Controller.POVUP){
			climber.set(1f);
		} else if(copilot.getPOV() == Controller.POVDOWN){
			climber.set(-1f);
		} else{
			climber.set(0f);
		}
		
		//Retry shelf extension
		if(copilot.getButtonLS()){
			ballShelf.retry();
		}
		
		//Close gear catcher (if needed)
		if(copilot.getButtonX()){
			gearCatcher.close();
		}
		/* Uncomment this if we find we need the option
		if(copilot.getButtonY()){
			gearCatcher.open();
		}
		*/

		Timer.delay(PERIODIC_DELAY);
	}

	private void shiftHigh(){
		leftShifter.set(DoubleSolenoid.Value.kForward);
		rightShifter.set(DoubleSolenoid.Value.kForward);
	}

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
