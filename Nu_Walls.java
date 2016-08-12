package md;
import robocode.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Nu_Walls - a robot by (your name here)
 */
public class Nu_Walls extends TeamRobot
{
	boolean noTurn;
	double moveAmount;

	public void run() {
		
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		noTurn = false;
		
		turnLeft(getHeading() % 90);
		ahead(moveAmount);
		noTurn = true;
		turnGunRight(90);
		turnRight(90);
		
		while(true) {
			noTurn = true;
			ahead(moveAmount);
			noTurn = false;
			turnRight(90);
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
	if (!isTeammate(e.getName())) {
	fire(2);
		if(noTurn){
		scan();
		}
	}

	}

	public void onHitRobot(HitRobotEvent e) {
		if(e.getBearing() > -90 && e.getBearing() < 90)
		{
			back(75);
		}
		else
			ahead(75);
	}
	
}
