package explorandum.g6;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class G6Player implements Player {
	private static final int MAX_TIMEOUTS = 2;

	public final int MAX_BEHAVIOR_TRANSITIONS = 5;
	
	Logger log;
	public Random rand;
	public static Map map;

	private enum Behavior { COAST_FOLLOWING, WEAVING, FINDING_AREA };
	Behavior behavior;
	private WeavingBehavior weavingBehavior;
	private CoastFollowingBehavior coastFollowingBehavior;
	private FindRegionBehavior findRegionBehavior;
	private HillClimbingBehavior hillClimbingBehavior;
	public Behavior currentBehavior;
	protected static ArrayList<Point> history; //to store the history of points been on
	protected static HashSet<Point> pointsOnBridges; //to store the points that are known to be on a bridge
	private double totalTime = 0;
	private double averageTime = 0;
	private int numberOfMoves = 0;
	private double maxTime = -100000;

	static public int N = 0; //total rounds
	int range;
	int explorers;

	private int timeoutCount;

	private int turnsToHillclimb;
	static public int explorerID;
	
	public void register(int explorerID, int rounds, int explorers, int range,
			Logger log, Random rand) {
		G6Player.N = rounds;
		this.range = range;
		this.explorers = explorers;
		G6Player.explorerID = explorerID;
		this.log = log;
		this.rand = rand;
		Util.rand = rand;
		timeoutCount = 0;
		turnsToHillclimb = 0;
		history = new ArrayList<Point>();
		pointsOnBridges = new HashSet<Point>();
		map = new Map();
		weavingBehavior = new WeavingBehavior(log, range, map);
		findRegionBehavior = new FindRegionBehavior(log, map, weavingBehavior);
		coastFollowingBehavior = new CoastFollowingBehavior(map, log, range, explorers, 250);
		hillClimbingBehavior = new HillClimbingBehavior(log, range, map);
		behavior = Behavior.COAST_FOLLOWING;
		log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers
				+ "\nRange:" + range);
	}

	public Color color() throws Exception {
		return Color.CYAN;
	}
	
	
	public String name() {
		return "Captain Kirk";
	}

	/**
	 * Wrapper for realMove which makes sure we don't throw an exception or time
	 * out.
	 * 
	 * Positions are relative to starting position.
	 */
	@SuppressWarnings("deprecation")
	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time,Boolean StepStatus) throws Exception {
		
		//New thread
		Timer timer = new Timer(this, rand, currentLocation, offsets, hasExplorer, otherExplorers, terrain, time);
		
		Move m;
		long time1 = System.currentTimeMillis();
		
		//start thread
		timer.start();
		try {
			timer.join(900);
			if(timer.isAlive()) //if move is still being calculated after 980ms
			{
				timer.stop();
				timeoutCount++;
				log.debug("Almost struck out! Moving randomly!");
				return Util.randomMove();
			} else {
				timeoutCount = 0;
			}
			m = timer.getMove();
			
			long time2 = System.currentTimeMillis();
			totalTime += time2-time1;
			if((time2-time1) > maxTime)
				maxTime = time2-time1;
			++numberOfMoves;
			averageTime = totalTime/numberOfMoves;

			log.debug("Average time per move(ms): " + averageTime  + ", Max time(ms): " + maxTime);
			
			return m;
		} catch (Throwable error) {
			error.printStackTrace();
			return Util.randomMove();
		}
	}
	
	public Move realMove(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time) throws Exception {
		updateMap(currentLocation, offsets, hasExplorer, otherExplorers, terrain);

		if (time < 50) {
			Util.dontGoOnBridges = false;
		} else if (time == 50) {
			Util.dontGoOnBridges = true;
		}
		
//		if (true) {
//			return hillClimbingBehavior.move(currentLocation, offsets, hasExplorer, otherExplorers, terrain, time);
//		}
		
		Move move = null;
		String source = "unknown";
		for (int i = 0; move == null; i++) {
			// Make sure we don't infinitely loop
			assert i < MAX_BEHAVIOR_TRANSITIONS : "Too many behavior transitions.";
			
			if (behavior == Behavior.COAST_FOLLOWING) {
				move = coastFollowingBehavior.move(currentLocation, offsets, hasExplorer, otherExplorers, terrain, time);
				source = "coast following";
				if (move == null) {
					behavior = Behavior.FINDING_AREA;
				}
			} else if (behavior == Behavior.WEAVING) {
				move = weavingBehavior.move(currentLocation, offsets, terrain);
				source = "weaving";
				if (move == null) {
					behavior = Behavior.FINDING_AREA;
				}
			} else {
				if (timeoutCount > MAX_TIMEOUTS) {
					turnsToHillclimb = 20;
				}
				FindRegionResult result = null;
				if (turnsToHillclimb == 0) {
					result = findRegionBehavior.go(currentLocation, offsets, hasExplorer,
									otherExplorers, terrain, time);
					source = "find region";
					if (result.status == FindRegionResult.GOING_TO_REGION) {
						move = result.move;
					} else if (result.status == FindRegionResult.AT_REGION) {
						// TODO
						behavior = Behavior.WEAVING;
					}
				}
				if (result == null || result.status == FindRegionResult.NO_REGION) {
					move = hillClimbingBehavior.move(currentLocation, offsets,
							hasExplorer, otherExplorers, terrain, time);
					source = "hill climbing";
					if (move == null) {
						move = Util.randomMove();
						source = "random";
					}
				}
				if (turnsToHillclimb > 0) {
					turnsToHillclimb--;
				}
			}
		}
		
		// Debug output
		if (false) {
			if (source.equals("find region")) {
				map.setRegion(findRegionBehavior.getCurrentRegion());
			} else if (source.equals("weaving")) {
				map.setRegion(weavingBehavior.getOffLimitsPoints());
			} else {
				map.setRegion(null);
			}
			log.debug("map:\n" + map.toString(currentLocation));
			log.debug("Move " + ACTION_NAMES[move.getAction()] + " acquired from " + source + " behavior.");
		}
		
		return move;
	}

	public void updateMap(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers, Integer[] terrain) {
		for (int i = 0; i < offsets.length; i++) {
			double dist = Util.euclideanDist(offsets[i], currentLocation);
			Cell cell = new Cell(terrain[i], otherExplorers[i], dist);
			map.set(offsets[i].x, offsets[i].y, cell);
		}

		history.add(currentLocation);
	}

}
