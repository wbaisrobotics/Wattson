package org.usfirst.frc.team4338.robot;

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
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	public final double PERIODIC_DELAY = 0.005f;
	private Timer timer;

	private Controller controller;

	private RobotDrive drive;
	private Servo leftGearShifter;
	private Servo rightGearShifter;
	private int leftGearShifterLowAngle = 50;
	private int leftGearShifterHighAngle = 125;
	private int rightGearShifterLowAngle = 5;
	private int rightGearShifterHighAngle = 95;

	private BallElevator ballElevator;
	private Shooter shooter;
	private GearCatcher gearCatcher;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);

		timer = new Timer();

		controller = new Controller(0);

		drive = new RobotDrive(0, 1, 2, 3);
		drive.setExpiration(0.1f);

		leftGearShifter = new Servo(4);
		rightGearShifter = new Servo(5);

		ballElevator = new BallElevator();
		shooter = new Shooter();
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
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
			case customAuto: //Custom autonomous program
				break;
			case defaultAuto: //Default autonomous program
				break;
			default:
				break;
		}
	}

	@Override
	public void teleopInit(){
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		//Gear shifting
		if (controller.getButtonRS()) {
			shiftHigh();
		} else {
			shiftLow();
		}

		//Ball elevator test
		if(controller.getLeftTrigger() > 0){
			ballElevator.set(0.3f, -1f);
		} else{
			ballElevator.set(0f, 0f);
		}

		//Shooter test
		if(controller.getRightTrigger() > 0){
			shooter.set(-0.7f, 1f);
		} else{
			shooter.set(0f, 0f);
		}

		//Gear catcher test
		if(controller.getButtonA()){
			gearCatcher.open();
		} else if(controller.getButtonB()){
			gearCatcher.close();
		}

		//Driving
		double x = controller.getRightJoyX();
		x = 0.8 * Math.signum(x) * Math.pow(x, 2); //Maybe decrease?
		double y = controller.getRightJoyY();
		y = 0.9 * Math.signum(y) * Math.pow(y, 2);

		drive.tankDrive(-y - x, -y + x);

		Timer.delay(PERIODIC_DELAY);
	}

	private void shiftHigh(){
		leftGearShifter.setAngle(leftGearShifterHighAngle);
		rightGearShifter.setAngle(rightGearShifterHighAngle);
	}

	private void shiftLow(){
		leftGearShifter.setAngle(leftGearShifterLowAngle);
		rightGearShifter.setAngle(rightGearShifterLowAngle);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
