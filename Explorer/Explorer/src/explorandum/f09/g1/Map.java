package explorandum.f09.g1;

import java.util.*;
import java.awt.Point;
import java.util.HashMap;

public class Map {

	private HashMap <Point, Cell> mapExplored;
	// constructor to assign memory
	
	public void Map(){
	mapExplored = new HashMap <Point, Cell>();	
	}
	
	// add to the current map
	
	public void setMapExplored(int explorerId, Point currentLocation, Point[] offsets, Boolean[] hasExplorer,
            Integer[][] otherExplorers, Integer[] terrain, int time, int range) {
		
		//Create current cell and input values
		Cell temp = new Cell();
		
		//temp.setCell(currentLocation.x, currentLocation.y, )
		
		
		//this.mapExplored.put(currentLocation, );
		
	}

	public HashMap <Point, Cell> getMapExplored() {
		return mapExplored;
	}
	
	
}

