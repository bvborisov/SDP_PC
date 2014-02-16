package world;

import world.object.MobilePixelObject;
import world.object.StationaryPixelObject;

/*
 * An interface defining what methods will be available to get a representation of the world. Pixel 
 * implies that everything is exactly as it appears in the image and the primary unit is pixels.
 */
public interface PixelWorld {
	// Integers constants to be used to reference the various moving elements in
	// the world.
	// (I.e. the four robots and the ball)
	public static final int YELLOW_ATTACKER = 20;
	public static final int YELLOW_DEFENDER = 21;
	public static final int BLUE_ATTACKER = 22;
	public static final int BLUE_DEFENDER = 23;
	public static final int BALL = 24;

	// Integers constants to be used to reference the various stationary
	// elements in the world.
	// (I.e. the four zones and the two goals)
	public static final int LEFT_TEAM_DEFENDER_ZONE = 20;
	public static final int RIGHT_TEAM_ATTACKER_ZONE = 21;
	public static final int LEFT_TEAM_ATTACKER_ZONE = 22;
	public static final int RIGHT_TEAM_DEFENDER_ZONE = 23;

	public static final int LEFT_TEAM_GOAL = 24;
	public static final int RIGHT_TEAM_GOAL = 25;

	/**
	 * Get one of the mobile objects in the world.
	 * 
	 * @param object
	 *            The integer that identifies the object of interest. Options
	 *            are defined as constants in this class.
	 * 
	 * @return Returns the mobile object.
	 */
	public MobilePixelObject getMobileObject(int object);

	/**
	 * Get one of the stationary objects in the world.
	 * 
	 * @param object
	 *            The integer that identifies the object of interest. Options
	 *            are defined as constants in this class.
	 * 
	 * @return Returns the mobile object.
	 */
	public StationaryPixelObject getStationaryObject(int object);
}