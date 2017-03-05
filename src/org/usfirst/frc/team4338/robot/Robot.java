package org.usfirst.frc.team4338.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.IterativeRobot;
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
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	private Controller controller = new Controller(0);
	
	private CANTalon leftDrive1 = new CANTalon(1);
	private CANTalon leftDrive2 = new CANTalon(2);
	private CANTalon rightDrive1 = new CANTalon(3);
	private CANTalon rightDrive2 = new CANTalon(4);
	
	private Victor sweeper = new Victor(2);
	private CANTalon elevator = new CANTalon(5);
	
	private Victor climber1 = new Victor(0);
	private Victor climber2 = new Victor(1);
	
	private Victor shooterWheel = new Victor(3);
	private Victor shooterFeeder = new Victor(4);
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
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
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		if(controller.getRightTrigger() > 0){
			rightDrive1.set(1);
		}
		if(controller.getButtonRB()){
			rightDrive2.set(1);
		}
		if(controller.getLeftTrigger() > 0){
			leftDrive1.set(1);
		}
		if(controller.getButtonLB()){
			leftDrive2.set(1);
		}
		if(controller.getPOV() == Controller.POVUP){
			climber1.set(1);
		}
		if(controller.getPOV() == Controller.POVDOWN){
			climber2.set(1);
		}
		if(controller.getButtonA()){
			shooterWheel.set(1);
		}
		if(controller.getButtonB()){
			shooterFeeder.set(1);
		}
		if(controller.getButtonX()){
			sweeper.set(1);
		}
		if(controller.getButtonY()){
			elevator.set(1);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

