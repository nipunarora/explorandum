package explorandum.g6;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import explorandum.GameConstants;
import explorandum.Move;
import explorandum.g6.pathfinding.AStarPathFinder;
import explorandum.g6.pathfinding.Mover;
import explorandum.g6.pathfinding.Path;
import explorandum.g6.pathfinding.PathFinder;
import explorandum.g6.pathfinding.TileBasedMap;
import explorandum.g6.pathfinding.Path.Step;

public class Util implements GameConstants {
	
	protected static boolean dontGoOnBridges = true;

	public static Random rand;
	
	final static int AStarLookUpDepth = 1000;
	/**
	 * @return Returns the index into offsets and hopefully the other arrays of
	 *         the cell in the given direction.
	 */
	public static int directionToIndex(Point currentLocation, int direction, Point[] offsets) {
//		log.debug(Util.pointToString(offsets));
		
		Point dest = directionToPoint(currentLocation, direction);
		return pointToIndex(dest, offsets);
	}

	public static int pointToIndex(Point point, Point[] offsets) {
		for (int i = 0; i < offsets.length; i++) {
			if (offsets[i].equals(point)) {
				return i;
			}
		}
		assert false : "Could not find index for point " + point;
		return 0;		
	}
	
	
	public static Point directionToPoint(Point currentLocation, int direction) {
		if (direction == NORTH) {
			return new Point(currentLocation.x, currentLocation.y - 1);
		} else if (direction == SOUTH) {
			return new Point(currentLocation.x, currentLocation.y + 1);
		} else if (direction == EAST) {
			return new Point(currentLocation.x + 1, currentLocation.y);
		} else if (direction == WEST) {
			return new Point(currentLocation.x - 1, currentLocation.y);
		} else if (direction == NORTHWEST) {
			return new Point(currentLocation.x - 1, currentLocation.y - 1);
		} else if (direction == NORTHEAST) {
			return new Point(currentLocation.x + 1, currentLocation.y - 1);
		} else if (direction == SOUTHWEST) {
			return new Point(currentLocation.x - 1, currentLocation.y + 1);
		} else if (direction == SOUTHEAST) {
			return new Point(currentLocation.x + 1, currentLocation.y + 1);
//		} else if (direction == STAYPUT) {
//			return currentLocation;
		} else {
			assert false : "Invalid direction";
			return currentLocation;
		}
	}
	
	static class ExplorerMover implements Mover {
	}
	
	static class ExplorerMap implements TileBasedMap {
		private Map map;

		public ExplorerMap(Map map) {
			this.map = map;
		}
		
		public int getWidthInTiles() {
			return map.getMaxX() - map.getMinX() + 1;
		}

		public int getHeightInTiles() {
			return map.getMaxY() - map.getMinY() + 1;
		}

		public boolean blocked(Mover mover, int x, int y) {
			Cell cell = map.get(x + map.getMinX(), y + map.getMinY());
			if (cell == null || cell.getTerrainType() == LAND) {
				return false;
			} else {
				return true;
			}
		}

		public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
			int delta = Math.abs(sx - tx) + Math.abs(sy - ty);
			if (delta == 0) {
				return 0;
			} else if (delta == 1) {
				return 2;
			} else if (delta == 2) {
				return 3;
			} else {
				return 100000;
			}
		}

