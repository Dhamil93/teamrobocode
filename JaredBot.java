
package man;

import java.awt.*;

import robocode.*;

public class JaredBot extends TeamRobot {
	boolean peek; // Don't turn if there's a robot there
	double moveAmount; // How much to move

	boolean lastStand = false;
	boolean closeToWall = false;

	/**
	 * run: Move around the walls
	 */
	public void run() {
		// Set colors
		setColors(new Color(255, 0, 158), new Color(255, 0, 158), new Color(255, 0, 158));
		setMaxVelocity(10);

		// Initialize moveAmount to the maximum possible for this battlefield.
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		// Initialize peek to false
		peek = false;

		// turnLeft to face a wall.
		// getHeading() % 90 means the remainder of
		// getHeading() divided by 90.
		turnLeft(getHeading() % 90);
		ahead(moveAmount);
		// Turn the gun to turn right 90 degrees.
		peek = true;
		turnGunRight(90);
		turnRight(90);

		while (true) {
			if (this.getEnergy() < 30) {
				// Look before we turn when ahead() completes.
				peek = true;
				// Move up the wall
				ahead(moveAmount);
				// Don't look now
				peek = false;
				// Turn to the next wall
				turnLeft(270);

				lastStand = false;
			} else {
				// Look before we turn when ahead() completes.
				peek = true;
				// Move up the wall
				ahead(moveAmount);
				// Don't look now
				peek = false;
				// Turn to the next wall
				turnRight(90);

				lastStand = false;
			}
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		// If he's in front of us, set back up a bit.
		if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
		} // else he's in back of us, so set ahead a bit.
		else {
			ahead(100);
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (!isTeammate(e.getName())) {
			double distance = e.getDistance();

			if (distance < 200) {
				fire(3);
			} else if (distance < 400) {
				fire(2);
			} else {
				fire(1);
			}

			if (peek) {
				scan();
			}
		}
	}

	public void onScannedWall() {

	}
}
