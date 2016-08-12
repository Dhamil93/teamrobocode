//Spec v1.0 by Ke Shen

//A robot developed in the last 2 days before this project was due: 11/19/2015
//accomplished by utilizing the tens of hours working on past robots and 
//implementing algorithms found here

//Tutorials on MinimumRiskMovement were perused to make the MRM for this robot
//Originally tried to make this a MiniBot by minimizing code side everywhere,
//realized need to be a better programmer to write code 'efficiently'

import robocode.*;
import robocode.util.Utils;

import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class Spec extends AdvancedRobot {
	static Point2D myLocation, last;
	static EnemyInfo currentTarget;
	static EnemyInfo timeModifier;
	static Hashtable enemies;
	static boolean linearTarget = false;
	static int missedTimes = 0;
	static int bulletsFired = 0;
	static LinkedHashMap<String, Double> enemyHashMap;
	static double scanDir;
	static Object sought;

	public void run() {
		System.out.println("Missed times: " + missedTimes);
		scanDir = 1;
	    enemyHashMap = new LinkedHashMap<String, Double>(5, 2, true);
	 
		
		setColors(Color.gray, null, Color.red);
		enemies = new Hashtable();
		Point2D next = currentTarget = null;
		do {
			setTurnRadarRight(scanDir * Double.POSITIVE_INFINITY);
			myLocation = new Point2D.Double(getX(), getY());
			// if there is a current target
			if (currentTarget != null) {
				if (next == null)
					next = last = myLocation;
				boolean changed = false;
				double angle = 0, distance;
				do {
					Point2D p;
					if (new Rectangle2D.Double(30, 30,
							getBattleFieldWidth() - 60,
							getBattleFieldHeight() - 60)
							// utilizes the projectPoint method to find the
							// enemy's
							// coordinates
							.contains(p = projectPoint(myLocation, angle, Math
									.min((distance = myLocation
											.distance(currentTarget)) / 2, 300)))
							&& findRisk(p) < findRisk(next)) {
						changed = true;
						next = p;
					}
					angle += .1;
				} while (angle < Math.PI * 2);
				// this helps the whole movement system know what direction I'm
				// going, and avoid head-on targeting:
				if (changed) {
					// if MY location has changed (usually), my previously
					// current location
					// becomes my last locaiton
					last = myLocation;
				}
				if(getGunTurnRemaining() == 0) {
				if (linearTarget == true) {
					// calculation of what power to use
					// won't fire at all if my energy is too low
					// and the enemy is very far away=
					// low reward for high risk
					if (getEnergy() / distance > .005) {
						setFire(60
								* Math.min(currentTarget.energy, getEnergy())
								/ distance);
					}
					setTurnGunRightRadians(robocode.util.Utils
							.normalRelativeAngle(angle(currentTarget,
									myLocation) - getGunHeadingRadians()));
				} else {
					setTurnGunRightRadians(linearTargeting());
					setFire(60
							* Math.min(currentTarget.energy, getEnergy())
							/ distance);
				}
				}
				double turn;
				if (Math.cos(turn = angle(next, myLocation)
						- getHeadingRadians()) < 0) {
					turn += Math.PI;
					distance = -distance;
					// to stop linear targeters
				}
				setTurnRightRadians(robocode.util.Utils
						.normalRelativeAngle(turn));
				setAhead((Math.abs(getTurnRemainingRadians()) > 1) ? 0
						: distance);
			}
			// updates the lastseen time to allow radar to spin toward the robot
			// it has not seen in the longest time
			scan();
			execute();
		} while (true);
	}

	private double findRisk(Point2D point) {
		// the greater new point is from distance away from the point I was at
		// previously
		// (not the current point), the lower the risk
		// this is because I moved away from my old point because obviously it
		// was more risky
		// than the point I traveled to and am at right now
		// Altogether, it encourages the robot to keep moving (and get hit
		// less!)
		// by assigning risk to its previous points
		double risk = 4 / last.distanceSq(point) + .1
				/ myLocation.distanceSq(point);

		Enumeration enum1 = enemies.elements();
		do {
			EnemyInfo e;
			// start with an anti-gravity-type value that calculates the risk
			// based on energy and distance of enemybots

			double thisrisk = Math.max(getEnergy(),
					(e = (EnemyInfo) enum1.nextElement()).energy)
					/ point.distanceSq(e);

			int closer = 0;
			Enumeration enum2 = enemies.elements();
			do
				// a reaction factor to responding to enemy bots near the point
				// I am planning to travel to
				// if an enemy is far enough, I add one to closer, which
				// combined with other bots that are far away
				// may raise closer above one, not triggering the if statement
				// below, increasing the risk
				if (.9 * e.distance((EnemyInfo) enum2.nextElement()) > e
						.distance(point)) {
					closer++;
				}
			while (enum2.hasMoreElements());

			// if a large number of enemies are close (so the closer variable is
			// small or I hit this enemy not more than 2 ticks ago (making it
			// likely to target me)
			// or if the enemy within range has
			if (closer <= 1 || e.lastHit > getTime() - 200
					|| e == currentTarget) {

				thisrisk *= 1 + Math.abs(Math.cos(angle(myLocation, point)
						- angle(e, myLocation)));
			}
			risk += thisrisk;
		} while (enum1.hasMoreElements());
		return risk;
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		String name;
		EnemyInfo enemy = (EnemyInfo) enemies.get(name = e.getName());
		// if the enemy is not already on the hashtable, puts it on
		if (enemy == null) {
			enemies.put(name, enemy = new EnemyInfo());
		}
		// Point2D objects simplify this so much
		// mass collection of data can be easily done
		// without need for almost 100 lines of code
		// written in an extremely confusing manner
		enemy.bearing = e.getBearing();
		enemy.velocity = e.getVelocity();
		enemy.heading = e.getHeading();
		enemy.energy = e.getEnergy();
		enemy.lastSeen = 0;
		// advanceTime(timeModifier);
		enemy.setLocation(projectPoint(myLocation,
				getHeadingRadians() + e.getBearingRadians(), e.getDistance()));
		if (currentTarget == null
				|| targetability(enemy) < targetability(currentTarget) - 100) {
			currentTarget = enemy;
		}
		
		LinkedHashMap<String, Double> ehm = enemyHashMap;
		 
	    ehm.put(name, getHeadingRadians() + e.getBearingRadians());
	 
	    if ((name == sought || sought == null) && ehm.size() == getOthers()) {
		scanDir = Utils.normalRelativeAngle(ehm.values().iterator().next()
	            - getRadarHeadingRadians());
	        sought = ehm.keySet().iterator().next();
	    }
	 
	}

	/*
	 * public void MeleeRadar(EnemyInfo e) { double time = 0; Enumeration enum4
	 * = enemies.elements(); while(enum4.hasMoreElements()) { EnemyInfo
	 * enemyInfo = (EnemyInfo) (enum4.nextElement()); if(enemyInfo.lastSeen >
	 * time) { time = enemyInfo.lastSeen; } } if(enemyInfo.lastSeen)
	 * 
	 * double scanDir = 1; }
	 */
	public static void advanceTime(EnemyInfo e) {
		Enumeration enum3 = enemies.elements();
		while (enum3.hasMoreElements()) {
			EnemyInfo enemyInfo = (EnemyInfo) (enum3.nextElement());
			enemyInfo.lastSeen += 1;
		}
	}

	public static double targetability(EnemyInfo e) {
		return myLocation.distance(e) - e.energy;
	}

	public void onHitByBullet(HitByBulletEvent e) {
		try {
			((EnemyInfo) enemies.get(e.getName())).lastHit = getTime();
		} catch (NullPointerException ex) {
		}
	}

	public void onBulletMissed(BulletMissedEvent e) {
		missedTimes++;
		System.out.println("Missed Times: " + missedTimes);
		if (missedTimes > 5) {
			linearTarget = !linearTarget;
			System.out.println("switched");
			missedTimes = 0;
		}
	}

	public void onBulletHit(BulletHitEvent e) {
		missedTimes--;
	}

	public void onRobotDeath(RobotDeathEvent e) {
		// the EnemyInfo objects are meant to sacrificed to the great Robocode
		// god
		if (currentTarget == enemies.remove(e.getName())) {
			currentTarget = null;
		}
		enemyHashMap.remove(e.getName());
	    sought = null;
	}

	// utility functions below:

	// calculates a point's coordinates given a starting set of coordinates
	// and the angle it is at from the normal
	// (calculated by getHeadingRadians() and getBeraingRadians)
	// and the distance between the intended point and the starting point
	private static Point2D projectPoint(Point2D startPoint, double theta,
			double dist) {
		return new Point2D.Double(startPoint.getX() + dist * Math.sin(theta),
				startPoint.getY() + dist * Math.cos(theta));
	}

	// returns the angle formed by the line connecting these two points with the
	// normal (-pi/2 -> pi/2)
	public static double angle(Point2D point2, Point2D point1) {
		return Math.atan2(point2.getX() - point1.getX(),
				point2.getY() - point1.getY());
	}

	public double linearTargeting() {
		double calculatedPower = Math.min(3, 2 * Math.log10(getEnergy()));
		double bulletPower = Math.max(0.2, calculatedPower);
		double bulletVelocity = 20 - 3 * bulletPower;
		double myX = getX();
		double myY = getY();
		double enemyX = 0;
		double enemyY = 0;
		double battleFieldWidth = getBattleFieldWidth();
		double battleFieldHeight = getBattleFieldHeight();
		// the direction an enemy is facing in radians
		double enemyHeading = 0;
		// the velocity of an enemy
		double enemyVelocity = 0;
		// the angle the robot's heading is between another robot
		double enemyBearing = 0;
		// an angle between -pi and pi that an enemy robot is from the vertical
		// y-axis
		double enemyDistance = 0;
		// enemy.setLocation(projectPoint(myLocation,

		/*
		 * getHeadingRadians() + e.getBearingRadians(), e.getDistance())); if
		 * (currentTarget == null || targetability(enemy) <
		 * targetability(currentTarget) - 100) { currentTarget = enemy; }
		 */

		enemyX = currentTarget.x;
		enemyY = currentTarget.y;
		double predictedX = enemyX;
		double predictedY = enemyY;
		double deltaTime = 0;
		System.out.println("Heading: " + currentTarget.heading);
		System.out.println("velocity: " + currentTarget.velocity);
		while ((++deltaTime) * (bulletVelocity) < Point2D.Double.distance(myX,
				myY, predictedX, predictedY)) {
			predictedX += Math.sin(currentTarget.heading)
					* currentTarget.velocity;
			predictedY += Math.cos(currentTarget.heading)
					* currentTarget.velocity;
		}
			if (predictedX < 18.0 || predictedY < 18.0
					|| predictedX > battleFieldWidth - 18.0
					|| predictedY > battleFieldHeight - 18.0) {
				// the first Math.max prevents aiming beyond the battlefield
				// (robot width is 36 pixels and so it aims for the center,
				// 18pixels off from the wall
				predictedX = Math.min(Math.max(18.0, predictedX),
						battleFieldWidth - 18.0);
				predictedY = Math.min(Math.max(18.0, predictedY),
						battleFieldHeight - 18.0);
		}
		// set these equal to the current enemyX and enemyY for now and add

		double theta = Utils.normalRelativeAngle(Math.atan2(predictedX - myX,
				predictedY - myY));
		return(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
//		setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
		}
	}


