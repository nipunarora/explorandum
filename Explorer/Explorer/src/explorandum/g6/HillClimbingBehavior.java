package explorandum.g6;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import explorandum.Logger;
import explorandum.Move;
import explorandum.GameConstants;

public class HillClimbingBehavior implements Behavior{

	private Map map;
	private Logger log;
	private Random rand;
	private int range;
	
	private final int WATER_SCORE = 4;//7;
	private final int LAND_SCORE = 0;//3;
	private final int MOUNTAIN_SCORE = -1;//1;
	private final int OPPONENT_WAS_ON_SCORE = -5;
	private final int NOT_VISITED_SCORE = 8;
	private final int SEEN_FROM_FAR_SCORE = 2;
	private final int BEEN_ON_CELL_SCORE = -5;
	public static ArrayList<Point> cellsCausingDeadlock;
	
	public HillClimbingBehavior(Logger log, int range, Map map) {
		cellsCausingDeadlock = new ArrayList<Point>();
		this.map = map;
		this.log = log;
		this.range = range;
		rand = new Random();
	}
	
	public String getName() {
		return "Hill climbing behavior";
	}

	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time) throws Exception {
		
		/*this stub used to implement a hill climber that chooses next point based upon unexplored neighbours divided by time to get there
		Point moveToPoint = null;
		double value = Double.MIN_VALUE;
		*/
		
		PointIntPair moveTo = new PointIntPair(currentLocation, Double.MIN_VALUE);
		ArrayList<Point> candidates = new ArrayList<Point>();
		for(Point exploredPoint: map.getMapPoints())
		{
			Cell exploredCell = map.get(exploredPoint.x, exploredPoint.y);
			if(exploredCell.getTerrainType() == GameConstants.LAND)
			{
				int dir = Util.pointToDirection(currentLocation, exploredPoint);
				if(!Util.isOpponentInDirection(currentLocation, offsets, hasExplorer, dir))
				{
					Point oneBeforeExploredPoint = Util.directionToPoint(exploredPoint, Util.oppositeDirection(dir));
					if(!G6Player.pointsOnBridges.contains(oneBeforeExploredPoint))
					{
						double val = score(exploredPoint, exploredCell);
						if(exploredCell.wasOpponentSeenOn() == true)
							val += -200;//OPPONENT_WAS_ON_SCORE;
						//else if(map.get(exploredPoint.x, exploredPoint.y).getDistanceSeenFrom() == 0)
						//	val += -100;
						//if(map.get(exploredPoint.x, exploredPoint.y).getDistanceSeenFrom() >= range-1)
						//	val += SEEN_FROM_FAR_SCORE + 3;
						else if(exploredCell.getDistanceSeenFrom() >= range-2)
							val += SEEN_FROM_FAR_SCORE;
						
						if(val > moveTo.value)
						{ 
							moveTo.p = exploredPoint;
							moveTo.value = val;
							candidates.clear();
							candidates.add(moveTo.p);
						}
						else if(val == moveTo.value)
						{
							candidates.add(exploredPoint);
						}
					}
				}
			}
		}
		
		Move m = null;
		Point bestPointToGoTo = null;
		boolean allPointsOnBridges = true;
		boolean wentIntoLoop = false;
		/* this stub used to break ties between explored points that have the same number of unexplored neighbors -- choose the one that is closest in terms of time*/
		double minDist = Integer.MAX_VALUE;
		Point pointOnBridge = new Point();
//		System.out.println("Current Location: " + currentLocation.x +", "+ currentLocation.y);
		for(Point pointToGoTowards:candidates)
		{
			int dir = Util.pointToDirection(currentLocation, pointToGoTowards);
			Point oneBeforePointToGoTowards = Util.directionToPoint(pointToGoTowards, Util.oppositeDirection(dir));
			if(!Util.isPointOnBridge(oneBeforePointToGoTowards, map))
			{
				allPointsOnBridges = false;
				double pathLength = Util.manhattanDist(currentLocation, pointToGoTowards);
				//MovePathLengthPair temp = Util.getMoveTowardsPoint(currentLocation, pointToGoTowards, map);
				//Point nextLoc = Util.directionToPoint(currentLocation, temp.move.getAction());
				//if(!HillClimbingBehavior.cellsCausingDeadlock.contains(nextLoc))
				//{	
					wentIntoLoop = true;
					if(pathLength < minDist) 
					{	
						bestPointToGoTo = pointToGoTowards;
						minDist = pathLength;
					}
				//}

			}
			else
			{
				pointOnBridge.x = oneBeforePointToGoTowards.x;
		    	pointOnBridge.y = oneBeforePointToGoTowards.y;
		    	G6Player.pointsOnBridges.add(pointOnBridge);
//		    	System.out.println("On bridge next Loc:" + oneBeforePointToGoTowards.x + ", " + oneBeforePointToGoTowards.y);
			}
		} 
		
		if(bestPointToGoTo != null)
		{
			MovePathLengthPair temp = Util.getMoveTowardsPoint(currentLocation, bestPointToGoTo, map);
			m = temp.move;
		}
		
		
		/* this stub used to implement a hill climber that does a DFS around the currentLocation and the offset points to choose the next location as the one with most unexplored neighbours
		PointIntPair moveTo = DFS(currentLocation, 0, 5, new PointIntPair(currentLocation, Integer.MIN_VALUE));
		//for(int i = 0 ; i < offsets.length; ++i)
		//{
		//	if(map.get(offsets[i].x, offsets[i].y).getTerrainType() == GameConstants.LAND)
		//	{
		//		PointIntPair temp = DFS(offsets[i], 0, 2, new PointIntPair(offsets[i], Integer.MIN_VALUE));
		//		if(temp.value > moveTo.value)
		//		{
		//			moveTo.value = temp.value;
		//			moveTo.p = temp.p;
		//		}
		//	}
		//}
		Move m = Util.getMoveTowardsPoint(currentLocation, moveTo.p, map, 10);
		*/
		if(m == null && wentIntoLoop == false)
			new Move(GameConstants.ACTIONS[rand.nextInt(GameConstants.ACTIONS.length-1)+1]);
		else if(allPointsOnBridges == true)
		{
			int dir = Util.pointToDirection(currentLocation, pointOnBridge);
			int dirToMoveTowards = Util.oppositeDirection(dir);
			m = new Move(dirToMoveTowards);
		}
		if(Util.isInDeadlock())
			m = new Move(GameConstants.ACTIONS[rand.nextInt(GameConstants.ACTIONS.length-1)+1]);
		return m;
	}
	
	
	private PointIntPair DFS(Point currentLocation, int curDepth, int maxDepth, PointIntPair bestPoint)
	{
		
		ArrayList<Point> neighbours = getAdjPoints(currentLocation);
		
		int val = 0;
		if(isVisited(currentLocation) && map.get(currentLocation.x, currentLocation.y).getTerrainType() == GameConstants.LAND)		
			for(Point neighbour:neighbours)
				if(!isVisited(neighbour))
					++val;
		if(val > bestPoint.value)
		{
			bestPoint.value = val;
			bestPoint.p = currentLocation;
		}
		
		if(curDepth < maxDepth)
		{
			for(Point neighbour:neighbours)
			{
					PointIntPair p = DFS(neighbour, curDepth + 1, maxDepth, bestPoint);
					if(p.value > bestPoint.value)
					{
						bestPoint.value = p.value;
						bestPoint.p = p.p;
					}
			}
		}
		return bestPoint;
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

	private boolean isVisited(Point p)
	{
		if(map.get(p.x, p.y)==null)
			return false;
		else
			return true;
	}
	
	private int score(Point p, Cell c)
	{
		int score = 0;
		ArrayList<Point> neighbours = getAdjPoints(p);
		for(Point neighbour:neighbours)
		{
			if(!isVisited(neighbour))
				score += NOT_VISITED_SCORE;
			//else if(map.get(neighbour.x, neighbour.y).getDistanceSeenFrom() == 1)
			//	score += BEEN_ON_CELL_SCORE;
		
			//else if(map.get(neighbour.x, neighbour.y).getDistanceSeenFrom() == 1)
			//	score += BEEN_ON_CELL_SCORE;
			else if(c.getTerrainType() == GameConstants.WATER)
				score += WATER_SCORE;
			else if(c.getTerrainType() == GameConstants.LAND)
				score += LAND_SCORE;
			//else if(map.get(neighbour.x, neighbour.y).getTerrainType() == GameConstants.MOUNTAIN)
			//	score += MOUNTAIN_SCORE;
		
		}
		return score;
	}
	

}
