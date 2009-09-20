package explorandum.g6;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Move;
import explorandum.g6.pathfinding.AStarPathFinder;
import explorandum.g6.pathfinding.Mover;
import explorandum.g6.pathfinding.Path;
import explorandum.g6.pathfinding.PathFinder;
import explorandum.g6.pathfinding.TileBasedMap;
import explorandum.g6.pathfinding.Path.Step;

public class FindRegionBehavior implements Behavior, GameConstants {

	private static final int MIN_REGION_SIZE_TO_WEAVE = 10;
	
	private Map map;
	private Logger log;
	private final int AStarLookupDepth = 1000;
	private WeavingBehavior weavingBehavior;
	private List<Point> currentRegion; // Null means no current region
	private Set<Point> currentRegionOffLimits;
	private InitialWeaveState initialWeaveState; // Initial state for weave we're trying to do

	/**
	 * For use by unit tests.
	 */
	protected FindRegionBehavior() {
	}
	
	/**
	 * @param weavingBehavior When it gets to the new area, sets up the
	 * directions the weaving behavior should move in.
	 */
	public FindRegionBehavior(Logger log, Map map,
			WeavingBehavior weavingBehavior) {
		this.map = map;
		this.log = log;
		this.weavingBehavior = weavingBehavior;
		currentRegion = null;
		initialWeaveState = new InitialWeaveState();
	}
	
	/**
	 * For use by unit tests.
	 */
	protected void init(Logger log, Map map) {
		this.map = map;
		this.log = log;
	}
	
	public String getName() {
		return "go to uncovered area";
	}

