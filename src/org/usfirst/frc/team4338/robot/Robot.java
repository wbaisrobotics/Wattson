package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;
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
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	public final double PERIODIC_DELAY = 0.005f;
	private Timer timer;

	private Controller controller;

	//Drive
	private CANTalon leftCAN1;
	private CANTalon leftCAN2;
	private CANTalon rightCAN1;
	private CANTalon rightCAN2;
	private RobotDrive drive;

	private BallElevator ballElevator;

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

		leftCAN1 = new CANTalon(1);
		leftCAN2 = new CANTalon(2);
		rightCAN1 = new CANTalon(3);
		rightCAN2 = new CANTalon(4);
		drive = new RobotDrive(leftCAN1, leftCAN2, rightCAN1, rightCAN2);
		drive.setExpiration(0.1f);

		ballElevator = new BallElevator();
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
		if(SmartDashboard.getBoolean("targetExists", false)){
			//do target aiming here
		} else{
			//Regular teleop code
		}

		if(controller.getLeftTrigger() > 0){
			ballElevator.set(-0.7f, 1f);
		} else{
			ballElevator.set(0f, 0f);
		}

		//Driving
		double x = controller.getRightJoyX();
		x = 0.55 * Math.signum(x) * Math.pow(x, 2); //original: 0.8f
		double y = controller.getRightJoyY();
		y = 0.6 * Math.signum(y) * Math.pow(y, 2); //original: 0.9f
		drive.tankDrive(y - x, y + x);

		Timer.delay(PERIODIC_DELAY);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
