package explorandum.g3;

import java.awt.Point;
import java.util.*;

import explorandum.Cell;
import explorandum.Explorer;
import explorandum.GameConstants;

public class Map {

	static int ID;
	static int [][]map;
	static int mapSize = 100;    //set a initial maximum size for the map
	static int mapSizeX = mapSize, mapSizeY = mapSize;    //set a maximum size for the map
	//the largest length of the map
	static int mapXMax = mapSizeX/2-1, mapYMin = -mapSizeY/2;
	static int mapXMin = -mapSizeX/2, mapYMax = mapSizeY/2-1;  //+ - - +
	//the largest size of knonwn map
	static int mapXeast = 0, mapYnorth = 0, mapXwest = 0, mapYsouth = 0;  //+ - - +
	//the coordinate of the initial location
	static int basicX = mapSizeX/2, basicY = mapSizeY/2;

	public static final int LAND = GameConstants.LAND;
	public static final int WATER = GameConstants.WATER;
	public static final int MOUNTAIN = GameConstants.MOUNTAIN;

	public static final int OUR_LAND = 3;
	public static final int THEIR_LAND = 4;
	public static final int MUTUAL_LAND = 5;
	public static final int UNKNOWN = 6;

	public static final int UNCLAIM_LAND = 7;
	public static final int UNCLAIM_WATER = 8;
	public static final int UNCLAIM_MOUNTAIN = 9;
	public static final int UNCLAIMABLE_LAND = 10;

	public static final String[] TERRAIN_NAMES2 = {"LAND", "WATER", 
		"MOUNTAIN", "OUR_LAND", "THEIR_LAND", "MUTUAL_LAND", "UNKNOWN",
		"UNCLAIM_LAND", "UNCLAIM_WATER", "UNCLAIM_MOUNTAIN"};
	//TODO: add new unclaimed terrain, need to process

	public Map(int id) {
		ID = id;
		map = new int[mapSizeX][mapSizeY];
		for(int i=0; i<mapSizeX; i++){
			for(int j=0; j<mapSizeY; j++){
				map[i][j] = UNKNOWN;
			}
		}
	}

	// return value of cell p (in game-coord)
	public int getCell(Point p) {
		return getCell(p.x,p.y);
	}
	public int getCell(int x, int y) {
		
//		if(x+basicX<mapXMin || x+basicX>mapXMax || y+basicY<mapYMin || y+basicY>mapYMax) {
		if(x<mapXMin || x>mapXMax || y<mapYMin || y>mapYMax) {
			return UNKNOWN;
		}
		
		return map[x+basicX][y+basicY];
	}

	public boolean adjacentFeature(Point loc, int feature) {
		// location in game coordinate system (ie relative to our starting point on board)
		// returns true if loc is adjacent to a cell with feature type terrain
		int x = loc.x+basicX;
		int y = loc.y+basicY;
		for (int i=0; i<9; i++){
			if(map[x+GameConstants._dx[i]][y+GameConstants._dy[i]]  == feature )
				return true;
		}
		return false;
	}

	static final int SIZE_LARGER = 50;
	private void resizeMap(int type, int size) {
		int [][]mapTemp = new int[mapSizeX][mapSizeY];
		int oldSizeX = mapSizeX, oldSizeY = mapSizeY;
		for(int i=0; i<oldSizeX; i++){
			for(int j=0; j<oldSizeY; j++){
				mapTemp[i][j] = map[i][j];
			}
		}

		if(type == 1){  //x larger
			mapSizeX += SIZE_LARGER;
			mapXMax += SIZE_LARGER;

			map = new int[mapSizeX][mapSizeY];
			for(int i=0; i<oldSizeX; i++){
				for(int j=0; j<mapSizeY; j++){
					map[i][j] = mapTemp[i][j];
				}
			}
			for(int i=oldSizeX; i<mapSizeX; i++){
				for(int j=0; j<mapSizeY; j++){
					map[i][j] = UNKNOWN;
				}
			}
		}
		else if(type == 2){ //x smaller
			mapSizeX += SIZE_LARGER;
			mapXMin -= SIZE_LARGER;
			basicX += SIZE_LARGER;

			map = new int[mapSizeX][mapSizeY];
			for(int i=0; i<SIZE_LARGER; i++){
				for(int j=0; j<mapSizeY; j++){
					map[i][j] = UNKNOWN;
				}
			}
			for(int i=SIZE_LARGER; i<mapSizeX; i++){
				for(int j=0; j<mapSizeY; j++){
					map[i][j] = mapTemp[i-SIZE_LARGER][j];
				}
			}
		}
		else if(type == 3){ //y larger
			mapSizeY += SIZE_LARGER;
			mapYMax += SIZE_LARGER;

			map = new int[mapSizeX][mapSizeY];
			for(int i=0; i<mapSizeX; i++){
				for(int j=0; j<oldSizeY; j++){
					map[i][j] = mapTemp[i][j];
				}
			}
			for(int i=0; i<mapSizeX; i++){
				for(int j=oldSizeY; j<mapSizeY; j++){
					map[i][j] = UNKNOWN;
				}
			}
		}
		else if(type == 4){ //y smaller
			mapSizeY += SIZE_LARGER;
			mapYMin -= SIZE_LARGER;
			basicY += SIZE_LARGER;

			map = new int[mapSizeX][mapSizeY];
			for(int i=0; i<mapSizeX; i++){
				for(int j=0; j<SIZE_LARGER; j++){
					map[i][j] = UNKNOWN;
				}
			}
			for(int i=0; i<mapSizeX; i++){
				for(int j=SIZE_LARGER; j<mapSizeY; j++){
					map[i][j] = mapTemp[i][j-SIZE_LARGER];
				}
			}
		}
		else{
			return;
		}
	}

