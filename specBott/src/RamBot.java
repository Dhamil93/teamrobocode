package jk;
import robocode.TeamRobot;
import robocode.Droid;
import robocode.MessageEvent;

public class RamBot extends TeamRobot implements Droid {
	private double [] values = new double[4];
	private double distance;
	private double enemyI;
	private double enemyJ;
	private double projX;
	private double projY;
	private double projDistance;
	private double projPtAngle;
	private double power;
	public void onMessageReceived(MessageEvent e){
		String message = (String) e.getMessage();
		
		String [] info = message.split(",", 4);
		
		for (int x = 0; x < 4; x++)
			values[x] = Double.parseDouble(info[x]);
	}	
	
	public double getEnemyX(){return values[0];}
	public double getEnemyY(){return values[1];}
	public double getEnemyHeading(){return convertToCorrect(values[2]);}
	public double getEnemyVelocity(){return values[3];}
	private double convertToCorrect(double angle){return (angle * (-1) + 90);}
	private double convertToIncorrect(double angle){return (angle * (-1) - 90);}
	private void findVector(){
		enemyI = getEnemyVelocity() * Math.cos(getEnemyHeading());
		enemyJ = getEnemyVelocity() * Math.sin(getEnemyHeading());
	}
	private void findProjectedPoint(){
		findVector();
		projX = getEnemyX();
		projY = getEnemyY();
		projDistance = Math.sqrt(Math.pow(getEnemyX() - projX, 2) + Math.pow(getEnemyY() - projY, 2));
		while (distance > projDistance){
			projX += enemyI;
			projY += enemyJ;
			projDistance = Math.sqrt(Math.pow(getEnemyX() - projX, 2) + Math.pow(getEnemyY() - projY, 2));
		}
		projPtAngle = Math.acos(projDistance / (projX - getX()));
	}
	private void findDistance(){distance = Math.sqrt(Math.pow(getEnemyX() - getX(), 2) + Math.pow(getEnemyY() - getY(), 2));}
	public void run(){
		test();
		while (true){
			turnToTarget();	
			ahead(50);
			findPower();
			fire(power);
		}
		
	}
	private void findPower(){
		power = Math.pow(2.8 * Math.E, Math.pow(- distance, 2) / getBattleFieldHeight()) + .02;
	}
	private void turnToTarget(){
		double heading = convertToCorrect(getHeading());
		turnRight(heading - projPtAngle);
		turnGunRight(heading - projPtAngle);
	}
public void test(){
	onMessageReceived(new MessageEvent("thing", "1,2,3,4"));
	findDistance();
	System.out.println(distance);
}

public static void main(String [] args){
	new RamBot().run();
	
}
	
}