		public void pathFinderVisited(int x, int y) {
//			System.out.println("path finder visted " + x + ", " + y);
		}
	}
	
	
	public static MovePathLengthPair getMoveTowardsPoint(Point currentLocation, Point dest, Map map)
	{
		Util.ExplorerMap gameMap = new ExplorerMap(map);
		PathFinder pathFinder = new AStarPathFinder(gameMap, AStarLookUpDepth, true);
		Point pathFindingSource = new Point(currentLocation.x - map.getMinX(), currentLocation.y - map.getMinY());
		Point pathFindingDest = new Point(dest.x - map.getMinX(), dest.y - map.getMinY());
		//log.debug("Finding path from " + pathFindingSource + " to " + pathFindingDest);
		Path path = pathFinder.findPath(new ExplorerMover(),
				pathFindingSource.x, pathFindingSource.y, pathFindingDest.x,
				pathFindingDest.y);
		
		if (path == null)// || path.getLength() <= 2) 
		{
//			System.out.println("AStar algorithm could not find a path!");
			return null;
		}
		else {
			//log.debug("Found path: " + pathToString(path));
			
			/*System.out.print("Path: ");
			for(int j=0;j<path.getLength();++j)
			{
				System.out.print(path.getStep(j).getX() + ", " + path.getStep(j).getY() + "; ");
			}
			System.out.println();*/
			Step step = path.getStep(1); // 0th step is current position
			Point point = new Point(map.getMinX() + step.getX(),
					map.getMinY() + step.getY());
			return new MovePathLengthPair(new Move(pointToDirection(currentLocation, point)), getPathTime(path));
		}
		
	}
	
	public static double euclideanDist(Point p1, Point p2)
	{
		return Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
	}
	
	public static double manhattanDist(Point p1, Point p2)
	{
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
	}
	
	
	//uses euclidean distance as distance measure for now
	public static Point getClosestPointInRegion(Point currentLocation, ArrayList<Point> region)
	{
		double minDist = Double.MAX_VALUE;
		Point bestPoint = currentLocation;
		for(Point p:region)
		{
			double dist = manhattanDist(currentLocation, p);
			if(dist < minDist)
			{
				minDist = dist;
				bestPoint = p;
			}
		}
		return bestPoint;
	}

	
	public static int getTimeToDest(Point currentLocation, Point dest, Map map)
	{
		Util.ExplorerMap gameMap = new ExplorerMap(map);
		PathFinder pathFinder = new AStarPathFinder(gameMap, AStarLookUpDepth, true);
		Point pathFindingSource = new Point(currentLocation.x - map.getMinX(), currentLocation.y - map.getMinY());
		Point pathFindingDest = new Point(dest.x - map.getMinX(), dest.y - map.getMinY());
		//log.debug("Finding path from " + pathFindingSource + " to " + pathFindingDest);
		Path path = pathFinder.findPath(new ExplorerMover(),
				pathFindingSource.x, pathFindingSource.y, pathFindingDest.x,
				pathFindingDest.y);
		
		if (path == null)// || path.getLength() <= 2) 
		{
//			System.out.println("AStar algorithm could not find a path!");
			return -1;
		}
		else {
			return getPathTime(path);
		}
		
	}
	
	
	private static int getPathTime(Path path)
	{
		int time = 0;
		for(int i=1;i<path.getLength();++i)
		{
			Step curStep = path.getStep(i-1);
			Step nextStep = path.getStep(i);
			if(areOrthogonal(curStep.getX(), curStep.getY(), nextStep.getX(), nextStep.getY()))
			{
				time += 2;
			}
			else if(areDiagonal(curStep.getX(), curStep.getY(), nextStep.getX(), nextStep.getY()))
			{
				time += 3;
			}
		}
		return time;
	}
	
	/* checks whether two points are orthogonal to each other or not */
	public static boolean areOrthogonal(int x1, int y1, int x2, int y2)
	{
		if(x1 == x2 || y1 == y2)
			return true;
		else
			return false;
	}
	
	/* checks whether two points are diagonal to each other or not */
	public static boolean areDiagonal(int x1, int y1, int x2, int y2)
	{
		if(x1 != x2 && y1 != y2)
			return true;
		else
			return false;
	}
	
	public static int pointToDirection(Point source, Point dest) {
		int dx = dest.x - source.x;
		int dy = dest.y - source.y;
		if (dx < 0 && dy < 0) {
			return NORTHWEST;
		} else if (dx < 0 && dy == 0) {
			return WEST;
		} else if (dx < 0 && dy > 0) {
			return SOUTHWEST;
		} else if (dx == 0 && dy < 0) {
			return NORTH;
		} else if (dx == 0 && dy == 0) {
			return STAYPUT;
		} else if (dx == 0 && dy > 0) {
			return SOUTH;
		} else if (dx > 0 && dy < 0) {
			return NORTHEAST;
		} else if (dx > 0 && dy == 0) {
			return EAST;
		} else if (dx > 0 && dy > 0) {
			return SOUTHEAST;
		} else {
			assert false : "pointToDirection: something impossible happened";
			return STAYPUT;
		}
	}
	
	public static int oppositeDirection(int direction) {
		switch (direction) {
		case NORTH: return SOUTH;
		case SOUTH: return NORTH;
		case EAST: return WEST;
		case WEST: return EAST;
		case NORTHWEST: return SOUTHEAST;
		case NORTHEAST: return SOUTHWEST;
		case SOUTHWEST: return NORTHEAST;
		case SOUTHEAST: return NORTHWEST;
		}
		assert(false); // Should never get here
		return NORTH;
	}

	public static String pointToString(Point[] offsets) {
		String s = new String();
		for (int i = 0; i < offsets.length; i++) {
			s += offsets[i] + ", ";
		}
		if (s.length() > 2) {
			s = s.substring(s.length() - 2);
		}
		return s;
	}
	
	public static boolean isPointOnBridge(Point p, Map map)
	{
		if(dontGoOnBridges == false)
			return false;
		final int LOOKOUT_DISTANCE = 5;
		//check if point is surrounded by water on two sides
		
		ArrayList<Integer> directions = new ArrayList<Integer>();
		directions.add(GameConstants.NORTH);
		directions.add(GameConstants.EAST);
		directions.add(GameConstants.SOUTHEAST);
		directions.add(GameConstants.NORTHEAST);
		
		for(Integer dir:directions)
		{
			ArrayList<Point> oneDir = pointsInDirection(p, map, dir, LOOKOUT_DISTANCE);
			ArrayList<Point> otherDir = pointsInDirection(p, map, oppositeDirection(dir), LOOKOUT_DISTANCE);
	
			if((hasWaterTowardsEnd(oneDir, map) || hasMountainTowardsEnd(oneDir, map))
				&& (hasWaterTowardsEnd(otherDir, map) || hasMountainTowardsEnd(otherDir, map)))
			{
				return true;
			}
		}
		return false;
	}
	
	
	static boolean hasWaterTowardsEnd(ArrayList<Point> points, Map map)
	{
		int counter = 0;
		for(int i = 0; i< points.size(); ++i)
		{
			if(map.get(points.get(i).x, points.get(i).y).getTerrainType() == GameConstants.WATER)
				++counter;
			else
				counter = 0;
		}
		if(counter >= 1)
			return true;
		
		return false;
	}
	
	
	static boolean hasMountainTowardsEnd(ArrayList<Point> points, Map map)
	{
		int counter = 0;
		for(int i = 0; i < points.size(); ++i)
		{
			if(map.get(points.get(i).x, points.get(i).y).getTerrainType() == GameConstants.MOUNTAIN)
				++counter;
			else
				counter = 0;
		}
		if(counter >= 1)
			return true;
		return false;
	}
	
	/**
	 * 
	 * @param currentLocation
	 * @param direction
	 * @param n
	 * @param map
	 * @return list of points (n maximum) that have been explored in the passed direction
	 */
	
	public static ArrayList<Point> pointsInDirection(Point currentLocation,  Map map, int direction, int n)
	{
		ArrayList<Point> points = new ArrayList<Point>();
		int i = 1;
		if(direction == GameConstants.NORTH)
		{
			while(map.get(currentLocation.x, currentLocation.y - i)!= null && i<n)
			{
				points.add(new Point(currentLocation.x, currentLocation.y - i));
				i++;
			}
			return points;
		}
		else if (direction == GameConstants.SOUTH)
		{
			while(map.get(currentLocation.x, currentLocation.y + i)!= null && i<n)
			{
				points.add(new Point(currentLocation.x, currentLocation.y + i));
				i++;
			}
			return points;
		}
		else if (direction == GameConstants.EAST)
		{
			while(map.get(currentLocation.x + i, currentLocation.y)!= null && i<n)
			{
				points.add(new Point(currentLocation.x + i, currentLocation.y));
				i++;
			}
			return points;
		}
		else if (direction == GameConstants.WEST)
		{
			while(map.get(currentLocation.x - i, currentLocation.y)!= null && i<n)
			{
				points.add(new Point(currentLocation.x - i, currentLocation.y));
				i++;
			}
			return points;
		}
		else if (direction == GameConstants.NORTHEAST)
		{
			while(map.get(currentLocation.x + i, currentLocation.y - i)!= null && i<n)
			{
				points.add(new Point(currentLocation.x + i, currentLocation.y - i));
				i++;
			}
			return points;
		}
		else if (direction == GameConstants.NORTHWEST)
		{
			while(map.get(currentLocation.x - i, currentLocation.y - i)!= null && i<n)
			{
				points.add(new Point(currentLocation.x - i, currentLocation.y - i));
				i++;
			}
			return points;
		}
		else if (direction == GameConstants.SOUTHEAST)
		{
			while(map.get(currentLocation.x + i, currentLocation.y + i)!= null && i<n)
			{
				points.add(new Point(currentLocation.x + i, currentLocation.y + i));
				i++;
			}
			return points;
		}
		else if (direction == GameConstants.SOUTHWEST)
		{
			while(map.get(currentLocation.x - i, currentLocation.y + i)!= null && i<n)
			{
				points.add(new Point(currentLocation.x - i, currentLocation.y + i));
				i++;
			}
			return points;
		}
		//never reached
		return null;
	}
	
	/* Deadlock detection -- if player is bouncing between two points */
	static boolean isInDeadlock()
	{
		int i = G6Player.history.size()-1;
		if(i<3)
			return false;
		int j = 1;
		boolean flag = false;
		while(j <= 2 && i >= 3)
		{
			if(G6Player.history.get(i).x == G6Player.history.get(i-2).x 
					&& G6Player.history.get(i).y == G6Player.history.get(i-2).y 
					&& G6Player.history.get(i-1).x == G6Player.history.get(i-3).x 
					&& G6Player.history.get(i-1).y == G6Player.history.get(i-3).y)
			{
				flag = true;
			}
			else
				flag = false;
			++j;
			--i;
		}
		return flag;
	}
	
	/* returns true if there is an opponent on a cell in the passed direction */
	public static boolean isOpponentInDirection(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, int direction)
	{
		for(int i=0;i<offsets.length;++i)
		{
			if (offsets[i] != currentLocation
					&& pointToDirection(currentLocation, offsets[i]) == direction
					&& hasExplorer[i])
				return true;
		}
		return false;
	}

	public static Move randomMove() {
		// Don't consider STAYPUT.
		return new Move(ACTIONS[rand.nextInt(ACTIONS.length - 1) + 1]);
	}

	/**
	 * Removes any points from set which are in list.
	 */
	public static void removePointsInList(Set<Point> set, List<Point> list) {
		for (Point point : list) {
			if (set.contains(point)) {
				set.remove(point);
			}
		}
	}

}
