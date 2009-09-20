package explorandum.g4;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;
import java.util.ArrayList;

import explorandum.GameConstants;

public abstract class Map {
  protected int time;
  protected int num_rounds;

  // Add what the player sees to the map.
  public abstract void add(int explorerId, Point currentLocation, Point[] offsets,
                  Boolean[] hasExplorer,
                  Integer[][] otherExplorers, Integer[] terrain, int time, int range);
  
  public abstract Point getmin();
  
  public abstract Point getmax();
  
  // Get the current bound of the map.
  public abstract Rectangle getBound();

  // Get the cell at (x, y). The coordinates are relative to the start point.
  public abstract Cell getCell(int x, int y);

  // Get the cell at p. The coordinates are relative to the start point.
  public abstract Cell getCell(Point p);

  // Get the coordinates of all discovered cells
  public abstract Set<Point> getAllDiscoveredCoordinates();

  // Print debug message to stdout
  public abstract void printDebugMsg();

  // Get a list of neighbors in some range. This function does consider
  // mountains as occluders.
  public ArrayList<Point> getNeighbors(Point p, double range) {
    ArrayList<Point> neighbors = new ArrayList<Point>();
    for (int x = (int) (p.x - range); x <= p.x + range; x++) {
      for (int y = (int) (p.y - range); y <= p.y + range; y++) {
        Point neighbor_point = new Point(x, y);
        if (p.distance(neighbor_point) <= range + 0.00001) {
          // check if there is a mountin in between
          boolean is_visible = true;
          double distance = p.distance(neighbor_point);
          ArrayList<Point> in_between_cells = Util.getCellsBetween(p, neighbor_point);
          for (Point p_in_between : in_between_cells) {
            if (p_in_between.equals(neighbor_point))
              continue;
            Cell cell_in_between = getCell(p_in_between);
            if (cell_in_between != null && cell_in_between.terrain == 2) {
              is_visible = false;
              break;
            }
          }
          if (is_visible)
            neighbors.add(neighbor_point);
        }
      }
    }
    return neighbors;
  }

  // Measure the openness of a cell.
  public double getCellOpenness(Point p, double range) {
    Cell c = getCell(p);
    if (c != null && c.terrain != 0)
      return 0;

    if (c != null && (c.claimed_distance_other == 0 ||
                      c.claimed_distance == 0))
      return 0;

    double openness = 0;
    ArrayList<Point> neighbors = getNeighbors(p, range);
    for (Point neighbor_point : neighbors) {
      c = getCell(neighbor_point);
      if (c != null) {
        double all_claimed_distance =
            Math.min(c.claimed_distance, c.claimed_distance_other);
        if (c.terrain == 0 || c.terrain == 2)
          openness += Math.max(0,
                      all_claimed_distance - p.distance(neighbor_point));
        else {  // water
          openness += Math.max(0,
                      (all_claimed_distance - p.distance(neighbor_point)) *
                      getWaterWeight());
        }
      } else {
        openness += (range - p.distance(neighbor_point)) * 2;
      }
    }
    return openness;
  }

  protected double getWaterWeight() {
    // A naive weighting function based on time.
    if (time < ((double) num_rounds) * 0.333) {
      return 30;
    }
    return 13 * ((double) num_rounds) / time - 9;
  }

  public Point getNeighborPoint(Point p, int direction) {
    return new Point(
      p.x + GameConstants._dx[direction],
      p.y + GameConstants._dy[direction]);
  }

  public static int getDirection(Point a, Point b) {
    if (b.x == a.x && b.y <  a.y)
      return 1;
    else if (b.x >  a.x && b.y <  a.y)
      return 2;
    else if (b.x >  a.x && b.y == a.y)
      return 3;
    else if (b.x >  a.x && b.y >  a.y)
      return 4;
    else if (b.x == a.x && b.y >  a.y)
      return 5;
    else if (b.x <  a.x && b.y >  a.y)
      return 6;
    else if (b.x <  a.x && b.y == a.y)
      return 7;
    else if (b.x <  a.x && b.y <  a.y)
      return 8;
    return 0;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public void setNumRound(int num_rounds) {
    this.num_rounds = num_rounds;
  }
}