	public void updateMap(Point cur,Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain) {
		//Build map: given the x/y of our player, and terrain of offsets
		int currentX = basicX + cur.x;
		int currentY = basicY + cur.y;

		//Handled in otherExplorers[][], not needed here
		if( map[currentX][currentY] == UNKNOWN || map[currentX][currentY] == LAND || map[currentX][currentY] == OUR_LAND){
			map[currentX][currentY] = OUR_LAND;
		}
		else if( map[currentX][currentY] == THEIR_LAND || map[currentX][currentY] == MUTUAL_LAND){
			map[currentX][currentY] = MUTUAL_LAND;
		}
		else if( map[currentX][currentY] == WATER || map[currentX][currentY] == MOUNTAIN ){
			
		}

		for (int j = 0; j < offsets.length; j++) {
//			System.out.println(offsets[j].x +"," + offsets[j].y +":" + terrain[j]);

			//If out of map, resize
			if(offsets[j].x > mapXMax) {
				//out of the bound of map, resize map, copy old data
				resizeMap(1, offsets[j].x);
			}
			else if(offsets[j].x < mapXMin) {
				//out of the bound of map, resize map, copy old data
				resizeMap(2, offsets[j].x);
			}
			if(offsets[j].y > mapYMax) {
				//out of the bound of map, resize map, copy old data
				resizeMap(3, offsets[j].y);
			}
			else if(offsets[j].y < mapYMin ){
				//out of the bound of map, resize map, copy old data
				resizeMap(4, offsets[j].y);
			}

			//update the known area size
			if(offsets[j].x > mapXeast){
				mapXeast = offsets[j].x;
			}
			else if(offsets[j].x < mapXwest){
				mapXwest = offsets[j].x;
			}
			if(offsets[j].y > mapYsouth){
				mapYsouth = offsets[j].y;
			}
			else if(offsets[j].y < mapYnorth){
				mapYnorth = offsets[j].y;
			}

			//update the known terrain type
			int x = basicX + offsets[j].x;
			int y = basicY + offsets[j].y;
			if (terrain[j] == LAND){
				if(map[x][y] == WATER && map[x][y] == MOUNTAIN){
					
				}
				else{ //hasExplorer contains ourselves
					if(hasExplorer[j] == true){ //have explorer
						int maxNum = otherExplorers[j].length;
						for(int i=0; i<maxNum; i++){
							if(otherExplorers[j][i] != null){
								if (otherExplorers[j][i] == ID){ //our player
									if(map[x][y] == OUR_LAND || map[x][y] == LAND || map[x][y] == UNKNOWN)
										map[x][y] = OUR_LAND;
									else     //LAND or THEIR_LAND
										map[x][y] = MUTUAL_LAND;
								}
								else{  //other player
									if(map[x][y] == OUR_LAND || map[x][y] == MUTUAL_LAND)
										map[x][y] = MUTUAL_LAND;
									else     //LAND or THEIR_LAND
										map[x][y] = THEIR_LAND;
								}

								if(map[x][y] == MUTUAL_LAND)
									break;
							}
							else
								break;  //no other explorer
						}
					}
					else{
						if(map[x][y] == UNKNOWN)
							map[x][y] = LAND;		
						//else use original terrain: land, our/their/mutual_land
					}

				}
			}
			else if (terrain[j] == WATER){
				if(map[x][y] != WATER && map[x][y] != UNKNOWN){
					//System.out.println("Terrain error: WANTER and other terrain");
				}
				else
					map[x][y] = WATER;
			}
			else if (terrain[j] == MOUNTAIN){
				if(map[x][y] != MOUNTAIN && map[x][y] != UNKNOWN){
					//System.out.println("Terrain error: MOUNTAIN and other terrain");
				}
				else
					map[x][y] = MOUNTAIN;				
			}
			else{
				//System.out.println("Terrain error: unknown terrain");
			}
		}
	}

