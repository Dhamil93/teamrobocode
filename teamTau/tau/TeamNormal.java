package TAU;
import robocode.*;
import java.awt.Color;
import java.util.*;
import robocode.util.*;
import java.awt.geom.*;
// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Terminator - a robot by Dujin
 */
public class TeamNormal extends TeamRobot
{
	Random rnd = new Random();
	/**
	 * run: Terminator's default behavior
	 */
	boolean missedAlot = false;
	int missedAgain = 0;
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(new Color(255, 20, 147),new Color(255, 20, 147),new Color(255, 20, 147)); // body,gun,radar

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			ahead(randInt(100.0) + 100);
			if(getEnergy() > 4) {
			turnGunRight(360);
			}
			turnLeft(randInt(120.0)-60);
			back(randInt(100.0) + 100);
			if(getEnergy() < 4){
				ahead(1600);
			}
			if(getEnergy() > 4) {
			turnGunRight(360);
		}
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
	if(isTeammate(e.getName())) {
	return;
	}
		double bulletPower = Math.min(3.0,getEnergy());
double myX = getX();
double myY = getY();
double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
double enemyHeading = e.getHeadingRadians();
double enemyVelocity = e.getVelocity();
 
 
double deltaTime = 0;
double battleFieldHeight = getBattleFieldHeight(), 
       battleFieldWidth = getBattleFieldWidth();
double predictedX = enemyX, predictedY = enemyY;
while((++deltaTime) * (20.0 - 3.0 * bulletPower) < 
      Point2D.Double.distance(myX, myY, predictedX, predictedY)){		
	predictedX += Math.sin(enemyHeading) * enemyVelocity;	
	predictedY += Math.cos(enemyHeading) * enemyVelocity;
	if(	predictedX < 18.0 
		|| predictedY < 18.0
		|| predictedX > battleFieldWidth - 18.0
		|| predictedY > battleFieldHeight - 18.0){
		predictedX = Math.min(Math.max(18.0, predictedX), 
                    battleFieldWidth - 18.0);	
		predictedY = Math.min(Math.max(18.0, predictedY), 
                    battleFieldHeight - 18.0);
		break;
	}
}
double theta = Utils.normalAbsoluteAngle(Math.atan2(
    predictedX - getX(), predictedY - getY()));
 
setTurnRadarRightRadians(
    Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
fire(bulletPower);
}
	public void onHitByBullet(HitByBulletEvent e) {
		if(90-e.getBearing() < 90+e.getBearing()){
			turnLeft(90 - e.getBearing());
		}
		else{
			turnRight(90+e.getBearing());
			}
		back(randInt(200)-100);
		}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		/*if(!(e.getBearing() < -90) && !(e.getBearing() > 90)) {
			back(20);
		}
		else {
			ahead(20);
		}*/
		turnLeft(90-e.getBearing());
		if(getEnergy() > 4){
		ahead(randInt(200)+200);
		turnLeft(90);
		ahead(100);
		}
		if(getEnergy() <=4){
			ahead(800);
			}
	}
	public void onRoundEnded(RoundEndedEvent e) {
		missedAgain = 0;
		missedAlot = false;
	}
	public double randInt(double n) {
		return rnd.nextDouble() * n;
	}
	 public void onHitRobot(HitRobotEvent event) {
		 	if(isTeammate(event.getName())) {
	return;
	}
		 if(90-event.getBearing() <= 90+event.getBearing()){
				turnLeft(90 - event.getBearing());
				turnGunLeft(-90 + getGunHeading() - getHeading());
			}
			else{
				turnRight(90+event.getBearing());
				turnGunRight(-90 -getGunHeading()+getHeading());
			}
			fire(3);
			fire(3);
			double whichWay = randInt(2.0)-1;
			if(whichWay > 0){
				ahead(200);
			}
			else{
				back(200);
			}
	}	
}