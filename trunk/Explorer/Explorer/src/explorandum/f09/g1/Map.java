package explorandum.f09.g1;

import java.util.*;
import java.awt.Point;
import java.util.HashMap;

public class Map {

	private HashMap<Point, Cell> mapExplored;
	// constructor to assign memory

	public Map() {
		mapExplored = new HashMap<Point, Cell>();	
	}

	// add to the current map

	public void setMapExplored( Point currentLocation, Point[] offsets, Boolean[] hasExplorer,
			Integer[][] otherExplorers, Integer[] terrain, int time, Boolean stepStatus) {

		System.out.println("Entered setMapExplored");

		//Initially adding the current Cell- Naive Approch
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
				//currentCell.printCell();
				
				continue;
			}
			
			int distance = euclidDistance(currentLocation, currPoint);
			
			Cell offsetCell = new Cell(offsets[i].x, offsets[i].y, terrain[i], 3, distance);
			this.mapExplored.put(offsets[i], offsetCell);
			//offsetCell.printCell();
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
}

