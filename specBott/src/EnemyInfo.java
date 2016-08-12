
import java.awt.geom.*;
import java.util.*;

public class EnemyInfo extends Point2D.Double {
	// EnemyInfo
	// Represents location and status of enemies

	long lastHit;
	double energy;
	double bearing;
	double heading;
	double velocity;
	double lastSeen;
}