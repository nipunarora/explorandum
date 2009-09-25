package explorandum.f09.g1;

import java.util.*;
import java.awt.Point;
import java.util.HashMap;

import explorandum.GameConstants;
import explorandum.f09.g1.Cell;
import explorandum.f09.g1.Utilities;

public class Map {

	private HashMap<Point, Cell> mapExplored;
	// constructor to assign memory

	public Map() {
		mapExplored = new HashMap<Point, Cell>();	
	}

	// add to the current map

	public void setMapExplored( Point currentLocation, Point[] offsets, Boolean[] hasExplorer,
			Integer[][] otherExplorers, Integer[] terrain, int time, Boolean stepStatus) {

		//Initially adding the current Cell- Naive Approach
		int currentTerrain = 0;
		int currentStepStatus = 0;
		Point currPoint;

		//Create current cell and input values
		for (int i=0; i<offsets.length; i++) {			
			currPoint = offsets[i];
			int currDistance = Utilities.euclidDistance(currentLocation, currPoint);
			
			if(this.hasExplored(currPoint)){
				if(this.mapExplored.get(currPoint).getDistance() < currDistance) {
					continue;
				}
			}
			
			if((currPoint.x == currentLocation.x) && (currPoint.y == currentLocation.y)) {
				if(stepStatus)
					currentStepStatus = 2; 
				else
					currentStepStatus = 1;

				Cell currentCell = new Cell(currentLocation.x, currentLocation.y, terrain[i], currentStepStatus,0);

				this.mapExplored.put(currentLocation, currentCell);
				
				continue;
			}
			
			int distance = euclidDistance(currentLocation, currPoint);
			
			Cell offsetCell = new Cell(offsets[i].x, offsets[i].y, terrain[i], 3, distance);
			this.mapExplored.put(offsets[i], offsetCell);
		}
	}

	public int euclidDistance(Point p1, Point p2) {		
		double tempval= Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2);
		return (int)Math.floor(Math.sqrt(tempval));
	}

	//checks if the point has been explored
	public boolean hasExplored(Point p) {
		if(this.mapExplored.containsKey(p))
			return true;
		else
			return false;
	}
	// checks if the point has been visited
	public boolean hasVisited(Point p) {
		if(this.mapExplored.containsKey(p) && this.mapExplored.get(p).checkVisitedCell())
			return true;
		else
			return false;
	}

	public HashMap <Point, Cell> getMapExplored() {
		return mapExplored;
	}
	
	 public Point getNeighborPoint(Point p, int direction) {
		    return new Point(
		      p.x + GameConstants._dx[direction],
		      p.y + GameConstants._dy[direction]);
		  }
	 
	 public double getCellOpenness(Point p, double range) {
		    Cell c = getCell(p);
		    
		    if (c != null && c.getTerrain() != 0)
		      return 0;

		    if (c != null && (c.getDistance() == 0))
		      return 0;

		    double openness = 0;
		    ArrayList<Point> neighbors = getNeighbors(p, range);
		    for (Point neighbor_point : neighbors) {
		      c = getCell(neighbor_point);
		     //has already been viewed
		      if (c != null) {
		    	  //minimum distance that the cell is at
		        double all_claimed_distance =c.getDistance();
		        // 
		        if (c.getTerrain() == 0 || c.getTerrain() == 2)
		          openness += Math.max(0, all_claimed_distance - p.distance(neighbor_point));
		        else {  // water
		          openness += Math.max(0,
		                      (all_claimed_distance - p.distance(neighbor_point)) *
		                      30);
		        }
		      } else {
		        openness += (range - p.distance(neighbor_point)) * 2;
		      }
		    }
		    return openness;
		  }
	 
	// Get the cell at p. The coordinates are relative to the start point.
	  public Cell getCell(Point p) {
		  if(mapExplored.containsKey(p))
	    return mapExplored.get(p);
		  else
			  return null;
	  }
	  
	  public ArrayList<Point> getNeighbors(Point p, double range) {
		    ArrayList<Point> neighbors = new ArrayList<Point>();
		    for (int x = (int) (p.x - range); x <= p.x + range; x++) {
		      for (int y = (int) (p.y - range); y <= p.y + range; y++) {
		        Point neighbor_point = new Point(x, y);
		            neighbors.add(neighbor_point);
		        }
		      }
		    
		    return neighbors;
		  }
}

