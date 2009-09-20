package explorandum.g4;

import explorandum.GameConstants;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MapInHash extends Map {
  private HashMap<Point, Cell> map;
  private int min_x = 0, min_y = 0, max_x = 0, max_y = 0;
  
  public MapInHash() {
    map = new HashMap<Point, Cell>();
  }

  // Add what the player sees to the map.
  public void add(int explorerId, Point currentLocation, Point[] offsets, Boolean[] hasExplorer,
                  Integer[][] otherExplorers, Integer[] terrain, int time, int range) {
    // Add mountains first.
    for (int i = 0; i < offsets.length; i++) {
      Point location = offsets[i];
      if (terrain[i] == 2 && !map.containsKey(location)) {
        Cell c = new Cell();
        c.location = new Point(location);
        c.terrain = terrain[i];
        c.claimed_distance = Double.MAX_VALUE;
        c.claimed_distance_other = Double.MAX_VALUE;
        c.claimed_time = time;
        map.put(location, c);
        buildNeighborLinks(c);
      }
    }

    for (int i = 0; i < offsets.length; i++) {
      Point location = offsets[i];
      if (!map.containsKey(location)) {
        Cell c = new Cell();
        c.location = new Point(location);
        c.terrain = terrain[i];
        int dx = location.x - currentLocation.x;
        int dy = location.y - currentLocation.y;
        boolean visible = true;
        if (terrain[i] == 0 && Math.abs(dx) == 1 && Math.abs(dy) == 1) {
          Cell cell_1 = getCell(currentLocation.x + dx, currentLocation.y);
          Cell cell_2 = getCell(currentLocation.x, currentLocation.y + dy);
          if ((cell_1 != null && cell_1.terrain == 2) ||
              (cell_2 != null && cell_2.terrain == 2)) {
            visible = false;
          }
        }
        if (visible)
          c.claimed_distance = currentLocation.distance(location);
        else
          c.claimed_distance = range + 1;
        c.claimed_time = time;
        c.claimed_distance_other = Double.MAX_VALUE; //Initialize the others claimed distance to infinity

        map.put(location, c);
        buildNeighborLinks(c);
       
        //To add other players data...
        if(hasExplorer[i]){
            seeOtherPlayers(explorerId, c, otherExplorers[i], range);
        }
      } else {
        Cell c = getCell(location);
        double distance = currentLocation.distance(location);
        if (distance < c.claimed_distance) {
          c.claimed_distance = distance;
          c.claimed_time = time;
        }
       
        //To add other players data...
        if(hasExplorer[i]){
            seeOtherPlayers(explorerId, c, otherExplorers[i], range);
        }
      }
      updateBound(location);
    }
  }
  
  public Point getmin()
  {
	  return new Point(min_x,min_y);
  }

  public Point getmax()
  {
	  return new Point(max_x,max_y);
  }


  //To add other players data...
  private void markNeighbors(Point p, int range){
	  ArrayList<Point> neighbors = getNeighbors(p, range);
	  for(Point point : neighbors){
		  if(point ==null)
			  continue;
		  
		  Cell cell = map.get(point);
		  if(cell == null)
			  continue;
		  
		  double distance = point.distance(p);
		  if(distance < cell.claimed_distance_other){
			  cell.claimed_distance_other = distance;
		  }
	  }
  }
  
  //To add other players data...
  private void seeOtherPlayers(int myId, Cell c, Integer[] otherExplorers, int range){
	  for(int i=0; i<otherExplorers.length; i++){
		  if(otherExplorers[i] != null){
			  int val = otherExplorers[i];
			  if(val != myId){
				  c.claimed_distance_other = 0;
				  c.other_players.add(val);
				  markNeighbors(c.location, range); //Change 3 to the actual range
			  }
		  }
	  }
  }
  
  
  private void buildNeighborLinks(Cell c) {
    for (int d = 1; d <= 8; d++) {
      Cell neighbor = getCell(
          c.location.x + GameConstants._dx[d],
          c.location.y + GameConstants._dy[d]);
      if (neighbor != null && c.terrain == 0 && neighbor.terrain == 0) {
        // Build a link from c to neighbor
        Link link = new Link();
        link.destination = neighbor;
        link.cost = (d % 2 == 1) ? 2 : 3;
        link.direction = d;
        c.neighbors.add(link);

        // Build a link from neighbor to c
        Link r_link = new Link();
        r_link.destination = c;
        r_link.cost = (d % 2 == 1) ? 2 : 3;
        r_link.direction = (d - 1 + 4) % 8 + 1;  // reverse direction
        neighbor.neighbors.add(r_link);
      }
      if (neighbor != null)
        neighbor.unexplored_directions.remove((d - 1 + 4) % 8 + 1);
      else
        c.unexplored_directions.add(d);
    }
  }

  private void updateBound(Point p) {
    if (p.x < min_x)
      min_x = p.x;
    if (p.x > max_x)
      max_x = p.x;
    if (p.y < min_y)
      min_y = p.y;
    if (p.y > max_y)
      max_y = p.y;
  }

  // Get the current bound of the map.
  public Rectangle getBound() {
    return new Rectangle(min_x, min_y, max_x - min_x, max_y - min_y);
  }

  // Get the cell at (x, y). The coordinates are relative to the start point.
  public Cell getCell(int x, int y) {
    return getCell(new Point(x, y));
  }

  // Get the cell at p. The coordinates are relative to the start point.
  public Cell getCell(Point p) {
    return map.get(p);
  }

  // Print debug message to stdout
  public void printDebugMsg() {
    //System.out.println("=== Map ===");
    //for (int y = max_y; y >= min_y; y--) {
    //  for (int x = max_x; x >= min_x; x--) {
    for (int y = min_y; y <= max_y; y++) {
      for (int x = min_x; x <= max_x; x++) {
        Cell c = getCell(x, y);
        if (c != null) {
          //if (c.terrain == 0)
            //System.out.print("_ ");
          //else if (c.terrain == 1)
            //System.out.print("~ ");
          //else
            //System.out.print("^ ");
        } else {
          //System.out.print("? ");
        }
      }
      //System.out.println();
    }
  }

  // Get the coordinates of all discovered cells
  public Set<Point> getAllDiscoveredCoordinates() {
    return map.keySet();
  }
}
