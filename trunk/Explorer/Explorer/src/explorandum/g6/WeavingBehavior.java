package explorandum.g6;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Move;
import explorandum.g6.Util;

public class WeavingBehavior implements GameConstants {
	private Logger log;
	private int range;
	private Map map;
	
	private static final int HORIZONTAL = 1;
	private static final int VERTICAL = 2;

	private int action = -1;
	private int mainAxis;
	private int offAxisDirection;
	private int offAxisMoves;
	private int lastMainAxisDirection;
	
	private HashMap<Point, Integer> cellsVisited; // how often each cells has been visited
	
//	private HashSet<Point> weavableRegion;
	private Set<Point> offLimitsPoints;
	
	public WeavingBehavior(Logger log, int range, Map map) {
		this.log = log;
		this.range = range;
		this.map = map;
		cellsVisited = new HashMap<Point, Integer>();
//		weavableRegion = new HashSet<Point>();
	}
	
	/**
	 * If a cell is visited for the 3rd time, returns null to indicate player is
	 * stuck. It's possible to visit a cell twice and not be stuck.
	 */
	public Move move(Point currentLocation, Point[] offsets, Integer[] terrain) {
		log.debug("\n\nWeavingBehavior.move()");
//		map.setRegion(getOffLimitsPoints());
//		log.debug("map:\n" + map.toString(currentLocation));

		// Initialize directions and stuff
		if (action == -1) {
			setInitialState(currentLocation);
		}
		
		// Check if we're moving back and forth without progress
		if (playerIsStuck(currentLocation)) {
			log.debug("WeavingBehavior detected stuck player, returning null");
			return null;
		}
		
		// If we were blocked while moving in the off axis direction and had to
		// switch to main axis movement, and now we can continue in the off axis
		// direction, do so.
		if (offAxisMoves < 2 * range + 1) {
			int indexOff = Util.directionToIndex(currentLocation,
					offAxisDirection, offsets);
			if (terrain[indexOff] == LAND
					&& !offLimitsPoints.contains(offsets[indexOff])) {
				log.debug("Resuming incomplete movement in off axis direction.");
				offAxisMoves++;
				return new Move(offAxisDirection);
			}
		}
		
		// Check if we crashed into something.
		// Each call to checkForCrash will update the movement (ie turn) if we crashed.
		// We might legitimately crash and turn three times in a move, but if we
		// crash a forth time, we definitely have no move.
		if (checkForCrash(currentLocation, offsets, terrain) &&
				checkForCrash(currentLocation, offsets, terrain) &&
				checkForCrash(currentLocation, offsets, terrain) &&
				checkForCrash(currentLocation, offsets, terrain)) {
			log.debug("WeavingBehavior: multiple crashes; can't find move. "
					+ "Returning null.");
			return null;
		}
		
		// Moving in off axis direction
		if (action == offAxisDirection) {
			log.debug("Moving in off axis direction.");
			if (offAxisMoves == range * 2 + 1) {
				log.debug("Done moving in off axis direction.");
				action = Util.oppositeDirection(lastMainAxisDirection);
				lastMainAxisDirection = action;
			}
			offAxisMoves++;
		}
		
		return new Move(action);
	}

	/**
	 * Checks if the player has crashed into something. If so, turns as needed to continue the weave.
	 * @return Returns whether player crashed.
	 */
	private boolean checkForCrash(Point currentLocation, Point[] offsets, Integer[] terrain) {
		int index = Util.directionToIndex(currentLocation, action, offsets);
		if (terrain[index] != LAND || offLimitsPoints.contains(offsets[index])) {
			if (movingInMainAxisDirection()) {
				action = offAxisDirection;
				offAxisMoves = 0;
			} else {
				action = Util.oppositeDirection(lastMainAxisDirection);
				lastMainAxisDirection = action;
			}
			log.debug("WeavingBehavior: crashed into something. Changed action to "
							+ ACTION_NAMES[action]);
			return true;
		} else {
			return false;
		}
	}

	private boolean movingInMainAxisDirection() {
		if (action == NORTH || action == SOUTH) {
			if (mainAxis == VERTICAL) {
				return true;
			} else {
				return false;
			}
		} else if (action == EAST || action == WEST) {
			if (mainAxis == HORIZONTAL) {
				return true;
			} else {
				return false;
			}
		}
		assert false : "Invalid action.";
		return false;
	}

