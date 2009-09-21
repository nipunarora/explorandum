package explorandum.f09.g1;

import java.util.*;
import java.awt.Point;
import java.util.HashMap;

public class Map {

	private HashMap <Point, Cell> mapExplored;
	// constructor to assign memory
	
	public Map(){
		
		//System.out.println("Created Map");
	mapExplored = new HashMap <Point, Cell>();	
	}
	
	// add to the current map
	
	public void setMapExplored( Point currentLocation, Point[] offsets, Boolean[] hasExplorer,
            Integer[][] otherExplorers, Integer[] terrain, int time, Boolean stepStatus) {
		
		System.out.println("Entered setMapExplored");
		
		//***Initially adding the current Cell- Naive Approch
		int currentTerrain = 0;
		int currentStepStatus =0;
		//Create current cell and input values
		for (int i=0; i<offsets.length; i++)
		{
			//random checking
			if(this.mapExplored.containsKey(offsets[i]))
				continue;
			
			if(offsets[i].x==0&&offsets[i].y==0)
			{
				if(stepStatus=true)
					currentStepStatus = 2; 
				else
					currentStepStatus = 1;
				
				Cell currentCell = new Cell();
				currentCell.setCell(currentLocation.x, currentLocation.y, terrain[i], currentStepStatus,0);
				
				this.mapExplored.put(currentLocation, currentCell);
				continue;
			}
			
				Cell offsetCell= new Cell();
				Point location= new Point();
				location.x= offsets[i].x - currentLocation.x;
				location.y= offsets[i].y - currentLocation.y;
				
				int distance= offsetdist(location);
				
				offsetCell.setCell(offsets[i].x, offsets[i].y, terrain[i], 3,distance);
				this.mapExplored.put(offsets[i], offsetCell);
			
		}
		
	}
	
	public int offsetdist(Point p)
	{		
		double tempval= (p.x)^2 + (p.y)^2;
		tempval= Math.sqrt(tempval);
		return (int)Math.floor(tempval);
	}
	
	//checks if the point has been explored
	public boolean hasExplored(Point p){
		
		if(this.mapExplored.containsKey(p))
			return true;
		else
			return false;
	}
	// checks if the point has been visited
	public boolean hasVisited(Point p){
		if(this.mapExplored.containsKey(p) && this.mapExplored.get(p).checkVisitedCell())
			return true;
		else
			return false;
	}

	public HashMap <Point, Cell> getMapExplored() {
		return mapExplored;
	}
	
	
}

