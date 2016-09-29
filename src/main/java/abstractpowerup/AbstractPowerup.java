package abstractpowerup;

import java.util.List;

/**
 * This class represents a powerup as held by a player.
 * @author Dario
 *
 */
public class AbstractPowerup {
	/**
	 * The type of the powerup.
	 * Can be
	 * 0 (additional life, not currently active),
	 * 1 (shield, not currently active),
	 * 2 (bullet size increase),
	 * 3 (piercing bullets),
	 * 4 (minigun)
	 * or 5 (triple-shot, not implemented yet).
	 */
	private int powerupType;
	/**
	 * The remaining duration of the powerup.
	 */
	private final int powerupDuration = 5000;
	/**
	 * When the powerup started.
	 */
	private long startTime;
	/**
	 * How much of a boost to fire rate it gives. Standard value is 1.
	 */
	private double FIRE_RATE_BOOST = 1;
	/**
	 * The fire rate boost given by a minigun powerup.
	 */
	private final double FIRE_RATE_BOOST_MINIGUN = 0.3;
	/**
	 * How much of a boost to pierce rate it gives. Standard value is 0.
	 */
	private int PIERCE_RATE_BOOST = 0;
	/**
	 * The boost to pierce rate the piercing bullet powerup gives.
	 */
	private final int PIERCE_RATE_BOOST_PIERCING = 3;
	/**
	 * How much of a boost to bullet size it gives. Standard value is 1.
	 */
	private int SIZE_BOOST = 1;
	/**
	 * The boost the big bullet powerup gives to bullet size.
	 */
	private final int SIZE_BOOST_BIG = 5;
	
	/**
	 * How much of a boost to the max number of bullets it gives.
	 * Standard value is 1.
	 */
	private int NUMB_BOOST = 1;
	/**
	 * The boost the minigun powerup gives to the max number of bullets.
	 */
	private final int NUMB_BOOST_MINIGUN = 3;
	
	/**
	 * Constructor for the AbstractPowerup class
	 * @param type the type of powerup it is.
	 */
	public AbstractPowerup(final int type) {
		powerupType = type;
		startTime = System.currentTimeMillis();
		switch(type) {
			case 2:SIZE_BOOST = SIZE_BOOST_BIG;
			break;
			case 3:PIERCE_RATE_BOOST = PIERCE_RATE_BOOST_PIERCING;
			break;
			case 4:FIRE_RATE_BOOST = FIRE_RATE_BOOST_MINIGUN;
			NUMB_BOOST = NUMB_BOOST_MINIGUN;
			break;
			default:
		}
	}
	
	/**
	 * Checks whether the powerup has expired or not.
	 */
	public final boolean powerupOver() {
		return (powerupDuration < (System.currentTimeMillis() - startTime));
	}
	
	/**
	 * Getter for the fire rate boost of the powerup.
	 */
	public final double getRateMult() {
		return FIRE_RATE_BOOST;
	}
	
	/**
	 * Getter for the number of bullets boost of the powerup.
	 */
	public final int getNumbMult() {
		return NUMB_BOOST;
	}
	
	/**
	 * Getter for the pierce boost of the powerup.
	 */
	public final int getPierceRateBoost() {
		return PIERCE_RATE_BOOST;
	}

	/**
	 * Getter for the size boost of the powerup.
	 */
	public final int getSizeBoost() {
		return SIZE_BOOST;
	}

}