	public List<Point> getCurrentRegion() {
		return currentRegion;
	}
	
	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time) {
		FindRegionResult result = go(currentLocation, offsets, hasExplorer, otherExplorers, terrain, time);
		return result.move;
	}
	
	public FindRegionResult go(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time) {
		FindRegionResult result = new FindRegionResult();
		result.move = realMove(currentLocation, offsets, hasExplorer,
				otherExplorers, terrain, time);
		if (result.move == null) {
			if (currentRegion != null && currentRegion.size() > MIN_REGION_SIZE_TO_WEAVE) {
				result.status = FindRegionResult.AT_REGION;
			} else {
				result.status = FindRegionResult.NO_REGION;
			}
			currentRegion = null;
		} else {
			result.status = FindRegionResult.GOING_TO_REGION;
		}
		return result;
	}

	public Move realMove(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time) {
		Move move = null;
		if (currentRegion != null) {
			move = getMoveTowardsRegion(currentLocation, currentRegion);
		}
		List<ArrayList<Point>> uncoveredRegions = getUncoveredRegions();
		Set<Point> exploredPoints = map.getExploredPoints();
		while (move == null && uncoveredRegions.size() > 0) {
			log.debug("uncovered regions size: " + uncoveredRegions.size());
			if (currentRegion == null) {
				log.debug("Don't have a region. Picking one.");
				currentRegion = chooseBestRegionToExplore(uncoveredRegions, currentLocation);
				currentRegionOffLimits = exploredPoints;
				// Don't make anything in the region off limits
				Util.removePointsInList(currentRegionOffLimits, currentRegion);
				if (currentRegion.size() < MIN_REGION_SIZE_TO_WEAVE) {
					uncoveredRegions.remove(currentRegion);
					currentRegion = null;
					move = null;
					continue;
				}
			}
			move = getMoveTowardsRegion(currentLocation, currentRegion);
			log.debug("move: " + move);
			if (move == null) { // We can't get to that region.
				uncoveredRegions.remove(currentRegion);
				currentRegion = null;
				// TODO If we can be sure we can never get there, make sure we
				// don't check it again in the future
			} else if (getAction(move) == STAYPUT) {
				log.debug("Got to desired region.");
				// Check if this region is worth weaving
				ArrayList<Point> region = getUnseenPointsOf(currentRegion);
				if (region.size() < MIN_REGION_SIZE_TO_WEAVE) {
					uncoveredRegions.remove(currentRegion);
					currentRegion = null;
					move = null;
				} else {
					weavingBehavior.setOfflimits(currentRegionOffLimits);
					weavingBehavior.setInitialState(initialWeaveState);
					return null;
				}
			}
		}
		if (move != null && getAction(move) == STAYPUT) {
			weavingBehavior.setOfflimits(currentRegionOffLimits);
			weavingBehavior.setInitialState(initialWeaveState);
			return null;
		} else {
			return move;
		}
	}

	/**
	 * Move.getAction() wrapper.
	 */
	private int getAction(Move move) {
		int action;
		try {
			action = move.getAction();
		} catch (Exception e) {
			e.printStackTrace();
			action = STAYPUT;
		}
		return action;
	}

	private ArrayList<Point> getUnseenPointsOf(List<Point> points) {
		ArrayList<Point> unseenPoints = new ArrayList<Point>();
		for (int x = map.getMinX(); x <= map.getMaxX(); x++) {
			for (int y = map.getMinY(); y <= map.getMaxY(); y++) {
				if (map.get(x, y) == null) {
					unseenPoints.add(new Point(x, y));
				}
			}
		}
		return unseenPoints;
	}

	protected List<ArrayList<Point>> getUncoveredRegions() {
		ArrayList<Point> unseenPoints = new ArrayList<Point>();
		for (int x = map.getMinX(); x <= map.getMaxX(); x++) {
			for (int y = map.getMinY(); y <= map.getMaxY(); y++) {
				if (map.get(x, y) == null) {
					unseenPoints.add(new Point(x, y));
				}
			}
		}
		
		ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();
		while(!unseenPoints.isEmpty())
		{
				Point p = unseenPoints.get(0);
				ArrayList<Point> region = getRegion(p, unseenPoints);
				regions.add(region);
				removePoints(unseenPoints,region);
		}

		return regions;
	}
	
	/**
	 * Removes points that are in in region from unseenPoints 
	 */
	private void removePoints(ArrayList<Point> unseenPoints, ArrayList<Point> region)
	{
		for(Point p:region)
		{
			if(unseenPoints.contains(p))
				unseenPoints.remove(p);
		}
	}
	
		
	/**
	 * BFSing for a region starting from a point p
	 */
	private ArrayList<Point> getRegion(Point p, ArrayList<Point> unseenPoints) {
		ArrayList<Point> region = new ArrayList<Point>();
		HashSet<Point> queue = new HashSet<Point>();
		queue.add(p);
		region.add(p);
		while (!queue.isEmpty()) {
			HashSet<Point> queueCopy = (HashSet<Point>)queue.clone();
			for (Point point : queue) {
				ArrayList<Point> adjPoints = getAdjPoints(point);
				for (Point adjPoint : adjPoints) {
					if (!region.contains(adjPoint)) {
						if(unseenPoints.contains(adjPoint))
						{
							region.add(adjPoint);
							queueCopy.add(adjPoint);
						}
					}
				}
				queueCopy.remove(point);
			}
			queue = queueCopy;
		}
		return region;
	}
	
	private ArrayList<Point> getAdjPoints(Point p) {
		ArrayList<Point> adjPoints = new ArrayList<Point>();
		adjPoints.add(new Point(p.x - 1, p.y));
		adjPoints.add(new Point(p.x + 1, p.y));

		adjPoints.add(new Point(p.x - 1, p.y - 1));
		adjPoints.add(new Point(p.x, p.y - 1));
		adjPoints.add(new Point(p.x + 1, p.y - 1));

		adjPoints.add(new Point(p.x - 1, p.y + 1));
		adjPoints.add(new Point(p.x, p.y + 1));
		adjPoints.add(new Point(p.x + 1, p.y + 1));
		return adjPoints;
	}
	
	private List<Point> chooseBestRegionToExplore(List<ArrayList<Point>> uncoveredRegions, Point currentLocation) {
		assert uncoveredRegions.size() > 0;
		// Returns largest uncovered region
		double maxSizeByPathLength = Double.MIN_VALUE;
		int index = 0;
		for (int i = 0; i < uncoveredRegions.size(); ++i) {
			Point dest = Util.getClosestPointInRegion(currentLocation,
					uncoveredRegions.get(i));
			double tempSizeByPathLength = 1.0
					* (uncoveredRegions.get(i).size())
					/ Util.getTimeToDest(currentLocation, dest, map);
			if (tempSizeByPathLength > maxSizeByPathLength) {
				maxSizeByPathLength = tempSizeByPathLength;
				index = i;
			}
		}
		return uncoveredRegions.get(index);
	}
	
	class ExplorerMover implements Mover {
	}
	
	class ExplorerMap implements TileBasedMap {
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
	
	/**
	 * @return Returns a STAYPUT Move if already in region or null if can't get
	 *         there.
	 */
	private Move getMoveTowardsRegion(Point currentLocation, List<Point> region) {
		// It looks like the path finder won't find a path to the current cell,
		// so we have to manually check if we've already gotten to the region.
		// TODO Move check to "if (path == null)" for efficiency
		// With the new initial weave state choosing, we want to get to the
		// desired point, not just anywhere in the region.
		//		for (Point point : region) {
//			if (point.equals(currentLocation)) {
//				log.debug("At destination: " + currentLocation);
//				return new Move(STAYPUT);
//			}
//		}
		InitialWeaveState weave = pickDestInRegion(currentLocation, (ArrayList<Point>) region);
		Point dest = weave.initialPoint;
		if (currentLocation.equals(dest)) {
			return new Move(STAYPUT);
		}
		ExplorerMap gameMap = new ExplorerMap(map);
		PathFinder pathFinder = new AStarPathFinder(gameMap, AStarLookupDepth, true);
		Point pathFindingSource = new Point(currentLocation.x - map.getMinX(), currentLocation.y - map.getMinY());
		Point pathFindingDest = new Point(dest.x - map.getMinX(), dest.y - map.getMinY());
		log.debug("Finding path from " + pathFindingSource + " to " + pathFindingDest);
		Path path = pathFinder.findPath(new ExplorerMover(),
				pathFindingSource.x, pathFindingSource.y, pathFindingDest.x,
				pathFindingDest.y);
		if (path == null) {
			return null;
//		} else if (path.getLength() <= 1) {
//			log.debug("At destination: " + currentLocation);
//			return new Move(STAYPUT);
		} else {
			log.debug("Found path: " + pathToString(path));
			Step step = path.getStep(1); // 0th step is current position
			Point point = new Point(map.getMinX() + step.getX(),
					map.getMinY() + step.getY());
			return new Move(Util.pointToDirection(currentLocation, point));
		}
	}

	private String pathToString(Path path) {
		String result = new String();
		for (int i = 0; i < path.getLength(); i++) {
			result += path.getStep(i).getX() + "," + path.getStep(i).getY() + "; ";
		}
		return result;
	}

	private InitialWeaveState pickDestInRegion(Point currentLocation,
			ArrayList<Point> region) {
		Point minX = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Point maxX = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
		Point minY = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Point maxY = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

		ArrayList<Point> minXs = new ArrayList<Point>();
		ArrayList<Point> maxXs = new ArrayList<Point>();
		ArrayList<Point> minYs = new ArrayList<Point>();
		ArrayList<Point> maxYs = new ArrayList<Point>();
		for (Point p : region) {
			if (p.x < minX.x) {
				minX = p;
				minXs.clear();
				minXs.add(p);
			} else if (p.x == minX.x) {
				minXs.add(p);
			}

			if (p.x > maxX.x) {
				maxX = p;
				maxXs.clear();
				maxXs.add(p);
			} else if (p.x == maxX.x) {
				maxXs.add(p);
			}

			if (p.y < minY.y) {
				minY = p;
				minYs.clear();
				minYs.add(p);
			} else if (p.y == minY.y) {
				minYs.add(p);
			}

			if (p.y > maxY.y) {
				maxY = p;
				maxYs.clear();
				maxYs.add(p);
			} else if (p.y == maxY.y) {
				maxYs.add(p);
			}

		}

		ArrayList<Point> closestExtrema = new ArrayList<Point>();

		closestExtrema.add(Util.getClosestPointInRegion(currentLocation, minXs));
		closestExtrema.add(Util.getClosestPointInRegion(currentLocation, maxXs));
		closestExtrema.add(Util.getClosestPointInRegion(currentLocation, minYs));
		closestExtrema.add(Util.getClosestPointInRegion(currentLocation, maxYs));

		initialWeaveState.initialPoint = Util.getClosestPointInRegion(currentLocation,
				closestExtrema);

		if (maxXs.contains(initialWeaveState.initialPoint)) {
			if (isPointInDirInRegion(initialWeaveState.initialPoint, SOUTH, region))
				initialWeaveState.mainAxisDirection = SOUTH;
			else
				initialWeaveState.mainAxisDirection = NORTH;
			initialWeaveState.offAxisDirection = WEST;
		} else if (minXs.contains(initialWeaveState.initialPoint)) {
			if (isPointInDirInRegion(initialWeaveState.initialPoint, SOUTH, region))
				initialWeaveState.mainAxisDirection = SOUTH;
			else
				initialWeaveState.mainAxisDirection = NORTH;
			initialWeaveState.offAxisDirection = EAST;
		}
		if (maxYs.contains(initialWeaveState.initialPoint)) {
			if (isPointInDirInRegion(initialWeaveState.initialPoint, EAST, region))
				initialWeaveState.mainAxisDirection = EAST;
			else
				initialWeaveState.mainAxisDirection = WEST;
			initialWeaveState.offAxisDirection = SOUTH;
		}
		if (minYs.contains(initialWeaveState.initialPoint)) {
			if (isPointInDirInRegion(initialWeaveState.initialPoint, EAST, region))
				initialWeaveState.mainAxisDirection = EAST;
			else
				initialWeaveState.mainAxisDirection = WEST;
			initialWeaveState.offAxisDirection = NORTH;
		}
		log.debug("Initial point in region: " + initialWeaveState.initialPoint);
		return initialWeaveState;
	}
	
	public boolean isPointInDirInRegion(Point p, int dir,
			ArrayList<Point> region) {
		if (dir == NORTH) {
			if (region.contains(new Point(p.x, p.y - 1)))
				return true;
		} else if (dir == SOUTH) {
			if (region.contains(new Point(p.x, p.y + 1)))
				return true;
		} else if (dir == EAST) {
			if (region.contains(new Point(p.x + 1, p.y)))
				return true;
		} else if (dir == WEST) {
			if (region.contains(new Point(p.x - 1, p.y)))
				return true;
		}
		return false;
	}
// private ArrayList<ArrayList<Point>>
// breakRegionIntoRectangles(ArrayList<Point> region)
//	{
//		//TODO
//		if(
//		return region;
//	}
}