	final static int MORE_SCORE = 10;
	final static int INIT_DIST = 500;
	private class Area {
		ArrayList<Point> edges;
		ArrayList<Point> unknowns;
		int score;  //area of this Area
		int distance;

		Area () {
			edges = new ArrayList<Point> ();
			unknowns = new ArrayList<Point> ();
			score = 0;
			distance = INIT_DIST;
		}
	}
	ArrayList<Area> unknownAreas = new ArrayList<Area>();

	/**
	 * Given an unknown cell, build this unknown area
	 * @param area
	 * @param cell
	 */
	private void buildAreas(Area area, Point cell) {
		//already add the cell
		if (area.unknowns.contains(cell)) 
			return;
		//out of known bounds, it may contain a large area, so give a high score
		if(cell.x > mapXMax+basicX || cell.x<mapXMin+basicX || cell.y>mapYMax+basicY || cell.y<mapYMin+basicY) {
			area.unknowns.add(cell);
			area.score += MORE_SCORE;	
			return;
		}
		else if(map[cell.x][cell.y] == UNKNOWN ) {
			area.unknowns.add(cell);
			area.score += 1;
			for(int m=1; m<GameConstants._dx.length; m++) {
				int x = GameConstants._dx[m] + cell.x;
				int y = GameConstants._dy[m] + cell.y;
				if(map[x][y]==LAND || map[x][y]==OUR_LAND || map[x][y]==THEIR_LAND || map[x][y]==MUTUAL_LAND) {
					area.edges.add(new Point(x,y));
				}
				else if(map[x][y]==UNKNOWN)
					buildAreas(area, new Point(x,y));
			}
		}
		else  //known terrain
			return;
	}

	private void checkNewAreas(Point cell) {
		//given a cell, check whether we have already record it in an area
		for (int i=0; i<unknownAreas.size(); i++){
			if (unknownAreas.get(i).unknowns.contains(cell)) {
				return;
			}
		}

		//build a new Area
		Area newArea = new Area();
		unknownAreas.add(newArea);
		buildAreas(newArea, cell);
		return;
		}

	/**
	 * evaluate map, which unknown area is more attractive
	 * @param cur
	 */
	private void findUnknownAreas() {
		unknownAreas = new ArrayList<Area>();
		for(int i=mapXMin+basicX; i<=mapXMax+basicX; i++) {
			for(int j=mapYMin+basicY; j<=mapYMax+basicX; j++) {
				//check all land
				if(map[i][j] == UNKNOWN) {
					//check all neighbours
					checkNewAreas(new Point(i,j));
				}
			}
		}
	}


	private final static int MINI_SCORE = 10; 
	private final static int NO_INDEX = 100000000; 
	/**
	 * evaluate map, which unknown area is more attractive
	 * @param cur
	 */
	private ArrayList<Point> evaluateUnknown(Point cur) {
//		unknownAreas = new ArrayList<Area>();
		//It will new unknownAreas every time
		findUnknownAreas();

		int smallDist = 10000;
		int idx = NO_INDEX;
		boolean haveSmallArea = false;
		ArrayList<Point> finalEdges = new ArrayList<Point> ();

		for (int i=0; i<unknownAreas.size(); i++){
			//if very few unknown cells, consider the next one
			if(unknownAreas.get(i).score < MINI_SCORE) {
				haveSmallArea = true;
				continue;
			}

			//TODO: since findRoute takes long time, we can figure out another way
			//to find the route to all the areas
			//we can tag all edges, and find the nearest one, and check whether it's large area
			//if no large area, use this, otherwise, go one check length
			ArrayList<Point> edges = findRoute(cur, unknownAreas.get(i).edges);
			unknownAreas.get(i).distance = edges.size();

			if (smallDist > edges.size()) {
				finalEdges = edges;
				idx = i;
				smallDist = edges.size();
			}
		}

		//if no large area, but have small area, check again
		if (idx == NO_INDEX && haveSmallArea == true) {
			for (int i=0; i<unknownAreas.size(); i++){
				ArrayList<Point> edges = findRoute(cur, unknownAreas.get(i).edges);
				unknownAreas.get(i).distance = edges.size();

				if (smallDist > edges.size()) {
					finalEdges = edges;
					idx = i;
					smallDist = edges.size();
				}
			}
		}
		if (idx == NO_INDEX) {
			return null;
		}
		return finalEdges;
	}