	/**
	 * A player is stuck if they've been on the same cell 3 times while weaving.
	 */
	private boolean playerIsStuck(Point currentLocation) {
		if (cellsVisited.containsKey(currentLocation)) {
			Integer i = cellsVisited.get(currentLocation);
			cellsVisited.put(currentLocation, i + 1);
			assert(cellsVisited.get(currentLocation).equals(i + 1));
		} else {
			cellsVisited.put(currentLocation, 1);
		}
		if (cellsVisited.get(currentLocation).intValue() >= 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * New function which must be manually triggered.
	 */
	public void setInitialState(InitialWeaveState state) {
		action = state.mainAxisDirection;

		if (state.mainAxisDirection == EAST || state.mainAxisDirection == WEST) {
			mainAxis = HORIZONTAL;
		} else {
			mainAxis = VERTICAL;
		}
		lastMainAxisDirection = state.mainAxisDirection;

		offAxisDirection = state.offAxisDirection;

		// Initialize it high so the "continue off axis movement" code doesn't
		// kick in immediately.
		offAxisMoves = range * 2 + 1;

		log.debug("setInitialState:");
		log.debug("mainAxis=" + mainAxis);
		log.debug("lastMainAxisDirection="
				+ ACTION_NAMES[lastMainAxisDirection]);
		log.debug("offAxisDirection=" + ACTION_NAMES[offAxisDirection]);
		log.debug("action=" + ACTION_NAMES[action]);
		log.debug("offAxisMoves=" + offAxisMoves);
	}
	
	@Deprecated
	private void setInitialState(Point currentLocation) {
		// As a first pass, figure out which two cardinal directions have more
		// unexplored tiles within 10 from current position.
		int[] unexploredCount = new int[10];
		for (int i = 0; i < unexploredCount.length; i++) {
			unexploredCount[i] = 0;
		}
		for (int i = 1; i <= 10; i++) {
			if (map.get(currentLocation.x, currentLocation.y - i) == null) {
				unexploredCount[NORTH]++;
			}
			if (map.get(currentLocation.x, currentLocation.y + i) == null) {
				unexploredCount[SOUTH]++;
			}
			if (map.get(currentLocation.x - 1, currentLocation.y) == null) {
				unexploredCount[WEST]++;
			}
			if (map.get(currentLocation.x + 1, currentLocation.y) == null) {
				unexploredCount[EAST]++;
			}
		}
		log.debug(unexploredCount[NORTH] + ", " + unexploredCount[SOUTH] + ", " + unexploredCount[WEST] + ", " + unexploredCount[EAST]);
		// TODO Factor out function
		int max = 0;
		int dir = -1;
		if (unexploredCount[NORTH] > max) {
			max = unexploredCount[NORTH];
			dir = NORTH;
		}
		if (unexploredCount[SOUTH] > max) {
			max = unexploredCount[SOUTH];
			dir = SOUTH;
		}
		if (unexploredCount[WEST] > max) {
			max = unexploredCount[WEST];
			dir = WEST;
		}
		if (unexploredCount[EAST] > max) {
			max = unexploredCount[EAST];
			dir = EAST;
		}
		assert dir != -1;
		action = dir;
		if (dir == EAST || dir == WEST) {
			mainAxis = HORIZONTAL;
		} else {
			mainAxis = VERTICAL;
		}
		lastMainAxisDirection = dir;
		
		if (mainAxis == HORIZONTAL) {
			if (unexploredCount[NORTH] > unexploredCount[SOUTH]) {
				offAxisDirection = NORTH;
			} else {
				offAxisDirection = SOUTH;
			}
		} else {
			if (unexploredCount[WEST] > unexploredCount[EAST]) {
				offAxisDirection = WEST;
			} else {
				offAxisDirection = EAST;
			}
		}
		
		// Initialize it high so the "continue off axis movement" code doesn't
		// kick in immediately.
		offAxisMoves = range * 2 + 1;
		
		log.debug("setInitialState:");
		log.debug("mainAxis=" + mainAxis);
		log.debug("lastMainAxisDirection=" + ACTION_NAMES[lastMainAxisDirection]);
		log.debug("offAxisDirection=" + ACTION_NAMES[offAxisDirection]);
		log.debug("action=" + ACTION_NAMES[action]);
		log.debug("offAxisMoves=" + offAxisMoves);
	}

//	public void setWeavableRegion(List<Point> weavablePoints) {
//		for (Point p : weavablePoints) {
//			weavableRegion.add(p);
//		}
//		// Set it up so the next call to move will figure out the directions.
//		action = -1;
//	}
//	
//	public void setWeave(Weave weave) {
//		for (Point p : weave.weavablePoints) {
//			weavableRegion.add(p);
//		}
//		// TODO
//	}
//	
	public void setOfflimits(Set<Point> points) {
		offLimitsPoints = points;
	}
	
	public List<Point> getOffLimitsPoints() {
		List<Point> list = new ArrayList<Point>(offLimitsPoints.size());
		for (Iterator iter = offLimitsPoints.iterator(); iter.hasNext();) {
			list.add((Point) iter.next());
		}
		return list;
	}
	
	public String getName() {
		return "weaving";
	}
}
