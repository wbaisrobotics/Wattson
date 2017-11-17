package org.usfirst.frc.team4338.robot;

import edu.wpi.first.wpilibj.Joystick;
/**
 * Controls all of the data coming from the controllers
 * @author Orian Leitersdorf
 *
 */
public class JoystickController extends Joystick {
	
	// Identifiers for sensitive controls
	
	private static final int LEFT_JOY_X_IDENTIFIER = 0; // Left Joy X
	private static final int LEFT_JOY_Y_IDENTIFIER = 1; // Left Joy Y
	private static final int RIGHT_JOY_X_IDENTIFIER = 4; // Right Joy X
	private static final int RIGHT_JOY_Y_IDENTIFIER = 5; // Right Joy Y
	private static final int LEFT_TRIGGER_IDENTIFIER = 2; // Left Trigger
	private static final int RIGHT_TRIGGER_IDENTIFIER = 3; // Right Trigger
	
	// Identifiers for buttons
	
	private static final int X_BUTTON_IDENTIFIER = 1;	 // X
	private static final int A_BUTTON_IDENTIFIER = 2;	 // A
	private static final int Y_BUTTON_IDENTIFIER = 3; // Y
	private static final int B_BUTTON_IDENTIFIER = 4; // B
	private static final int LB_BUTTON_IDENTIFIER = 5; // Left Bumper
	private static final int RB_BUTTON_IDENTIFIER = 6; // Right Bumper
	private static final int LS_BUTTON_IDENTIFIER = 9; // Left Stick
	private static final int RS_BUTTON_IDENTIFIER = 10; // Right Stick
	private static final int BACK_BUTTON_IDENTIFIER = 11; // Back
	private static final int START_BUTTON_IDENTIFIER = 12; // Start

	// POV Constants
	
	private static final int POV_NORTH = 0; // North
	private static final int POV_NORTHEAST = 45; // Northeast
	private static final int POV_EAST = 90; // East
	private static final int POV_SOUTHEAST = 135; // Southeast
	private static final int POV_SOUTH = 180; // South
	private static final int POV_SOUTHWEST = 225; // Southwest
	private static final int POV_WEST = 270; // West
	private static final int POV_NORTHWEST = 315; // Northwest
	private static final int POV_NO_SELECTION = -1; // No Selection
	
	
	
	/**
	 * Initializes the joystick controller using the given port
	 * @param port
	 */
	public JoystickController(int port) {
		super(port);
	}
	
	
	
	/**
	 * Returns the value of the leftJoyXAxis
	 * @return
	 */
	public double getLeftJoyXAxis() {
		return super.getRawAxis(LEFT_JOY_X_IDENTIFIER);
	}
	
	/**
	 * Returns the value of the leftJoyYAxis
	 * @return
	 */
	public double getLeftJoyYAxis() {
		return super.getRawAxis(LEFT_JOY_Y_IDENTIFIER);
	}
	
	/**
	 * Returns the value of the rightJoyXAxis
	 * @return
	 */
	public double getRightJoyXAxis() {
		return super.getRawAxis(RIGHT_JOY_X_IDENTIFIER);
	}
	
	/**
	 * Returns the value of the rightJoyYAxis
	 * @return
	 */
	public double getRightJoyYAxis() {
		return super.getRawAxis(RIGHT_JOY_Y_IDENTIFIER);
	}
	
	/**
	 * Returns the value of the leftTrigger
	 * @return
	 */
	public double getLeftTrigger() {
		return super.getRawAxis(LEFT_TRIGGER_IDENTIFIER);
	}
	
	/**
	 * Returns the value of the rightTrigger
	 * @return
	 */
	public double getRightTrigger() {
		return super.getRawAxis(RIGHT_TRIGGER_IDENTIFIER);
	}
	
	
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getXButton() {
		return super.getRawButton(X_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getAButton() {
		return super.getRawButton(A_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getYButton() {
		return super.getRawButton(Y_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getBButton() {
		return super.getRawButton(B_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getLBButton() {
		return super.getRawButton(LB_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getRBButton() {
		return super.getRawButton(RB_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getLSButton() {
		return super.getRawButton(LS_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getRSButton() {
		return super.getRawButton(RS_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getBackButton() {
		return super.getRawButton(BACK_BUTTON_IDENTIFIER);
	}
	
	/**
	 * Returns the status of the button
	 * @return
	 */
	public boolean getStartButton() {
		return super.getRawButton(START_BUTTON_IDENTIFIER);
	}

	/**
	 * Returns the current state of POV in the enum POV format. Note there is a difference between no selection and error.
	 * @return
	 */
	public POV getPOVEnum() {
		
		switch(super.getPOV()) {
		
		case POV_NORTH:
			return POV.NORTH;
			
		case POV_NORTHEAST:
			return POV.NORTHEAST;
			
		case POV_EAST:
			return POV.EAST;
			
		case POV_SOUTHEAST:
			return POV.SOUTHEAST;
			
		case POV_SOUTH:
			return POV.SOUTH;
			
		case POV_SOUTHWEST:
			return POV.SOUTHWEST;
			
		case POV_WEST:
			return POV.WEST;
			
		case POV_NORTHWEST:
			return POV.NORTHWEST;
			
		case POV_NO_SELECTION:
			return POV.NOSELECTION;
			
		default:
			return POV.ERROR;
			
		}
		
		
	}
	

}
