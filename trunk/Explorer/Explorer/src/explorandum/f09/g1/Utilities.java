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
		
		switch(angle) {
			case 0: 
				return 3;
			case 45:
				return 2;
			case 90:
				return 1;
			case 135:
				return 8;
			case 180:
				return 7;
			case 225:
				return 6;
			case 270:
				return 5;
			case 315:
				return 4;
			case 360:
				return 3;
			default:
				System.out.println("angle: " + angle + ", entering default");
				return 0;
		}
	}
	
	public static Cell checkMemory(Map memory, Map view, Point p) {
		
		if(memory.getMapExplored().containsKey(p)) {
			return memory.getMapExplored().get(p);
		} else if(view.getMapExplored().containsKey(p)) {
			return view.getMapExplored().get(p);
		} else {
			return null;
		}
		
	}
}
