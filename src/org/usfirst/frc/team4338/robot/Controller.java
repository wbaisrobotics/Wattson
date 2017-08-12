package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Controller.java - The controller used to control the robot. The variables in this class are
 * configured for Logitech controllers.
 *
 * @author Aaron Shappell
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

	/**
	 * Gets the state of the A button.
	 *
	 * @return the A button state
	 */
	public boolean getButtonA() {
		return getRawButton(0);
	}

	/**
	 * Gets the state of the B button.
	 *
	 * @return the B button state
	 */
	public boolean getButtonB() {
		return getRawButton(1);
	}

	/**
	 * Gets the state of the Back button.
	 *
	 * @return the Back button state
	 */
	public boolean getButtonBack() {
		return getRawButton(6);
	}

	/**
	 * Get the state of the LB button.
	 *
	 * @return the LB button state
	 */
	public boolean getButtonLB() {
		return getRawButton(4);
	}

	/**
	 * Gets the state of the LS button.
	 *
	 * @return the LS button state
	 */
	public boolean getButtonLS() {
		return getRawButton(8);
	}

	/**
	 * Gets the state of the RB button.
	 *
	 * @return the RB button state
	 */
	public boolean getButtonRB() {
		return getRawButton(5);
	}

	/**
	 * Gets the state of the RS button.
	 *
	 * @return the RS button state
	 */
	public boolean getButtonRS() {
		return getRawButton(9);
	}

	/**
	 * Gets the state of the Start button.
	 *
	 * @return the Start button state
	 */
	public boolean getButtonStart() {
		return getRawButton(7);
	}

	/**
	 * Gets the state of the X button.
	 *
	 * @return the X button state
	 */
	public boolean getButtonX() {
		return getRawButton(2);
	}

	/**
	 * Gets the state of the Y button.
	 *
	 * @return the Y button state
	 */
	public boolean getButtonY() {
		return getRawButton(3);
	}

	/**
	 * Gets the state of the left joy x axis.
	 *
	 * @return the left joy x axis state
	 */
	public double getLeftJoyX() {
		return getRawAxis(0);
	}

	/**
	 * Gets the state of the left joy y axis.
	 *
	 * @return the left joy y axis state
	 */
	public double getLeftJoyY() {
		return getRawAxis(1);
	}

	/**
	 * Gets the state of the right joy x axis.
	 *
	 * @return the right joy x axis state
	 */
	public double getRightJoyX() {
		return getRawAxis(4);
	}

	/**
	 * Gets the state of the right joy y axis.
	 *
	 * @return the right joy y axis state
	 */
	public double getRightJoyY() {
		return getRawAxis(5);
	}

	/**
	 * Gets the state of the left trigger.
	 *
	 * @return the left trigger state
	 */
	public double getLeftTrigger() {
		return getRawAxis(2);
	}

	/**
	 * Gets the state of the right trigger.
	 *
	 * @return the right trigger state
	 */
	public double getRightTrigger() {
		return getRawAxis(3);
	}

	/**
	 * Gets the value of the POV dpad
	 *
	 * @return the POV dpad value
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
	 * Gets the value of a given button.
	 *
	 * @param button the button to check
	 * @return the state of the given button
	 */
	public boolean getRawButton(int button) {
		return ((1 << button) & ds.getStickButtons(port)) != 0;
	}
}
