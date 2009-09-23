package explorandum.f09.g1;

import java.awt.Point;

public class Utilities {

	public static int euclidDistance(Point p1, Point p2) {		
		double tempval= Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2);
		return (int)Math.floor(Math.sqrt(tempval));
	}
	
	public static double getAngle(Point a, Point b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double angle = 0.0d;
 
        if (dx == 0.0) {
            if(dy == 0.0)     angle = 0.0;
            else if(dy > 0.0) angle = Math.PI / 2.0;
            else              angle = (Math.PI * 3.0) / 2.0;
        }
        else if(dy == 0.0) {
            if(dx > 0.0)      angle = 0.0;
            else              angle = Math.PI;
        }
        else {
            if(dx < 0.0)      angle = Math.atan(dy/dx) + Math.PI;
            else if(dy < 0.0) angle = Math.atan(dy/dx) + (2*Math.PI);
            else              angle = Math.atan(dy/dx);
        }
        return ((angle * 180) / Math.PI);
    }
	
	public static int getHeading(Point a, Point b) {
		int angle = (int)Math.floor(getAngle(a, b));
		
		if(angle >= 0 && angle < 45) {
			return 3;
		} else if(angle >= 45 && angle < 90) {
			return 2;
		} else if(angle >= 90 && angle < 135) {
			return 1;
		} else if(angle >= 135 && angle < 180) {
			return 8;
		} else if(angle >= 180 && angle < 225) {
			return 7;
		} else if(angle >= 225 && angle < 270) {
			return 6;
		} else if(angle >= 270 && angle < 315) {
			return 5;
		} else if(angle >= 315 && angle < 360) {
			return 4;
		} else {
			return 3;
		}
	}
	
	public static Cell checkMemory(Map memory, Map view, Point p) {
		
		if(memory.getMapExplored().containsKey(p)) {
			return memory.getMapExplored().get(p);
		} 
		else if(view.getMapExplored().containsKey(p)) {
			return view.getMapExplored().get(p);
		} 
		else {
			return null;
		}
		
	}
}
