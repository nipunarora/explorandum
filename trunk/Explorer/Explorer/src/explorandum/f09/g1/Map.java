package explorandum.f09.g1;

import java.util.*;
import java.awt.Point;
import java.util.HashMap;

public class Map {

	private HashMap <Point, Cell> mapExplored;
	// constructor to assign memory
	
	public Map(){
	mapExplored = new HashMap <Point, Cell>();	
	}
	
	// add to the current map
	
	public void setMapExplored( Point currentLocation, Point[] offsets, Boolean[] hasExplorer,
            Integer[][] otherExplorers, Integer[] terrain, int time, Boolean stepStatus) {
		
		//***Initially adding the current Cell- Naive Approch
		int currentTerrain = 0;
		int currentStepStatus =0;
		//Create current cell and input values
		for (int i=0; i<offsets.length; i++)
		{
			//random checking
			if(this.mapExplored.containsKey(currentLocation))
				continue;
			
			if(offsets[i].x==0&&offsets[i].y==0)
			{
				if(stepStatus=true)
					currentStepStatus = 2; 
				
				Cell currentCell = new Cell();
				currentCell.setCell(currentLocation.x, currentLocation.y, terrain[i], currentStepStatus,0);
				
				this.mapExplored.put(currentLocation, currentCell);
			}
			else
			{
				Point location=new Point();
				//new location of the offset point relative to the starting position, since currentLocation is relative anyways
				location.x=currentLocation.x+offsets[i].x;
				location.y=currentLocation.y+offsets[i].y;
				
				if(this.mapExplored.containsKey(location))
					continue;
				Cell offsetCell= new Cell();
				int distance=0;
				distance = (int)offsets[i].distance(new Point(0,0)); //Should this be lower bound?
				offsetCell.setCell(location.x, location.y, terrain[i], 3,distance);
				
				this.mapExplored.put(location, offsetCell);
			}
		}
		
	}
	
	public boolean hasExplored(Point p){
		
		if(this.mapExplored.containsKey(p))
			return true;
		else
			return false;
	}
	
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

