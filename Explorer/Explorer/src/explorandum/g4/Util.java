package explorandum.g4;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Util {
  public static void debugMessage(String type, String msg) {
    if (
        type.equals("move") ||
        type.equals("null target") ||
        type.equals("shortest path") ||
        //type.equals("explore") ||
        //type.equals("openness") ||
       false) {
      //System.out.println(type + ": " + msg);
    }
  }


  public static ArrayList<Point> getCellsBetween(Point c1, Point c2) {
		ArrayList<Point> cells = new ArrayList<Point>();
		Point2D.Double p1 = new Point2D.Double(c1.x + 0.5, c1.y + 0.5);// explorer location
		Point2D.Double p2 = new Point2D.Double(c2.x + 0.5, c2.y + 0.5);// cell to be checked

		// slope
		double m = (p1.x - p2.x) / (p1.y - p2.y);

		int start = 0;
		int end = 0;
		if (p1.y < p2.y)
		{
			start = (int) Math.ceil(p1.y);
			end = (int) Math.floor(p2.y);
		} else
		{
			start = (int) Math.ceil(p2.y);
			end = (int) Math.floor(p1.y);
		}

		if (p1.y == p2.y)
		{
			int j = c1.y;
			start = Math.min(c1.x, c2.x) + 1;
			end = Math.max(c1.x, c2.x);

			for (int i = start; i < end; i++)
				cells.add(new Point(i, j));
			return cells;
		}

		// Loop between possible Y values
		for (int i = start; i <= end; i++)
		{
			// Calculate X value of intersection with cell
			double x = m * (i - p1.y) + p1.x;

			// if Diagonal intersection
			if (Math.floor(x) == x)
			{
				// Add Cell above left of point of intersection
				cells.add(new Point((int) Math.floor(x - 1), i));

				// Add Cell below left of cell of intersection
				cells.add(new Point((int) Math.floor(x - 1), i - 1));
			}

			// Add cell below point of intersection
			cells.add(new Point((int) Math.floor(x), i - 1));

			// Add cell above point of intersection
			cells.add(new Point((int) Math.floor(x), i));
		}
		
		
		//now process x-axis intersections
		if (p1.x < p2.x)
		{
			start = (int) Math.ceil(p1.x);
			end = (int) Math.floor(p2.x);
		} else
		{
			start = (int) Math.ceil(p2.x);
			end = (int) Math.floor(p1.x);
		}

		// Loop between possible X values
		for (int i = start; i <= end; i++)
		{
			// Calculate Y value of intersection with cell
			double y = (i - p1.x) / m + p1.y;

			// Add cell left to point of intersection
			cells.add(new Point(i - 1, (int) Math.floor(y)));

			// Add cell right to point of intersection
			cells.add(new Point(i, (int) Math.floor(y)));
		}

		return cells;
	}

}
