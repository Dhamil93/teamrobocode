package Epsilon;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * ER - a robot by (your name here)
 */
public class ER extends TeamRobot
{
	/**
	 * run: ER's default behavior
	 */
	private double x = 0;
	private double y = 0;
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop

		while(true) {
			// Replace the next 4 lines with any behavior you would like
			setTurnRadarRight(10000);
			back(100);
			if (x!=0) shoot();
			ahead(100);
			if (x!=0) shoot();
			
			
		}
	}
	
	private void shoot(){
		double dx = x - this.getX();
		double dy = y - this.getY();
		// Calculate angle to target
		double theta = Math.toDegrees(Math.atan2(dx, dy));
	
		// Turn gun to target
		turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
		// Fire hard!
		fire(3);
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Don't fire on teammates
		if (isTeammate(e.getName()))
			return;
		
		// Calculate enemy bearing
		double enemyBearing = this.getHeading() + e.getBearing();
		// Calculate enemy's position
		x = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
		y = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
	}
	
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}
}