	public ArrayList<Point> findEdges() {
		ArrayList<Point> edges = new ArrayList<Point>();

		//System.out.println("xmin="+mapXMin+" ymin="+mapYMin+" basicX="+basicX+" basicY="+basicY);
		for(int i=mapXMin+1; i<mapXMax; i++) {
			for(int j=mapYMin+1; j<mapYMax; j++) {
				//check all land
				int cell = getCell(i,j);
				//int xx = i+basicX;
				//int yy = j+basicY;
				//if(map[xx][yy] == LAND || map[xx][yy] == OUR_LAND 
				//		|| map[xx][yy] == THEIR_LAND || map[xx][yy] == MUTUAL_LAND) {
				if (cell == LAND || cell == OUR_LAND || cell == THEIR_LAND 
						|| cell == MUTUAL_LAND) {
					//check all neighbours
					for(int m=1; m<GameConstants._dx.length; m++) {
						int x = GameConstants._dx[m] + i;
						int y = GameConstants._dy[m] + j;
						
						if (x > mapXMax || x < mapXMin || y>mapYMax || y<mapYMin) {
							edges.add(new Point(i,j));
							break;
						} else if (getCell(x,y) == UNKNOWN) {
							edges.add(new Point(i,j));
							break;
						}
					}
				}
			}
		}
		return edges;
	}

	int routeLength = 0;
	Point bestNode = new Point();
	ArrayList<Point> nodes = new ArrayList<Point>();
	ArrayList<Point> lastNode = new ArrayList<Point>();
	ArrayList<Integer> lengths = new ArrayList<Integer>();
	private void tagRouteNode(Point start, ArrayList<Point> targets, int len) {

		int newLen = 0;
		for(int m=1; m<GameConstants._dx.length; m++) {
			int x = GameConstants._dx[m] + start.x;
			int y = GameConstants._dy[m] + start.y;

			int cell = getCell(x,y);
			if(cell == MOUNTAIN || cell == WATER || cell == UNKNOWN) {
				continue;
			}
			if(m%2 == 1) { //1 3 5 7, 
				newLen = len + 2;
			}
			else { //diago
				newLen = len + 3;
			}
			if(routeLength>0 && routeLength<=newLen) {
				continue;
//				return;
			}
			
			//TODO:
			Point neighbour = new Point(x,y);
			boolean flag = false; 
			if(targets.contains(neighbour)) {
				routeLength = newLen;
//				bestNode = start;
//				return;
				bestNode = neighbour;
				flag =  true;
			}
			
			if(nodes.contains(neighbour)) {
				int idx = nodes.indexOf(neighbour);
				int lenNow = lengths.get(idx);
				if(lenNow > newLen) {
					lengths.set(idx, newLen);
					lastNode.set(idx, start);
					if(flag == true) {
						continue;
//						return;
					}
						
					tagRouteNode (neighbour, targets, newLen);					
				}
				else {
					continue;
//					return;
				}
			}
			else if(! neighbour.equals(start)){
				nodes.add(neighbour);
				lengths.add(newLen);
				lastNode.add(start);
				if(flag == true) {
					continue;
//					return;
				}
				tagRouteNode (neighbour, targets, newLen);
//				return;
			}			
		}
	}

	public ArrayList<Point> findRoute(Point cur, ArrayList<Point> targets) {
		
		ArrayList<Point> route = new ArrayList<Point>();
		ArrayList<Point> route2 = new ArrayList<Point>();

		routeLength = 0;
		bestNode = new Point();
		nodes = new ArrayList<Point>();
		lastNode = new ArrayList<Point>();
		lengths = new ArrayList<Integer>();

		tagRouteNode (cur, targets, 0);

		Point nodeNow = bestNode;
		
		while(! nodeNow.equals(cur)) {
			int idx;
			route.add(nodeNow);
			idx = nodes.indexOf(nodeNow);
			nodeNow = lastNode.get(idx);
			}

		//the last node is current node
		for (int i=route.size()-1; i>=0; i--) {
			route2.add(route.get(i));
		}
		return route2;
	}


	public ArrayList<Point> findNearestArea(Point cur) {

		ArrayList<Point> route = new ArrayList<Point>();
		
		ArrayList<Point> edges = findEdges();
		
		if (edges.size() == 0) {
			return route;
		}
		
		route = findRoute(cur, edges);

		return route;
	}

	/**
	 * find the route to the nearest large unknown area
	 * @param cur
	 * @return
	 */
	public ArrayList<Point> findBestArea(Point cur) {		
//		Area[][] test = new Area[10][20];
		ArrayList<Point> cells = evaluateUnknown(cur);
//		for (int i=0; i<cells.size(); i++) {
//			cells.get(i).x -= basicX;
//			cells.get(i).y -= basicY;
//		}
		return cells;		
	}

}