package explorandum.f09.g1;

import java.awt.Point;

public class Utilities {

	public static int euclidDistance(Point p1, Point p2) {		
		double tempval= Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2);
		return (int)Math.floor(Math.sqrt(tempval));
	}
}
