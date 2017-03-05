package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * The controller used to control the robot. The variables in this class are
 * configured for Logitech controllers.
 */
public class Controller {
	public static final int POVUP = 0;
	public static final int POVLEFT = 90;
	public static final int POVDOWN = 180;
	public static final int POVRIGHT = 270;
	
	private DriverStation ds;
	private final int port;

	/**
	 * Creates a controller at the given port.
	 *
	 * @param port the port
	 */
	public Controller(int port) {
		ds = DriverStation.getInstance();
		this.port = port;
	}

	public boolean getButtonA() {
		return getRawButton(0);
	}

	public boolean getButtonB() {
		return getRawButton(1);
	}

	public boolean getButtonBack() {
		return getRawButton(6);
	}

	public boolean getButtonLB() {
		return getRawButton(4);
	}

	public boolean getButtonLS() {
		return getRawButton(8);
	}

	public boolean getButtonRB() {
		return getRawButton(5);
	}

	public boolean getButtonRS() {
		return getRawButton(9);
	}

	public boolean getButtonStart() {
		return getRawButton(7);
	}

	public boolean getButtonX() {
		return getRawButton(2);
	}

	public boolean getButtonY() {
		return getRawButton(3);
	}

	public double getLeftJoyX() {
		return getRawAxis(0);
	}

	public double getLeftJoyY() {
		return getRawAxis(1);
	}

	public double getLeftTrigger() {
		return getRawAxis(2);
	}

	/**
	 *
	 * @return
	 */
	public int getPOV() {
		return ds.getStickPOV(port, 0);
	}

	/**
	 * Gets the value of a given axis on the set port.
	 *
	 * @param axis the axis
	 * @return the value
	 */
	public double getRawAxis(int axis) {
		return ds.getStickAxis(port, axis);
	}

	/**
	 *
	 * @param button
	 * @return
	 */
	public boolean getRawButton(int button) {
		return ((1 << button) & ds.getStickButtons(port)) != 0;
	}

	public double getRightJoyX() {
		return getRawAxis(4);
	}

	public double getRightJoyY() {
		return getRawAxis(5);
	}

	public double getRightTrigger() {
		return getRawAxis(3);
	}
}