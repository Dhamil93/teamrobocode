package nu;
import robocode.*;
import java.awt.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class NicholasRobot1 extends TeamRobot {
	int moveDirection=1;//which way to move
	boolean trackingStarted = false;
	boolean tracking = false;
	boolean movingForward; // Is set to true when setAhead is called, set to false on setBack
	boolean inWall; // Is true when robot is near the wall.
	/**
	 * run:  Tracker's main run function
	 */
	public void run() {
		tracking = false;
		setColors(new Color(255,0,158), new Color(255,0,158), new Color(255,0,158));
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		// Check if the robot is closer than 50px from the wall.
		if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50) {
			inWall = true;
		} else {
			inWall = false;
		}
		setAhead(40000); // go ahead until you get commanded to do differently
		setTurnRadarRight(360); // scan until you find your first enemy
		movingForward = true; // we called setAhead, so movingForward is true
		while(true){
			if(!tracking){
				if (getX() > 50 && getY() > 50 && getBattleFieldWidth() - getX() > 50 && getBattleFieldHeight() - getY() > 50 && inWall == true) {
					inWall = false;
				}
				if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50 ) {
					if ( inWall == false){
						reverseDirection();
						inWall = true;
					}
				}

				// If the radar stopped turning, take a scan of the whole field until we find a new enemy
				if (getRadarTurnRemaining() == 0.0){
					setTurnRadarRight(360);
				}

				execute(); // execute all actions set.
				if(getOthers()<=2){
					tracking = true;
				}
			}
			if(!trackingStarted && tracking){
				trackingStarted = true;
				setAdjustRadarForRobotTurn(true);//keep the radar still while we turn
				setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
				turnRadarRightRadians(Double.POSITIVE_INFINITY);//keep turning radar right
			}
		}
	}


	public void onScannedRobot(ScannedRobotEvent e) {
		if(tracking){
			double absBearing=e.getBearingRadians()+getHeadingRadians();//enemies absolute bearing
			double latVel=e.getVelocity() * Math.sin(e.getHeadingRadians() -absBearing);//enemies later velocity
			double gunTurnAmt;//amount to turn our gun
			setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar
			if(Math.random()>.9){
				setMaxVelocity((12*Math.random())+12);//randomly change speed
			}
			if (e.getDistance() > 150) {//if distance is greater than 150
				gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/22);//amount to turn our gun, lead just a little bit
				setTurnGunRightRadians(gunTurnAmt); //turn our gun
				setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));//drive towards the enemies predicted future location
				setAhead((e.getDistance() - 140)*moveDirection);//move forward
				setFire(3);//fire
			}
			else{//if we are close enough...
				gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/15);//amount to turn our gun, lead just a little bit
				setTurnGunRightRadians(gunTurnAmt);//turn our gun
				setTurnLeft(-90-e.getBearing()); //turn perpendicular to the enemy
				setAhead((e.getDistance() - 140)*moveDirection);//move forward
				setFire(3);//fire
			}	
		} else {
			// Calculate exact location of the robot
			double absoluteBearing = getHeading() + e.getBearing();
			double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
			double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());

			//Spiral around our enemy. 90 degrees would be circling it (parallel at all times)
			// 80 and 100 make that we move a bit closer every turn.
			if (movingForward){
				setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 80));
			} else {
				setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 100));
			}


			// If it's close enough, fire!
			if (Math.abs(bearingFromGun) <= 4) {
				setTurnGunRight(bearingFromGun); 
				setTurnRadarRight(bearingFromRadar); // keep the radar focussed on the enemy
				// We check gun heat here, because calling fire()
				// uses a turn, which could cause us to lose track
				// of the other robot.

				// The close the enmy robot, the bigger the bullet. 
				// The more precisely aimed, the bigger the bullet.
				// Don't fire us into disability, always save .1
				if (getGunHeat() == 0 && getEnergy() > .2) {
					fire(Math.min(4.5 - Math.abs(bearingFromGun) / 2 - e.getDistance() / 250, getEnergy() - .1));
				} 
			} // otherwise just set the gun to turn.
			// 
			else {
				setTurnGunRight(bearingFromGun);
				setTurnRadarRight(bearingFromRadar);
			}
			// Generates another scan event if we see a robot.
			// We only need to call this if the radar
			// is not turning.  Otherwise, scan is called automatically.
			if (bearingFromGun == 0) {
				scan();
			}
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		// If we're moving the other robot, reverse!
		if(tracking){
			moveDirection=-moveDirection;//reverse direction upon hitting a wall
		} else {
			reverseDirection();
		}

	}

	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}


	public void onHitWall(HitWallEvent e){
		if(tracking){
			moveDirection=-moveDirection;//reverse direction upon hitting a wall
		} else {
			reverseDirection();
		}
	}
	/**
	 * onWin:  Do a victory dance
	 */
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}
}
