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
	final String redA0 = "Red A0";
	final String redA1 = "Red A1";
	final String redA2 = "Red A2";
	final String redA3 = "Red A3";
	final String redB0 = "Red B0";
	final String redB1 = "Red B1";
	final String redB2 = "Red B2";
	final String redB3 = "Red B3";
	final String redC0 = "Red C0";
	final String redC1 = "Red C1";
	final String redC2 = "Red C2";
	final String redC3 = "Red C3";
	//Blue autonomous choices
	final String blueC1 = "Blue C1";
	//private final String blueA1 = "Blue A1";
	//private final String blueB1 = "Blue B1";
	//private final String blueC1 = "Blue C1";

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
	private Climber climber;
	
	private AnalogInput gearSonic;
	
	private DigitalOutput gearRelay;
	private DigitalOutput ballRelay;
	
	private boolean state = false;
	private double lastDebounceTime = 0f;
	private double debounceDelay = 0.05f;
	private boolean toggleState = false;
	private boolean lastToggleState = false;

	private boolean autoStop = false;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addObject("Red A0", redA0);
		chooser.addObject("Red A1", redA1);
		chooser.addObject("Red A2", redA2);
		chooser.addObject("Red A3", redA3);
		chooser.addObject("Red B0", redB0);
		chooser.addDefault("Red B1", redB1); //Default
		chooser.addObject("Red B2", redB2);
		chooser.addObject("Red B3", redB3);
		chooser.addObject("Red C0", redC0);
		chooser.addObject("Red C1", redC1);
		chooser.addObject("Red C2", redC2);
		chooser.addObject("Red C3", redC3);
		
		chooser.addObject("Blue C1", blueC1);
		SmartDashboard.putData("Auto choices", chooser);
		
		SmartDashboard.putBoolean("state", false);

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
		climber = new Climber();
		
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

		SmartDashboard.putBoolean("autoEnd", false);
		ballShelf.release();
		gyro.reset();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
			case redA0: autoRedA0(); break;
			case redA1: autoRedA1(); break;
			case redA2: autoRedA2(); break;
			case redA3: autoRedA3(); break;
			case redB0: autoRedB0(); break;
			case redB1: autoRedB1(); break;
			case redB2: autoRedB2(); break;
			case redB3: autoRedB3(); break;
			case redC0: autoRedC0(); break;
			case redC1: autoRedC1(); break;
			case redC2: autoRedC2(); break;
			case redC3: autoRedC3(); break;
			
			case blueC1: autoBlueC1(); break;
			default: break;
		}
	}

	private void autoRedA0(){
		autoMove(1f, 3f);

		autoEnd();
	}

	private void autoRedA1(){ //Tested
		autoAGear();
		autoTurn(-70);
		autoMove(1f, 4f);
		
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
		autoTurn(15);
		autoMove(0.7f, 3f);
		autoTurn(-105);
		autoMove(0.7f, 1f);
		
		autoEnd();
	}

	private void autoRedB0(){

	}
	
	private void autoRedB1(){ //Tested ok~
		autoBGear();
		autoTurn(-80);
		autoMove(1f, 2f);
		autoTurn(80);
		autoMove(1f, 4f);
		
		autoEnd();
	}
	
	private void autoRedB2(){
		autoBGear();
		autoTurn(-90);
		autoMove(0.7f, 1.5f);
		autoTurn(90);
		autoMove(0.7f, 4f);
		
		autoEnd();
	}
	
	private void autoRedB3(){
		autoBGear();
		autoTurn(-90);
		autoMove(0.7f, 1.5f);
		autoTurn(90);
		autoMove(0.7f, 2f);
		autoTurn(15);
		autoMove(0.7f, 3f);
		autoTurn(-105);
		autoMove(0.7f, 1f);
		
		autoEnd();
	}

	private void autoRedC0(){
		autoMove(1f, 3f);

		autoEnd();
	}
	
	private void autoRedC1(){ //Tested
		autoCGear();
		autoTurn(70);
		autoMove(1f, 4f);
		
		autoEnd();
	}
	
	private void autoRedC2(){
		autoCGear();
		autoTurn(30);
		autoMove(0.7f, 1.5f);
		autoTurn(-45);
		autoMove(0.7f, 3f);
		autoTurn(45);
		
		autoEnd();
	}
	
	private void autoRedC3(){
		autoCGear();
		autoTurn(30);
		autoMove(0.7f, 4f);
		autoTurn(-90);
		autoMove(0.7f, 3f);
		
		autoEnd();
	}
	
	private void autoBlueC1(){ //Tested
		autoBlueCGear();
		autoTurn(70);
		autoMove(1f, 4f);
		
		autoEnd();
	}

	private void autoAGear(){
		autoMove(0.85f, 2.3f);
		autoTurn(60);
		autoAdjustAngle();
		autoDeliverSideGear();
		autoMove(-0.75f, 0.5f);
	}
	
	private void autoBGear(){ //ADD Ultrasonic or at least time safety fallback (probably just time)
		//THIS COULD REPLACE THE autoMove(0.6f, 2f); CALL
		/*
		double start = Timer.getFPGATimestamp();
		gyro.reset();
		while(gearSonic.getVoltage() > whatDistance? && Timer.getFPGATimestamp() - start < estimatedTimeToComplete?){
			angle = gyro.getAngle();
			drive.tankDrive(0.6f + angle * kp, 0.6f - angle * kp);
			Timer.delay(PERIODIC_DELAY);
		}
		*/
		
		autoMove(0.7f, 1.9f);
		//autoAdjustAngle();
		autoDeliverCenterGear();
	}
	
	private void autoCGear(){
		autoMove(0.85f, 2.3f);
		autoTurn(-65);
		autoAdjustAngle();
		autoDeliverSideGear();
		autoMove(-0.75f, 0.5f);
	}
	
	private void autoBlueCGear(){
		autoMove(0.85f, 2.4f);
		autoTurn(-65);
		autoAdjustAngle();
		autoDeliverSideGear();
		autoMove(-0.75f, 0.5f);
	}
	
	private void autoAdjustAngle(){
		if(!autoStop) {
			Timer.delay(0.5f);
			double adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
			double start = Timer.getFPGATimestamp();
			while (adjustValue == -1000 && isAutonomous()) { //Wait for target
				adjustValue = SmartDashboard.getNumber("adjustValue", -1000);
				if(Timer.getFPGATimestamp() - start > 3){ //autoStop after 3s
					autoStop = true;
					break;
				}
			}
			if (adjustValue > 0) {
				autoTurn(adjustValue + 2);
			} else if (adjustValue < 0) {
				autoTurn(adjustValue - 2);
			}
		}
	}

	private void autoDeliverSideGear(){
		if(!autoStop){
			boolean triggered = false;
			gyro.reset();
			double start = Timer.getFPGATimestamp();
			while(!triggered){
				if(Timer.getFPGATimestamp() - start > 7){ //Stop creeping after 5s and assume something went wrong
					autoStop = true;
					break;
				}

				angle = gyro.getAngle();
				drive.tankDrive(0.5f + angle * kp, 0.5f - angle * kp);
				if(gearCatcher.getTriggerState()){
					triggered = true;
					placeGear();
				}

				Timer.delay(PERIODIC_DELAY);
			}
		}
	}
	
	private void autoDeliverCenterGear(){
		if(!autoStop){
			boolean triggered = false;
			gyro.reset();
			double start = Timer.getFPGATimestamp();
			while(!triggered){
				if(Timer.getFPGATimestamp() - start > 5){ //Stop creeping after 5s and assume something went wrong
					autoStop = true;
					break;
				}

				angle = gyro.getAngle();
				drive.tankDrive(0.5f + angle * kp, 0.5f - angle * kp);
				if(gearCatcher.getTriggerState()){
					triggered = true;
					placeGear();
				}

				Timer.delay(PERIODIC_DELAY);
			}
		}
	}

	private void placeGear(){
		gearCatcher.open();
		autoMove(-0.75f, 1.25f);
		gearCatcher.close();
	}
	
	private void autoMove(double speed, double time){
		if(!autoStop){
			gyro.reset();
			double start = Timer.getFPGATimestamp();

			while(Timer.getFPGATimestamp() - start < time){
				angle = gyro.getAngle();
				//y + x, y - x
				drive.tankDrive(speed + angle * kp, speed - angle * kp);
				Timer.delay(PERIODIC_DELAY);
			}
		}

		drive.tankDrive(0f, 0f);
	}
	
	private void autoTurn(double turnAngle){
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
		//SmartDashboard.putNumber("gearSonic", gearSonic.getVoltage());
		
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
		//IS SOME OF THIS REDUNDENT?
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
				ballElevator.set(0f, 1f);
			} else{
				shooter.setFeeder(0f);
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
		
		//Close gear catcher (if needed)
		if(copilot.getButtonX()){
			gearCatcher.close();
		}
		
		//Enable the gear catcher
		if(copilot.getButtonY()){
			gearCatcher.enable();
		}
		
		/* Uncomment this if we find we need the option
		if(copilot.getButtonY()){
			gearCatcher.open();
		}
		*/

		Timer.delay(PERIODIC_DELAY);
	}

	private double getXScale(double y){ //Logistic function to limit turning speed based on forward speed
		return 1 - 0.5f / (1 + Math.pow(Math.E, -10 * (Math.abs(y) - 0.5f)));
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
