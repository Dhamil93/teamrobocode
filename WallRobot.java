/**
 * Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package cs;


import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;


/**
 * Corners - a sample robot by Mathew Nelson.
 * <p/>
 * This robot moves to a corner, then swings the gun back and forth.
 * If it dies, it tries a new corner in the next round.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class WallRobot extends TeamRobot implements Droid {
	int others; // Number of other robots in the game
	static int corner = 180; // Which corner we are currently using
	// static so that it keeps it between rounds.

	/**
	 * run:  Corners' main run function.
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.red);
		setGunColor(Color.black);
		setRadarColor(Color.yellow);
		setBulletColor(Color.green);
		setScanColor(Color.green);

		// Save # of other bots
		others = getOthers();

		turnRight(normalRelativeAngleDegrees(corner - getHeading()));
		// Move to a corner
		//goCorner();

		// Initialize gun turn speed to 3
		int gunIncrement = 3;

		// Spin gun back and forth
		while (true) {
			ahead(5000);
			turnGunLeft(gunIncrement);
			
			gunIncrement *= -1;
		}
	}


	/**
	 * smartFire:  Custom fire method that determines firepower based on distance.
	 *
	 * @param robotDistance the distance to the robot to fire at
	 */
	public void smartFire(double robotDistance) {
		if (robotDistance > 200 || getEnergy() < 15) {
			fire(1);
		} else if (robotDistance > 50) {
			fire(2);
		} else {
			fire(3);
		}
	}

	/**
	 * onDeath:  We died.  Decide whether to try a different corner next game.
	 */
	public void onDeath(DeathEvent e) {
		// Well, others should never be 0, but better safe than sorry.
		if (others == 0) {
			return;
		}

		// If 75% of the robots are still alive when we die, we'll switch corners.
		if ((others - getOthers()) / (double) others < .75) {
			corner += 90;
			if (corner == 270) {
				corner = -90;
			}
			out.println("I died and did poorly... switching corner to " + corner);
		} else {
			out.println("I died but did well.  I will still use corner " + corner);
		}
	
	}
	
public void onHitWall(HitWallEvent e)
{
	turnLeft(90);

}
	
	
}

