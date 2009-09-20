package explorandum.g6;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Move;
import java.util.HashMap;

public class CoastFollowingBehavior implements GameConstants, Behavior {
	private int action;
	private boolean coastRunnerActive;
	private int leaning_direction;
	private boolean started;
	private Map map;
	private Logger log;
	private HashMap<Point, Integer> cellsVisited; // how often each cells has been visited
	int self_trap_breaker;
	boolean pushOut;
	int pushCounter;
	Point startPoint;
	int range;
	private int pushOutAction;
	int[] playersSeenHistory;
	int explorers;
	int maxtime;
	int currenttime;
	CoastFollowerData diagonalPushIn;
	int next_leaning;
	int next_act;
	
	public CoastFollowingBehavior(Map map, Logger log, int range, int explorers, int initialTime) {
		coastRunnerActive = true;
		action = -1;
		this.map = map;
		this.log = log;
		cellsVisited = new HashMap<Point, Integer>();
		self_trap_breaker = 0;
		pushCounter = 0;
		this.range = range;
		pushOut = false;
		startPoint = new Point(0,0);
		this.explorers = explorers;
		maxtime = initialTime;
		currenttime = 0;
		diagonalPushIn = new CoastFollowerData(null, 0, null);
	}

	public void setInactive() {
		action = -1;
		started = false;
		self_trap_breaker = 0;
		coastRunnerActive = false;
		pushCounter = 0;
		currenttime = 0;
	}
	
	/**
	 * @return Returns null if can't follow coast.
	 */
	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time) throws Exception {
		
		if (!coastRunnerActive) {
			return null;
		}
		
		currenttime += 2;
		if (!isCoastFollowingTime(currenttime)) {
			setInactive();
			return null;
		}
		
		int[] playersSeen = new int[explorers];
		
		/*//Keep count of how many times we've seen a player in a row
		for (int i = 0; i < otherExplorers.length; i++) {
			for (int j = 0; j < otherExplorers[i].length; j++) {
				playersSeen[j]++;
			}
		}
		
		//Keep a climbing 'danger' reading on the player. If we see him many times in
		//a row, increment the history. If we don't see him, subtract 2.
		for (int i = 0; i < playersSeen.length; i++) {
			if (playersSeenHistory[i] + playersSeen[i] == playersSeenHistory[i]) {
				playersSeenHistory[i] -= 2;
				if (playersSeenHistory[i] < 0) 
					playersSeenHistory[i] = 0;
			}
			else
				playersSeenHistory[i] += playersSeen[i];
		}*/
		
		//If we've seen him more than 10 times in a row (very dangerous) and 
		//we're 'behind' him, stop coast following because we may be following 
		//and getting nothing
		/*for (int i = 0; i < playersSeenHistory.length; i++) {
			if (playersSeenHistory[i] > 10 && weAreBehind) {
				
				setInactive();
				return null;
			}
		}*/
		
		int next_action = action;
		
		if (self_trap_breaker == 10) {
			setInactive();
			return null;
		}
		
		//Got around the map, stop coastrunning
		if (currentLocation.x == startPoint.x && currentLocation.y == startPoint.y && started) {
			setInactive();
			return null;
		}
		
		//push out to the shore again
		if (pushOut) {
			pushCounter = 0;
			pushOut = false;
			int dx = diagonalPushIn.getTarget().x - currentLocation.x;
			int dy = diagonalPushIn.getTarget().y - currentLocation.y;
			
			//Sometimes we'll end up in a weird place. If we find that we're too
			//far from where we wanted to get, we just backtrack the last move.
			if (dx < -1 || dx > 1 || dy < -1 || dy > 1)
				return new Move(Util.oppositeDirection(pushOutAction));
			
			//Otherwise move diagonally towards where we would've moved had we not done
			//the inward push.
			int diagonal = getDirection(dx, dy);
			leaning_direction = next_leaning;
			action = next_act;
			return new Move(diagonal);
		}
		
		//Pushes inland and sets the coastfollower to push back out on the next turn
		if (pushCounter > range && range >= 3) {
			int index = Util.directionToIndex(currentLocation, Util.oppositeDirection(leaning_direction), offsets);
			if (!hasVisited(offsets[index])) {
				if (!hasExplorer[index]) {
					if (terrain[index] == LAND) {
						pushCounter = 0;
						pushOutAction = Util.oppositeDirection(leaning_direction);
						if (pushOutAction != Util.oppositeDirection(action)) {
							int last_action = action;
							int last_leaning = leaning_direction;
							Move nextMove = move(currentLocation, offsets, hasExplorer,
									otherExplorers, terrain, time);
							next_leaning = leaning_direction;
							next_act = action;
							action = last_action;
							leaning_direction = last_leaning;
							
							if (nextMove.getAction() != pushOutAction) {
								int wouldveBeen = Util.directionToIndex(currentLocation, nextMove.getAction(), offsets);
								if (-1 * offsets[index].x + offsets[wouldveBeen].x >= -1 &&
										-1 * offsets[index].x + offsets[wouldveBeen].x <= 1 &&
										-1 * offsets[index].y + offsets[wouldveBeen].y <= 1 &&
										-1 * offsets[index].y + offsets[wouldveBeen].y >= -1) {
									diagonalPushIn.setTarget(offsets[wouldveBeen]);
									pushOut = true;
									return new Move(pushOutAction);
								}
								else
									pushCounter = 0;
							}
							else
								pushCounter = 0;
						}
					}
				}
			}
		}
		
		//If unstarted, set the start location
		if (!started) {
			startPoint.x = currentLocation.x;
			startPoint.y = currentLocation.y;
			started = true;
		}
		addVisitedPoint(currentLocation);
		
		ArrayList<CoastFollowerData> interestingPoints = new ArrayList<CoastFollowerData>();
		ArrayList<CoastFollowerData> validMoves = new ArrayList<CoastFollowerData>();
		
		//Loop through adjacent cells, record water cells
		for (int i = 0; i < _dx.length; i++) {
			if (_dx[i] != 0 || _dy[i] != 0) {
				// Find this cell in visible cells list
				for (int j = 0; j < offsets.length; j++) {
					Point point = offsets[j];

					// if adjacent cell and WATER, add it to the beach points
					if (point.x == currentLocation.x + _dx[i]
							&& point.y == currentLocation.y + _dy[i]
							&& (terrain[j] == WATER || terrain[j] == MOUNTAIN)) {
						interestingPoints.add(new CoastFollowerData(new Point(_dx[i], _dy[i]), point, terrain[j], hasExplorer[j]));
						break;
					}
				}
			}
		}
		
		if (action == -1) {
			//If only 1 point of interest adjacent to us, move to another cell that's adjacent to it
			if (interestingPoints.size() == 1) {
				//check diagonals around water
				for (int i = 0; i < offsets.length; i++) {
					if (terrain[i] == LAND) {
						if (isOrthogonal(interestingPoints.get(0).getOffset(),offsets[i])) {
							int dx = -1 * currentLocation.x + offsets[i].x;
							int dy = -1 * currentLocation.y + offsets[i].y;
							
							action = getDirection(dx, dy);
							leaning_direction = getDirection(interestingPoints.get(0).getOffsetFromCurrent().x, interestingPoints.get(0).getOffsetFromCurrent().y);
							return new Move(action);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < interestingPoints.size(); i++) {
			for (int j = i + 1; j < interestingPoints.size(); j++) {
				//check for water cells that are orthogonal to each other.
				//move along these "walls" of water.
				if (isOrthogonal(interestingPoints.get(i).getOffsetFromCurrent(),interestingPoints.get(j).getOffsetFromCurrent())) {					
					if (isOrthogonal(interestingPoints.get(i).getOffset(),currentLocation)) {
						int dx = interestingPoints.get(j).getOffsetFromCurrent().x - interestingPoints.get(i).getOffsetFromCurrent().x;
						int dy = interestingPoints.get(j).getOffsetFromCurrent().y - interestingPoints.get(i).getOffsetFromCurrent().y;

						if (action != -1)
							next_action = getDirection(dx, dy);
						else {
							action = getDirection(dx, dy);
							int def_index = Util.directionToIndex(currentLocation, action, offsets);
							if (terrain[def_index] != LAND) { action = -1; continue; }
							leaning_direction = getDirection(interestingPoints.get(i).getOffsetFromCurrent().x, interestingPoints.get(i).getOffsetFromCurrent().y);
							if (hasExplorer[def_index]) {
								CoastFollowerData firstMove = new CoastFollowerData(new Move(action),leaning_direction,offsets[def_index]);
								validMoves.add(firstMove);
								action = -1; 
								continue; 
							}
							return new Move(action);
						}
						
						int index = Util.directionToIndex(currentLocation, next_action, offsets);
						if (terrain[index] != LAND) continue;
						//if (hasExplorer[index]) continue;
						
						CoastFollowerData newMove = new CoastFollowerData(new Move(next_action),(getDirection(interestingPoints.get(i).getOffsetFromCurrent().x, interestingPoints.get(i).getOffsetFromCurrent().y)), offsets[index]);
						
						if (next_action == Util.oppositeDirection(action)) {
							newMove.setOpposite(true);
						}
						
						validMoves.add(newMove);
						continue;
					}
					else if (isOrthogonal(interestingPoints.get(j).getOffset(),currentLocation)) {
						int dx = interestingPoints.get(i).getOffsetFromCurrent().x - interestingPoints.get(j).getOffsetFromCurrent().x;
						int dy = interestingPoints.get(i).getOffsetFromCurrent().y - interestingPoints.get(j).getOffsetFromCurrent().y;

						if (action != -1)
							next_action = getDirection(dx, dy);
						else {
							action = getDirection(dx, dy);
							int def_index = Util.directionToIndex(currentLocation, action, offsets);
							if (terrain[def_index] != LAND) { action = -1; continue; }
							leaning_direction = getDirection(interestingPoints.get(i).getOffsetFromCurrent().x, interestingPoints.get(i).getOffsetFromCurrent().y);
							if (hasExplorer[def_index]) {
								CoastFollowerData firstMove = new CoastFollowerData(new Move(action),leaning_direction,offsets[def_index]);
								validMoves.add(firstMove);
								action = -1; 
								continue; 
							}
							return new Move(action);
						}
						
						int index = Util.directionToIndex(currentLocation, next_action, offsets);
						if (terrain[index] != LAND) continue;
						//if (hasExplorer[index]) continue;
						
						CoastFollowerData newMove = new CoastFollowerData(new Move(next_action),(getDirection(interestingPoints.get(j).getOffsetFromCurrent().x, interestingPoints.get(j).getOffsetFromCurrent().y)), offsets[index]);
						
						if (next_action == Util.oppositeDirection(action)) {
							newMove.setOpposite(true);
						}
						validMoves.add(newMove);
						continue;
					}
				}
			}
			//check diagonals around water
			for (int k = 0; k < offsets.length; k++) {
				if (terrain[k] == LAND) {
					if (isOrthogonal(interestingPoints.get(i).getOffset(),offsets[k])) {
						if (isDiagonal(offsets[k],currentLocation)) {
							int dx = -1 * currentLocation.x + offsets[k].x;
							int dy = -1 * currentLocation.y + offsets[k].y;
							
							if (action != -1)
								next_action = getDirection(dx, dy);
							else {
								action = getDirection(dx, dy);
								int def_index = Util.directionToIndex(currentLocation, action, offsets);
								if (terrain[def_index] != LAND) { action = -1; continue; }
								leaning_direction = getDirection(interestingPoints.get(i).getOffsetFromCurrent().x, interestingPoints.get(i).getOffsetFromCurrent().y);
								if (hasExplorer[def_index]) {
									CoastFollowerData firstMove = new CoastFollowerData(new Move(action),leaning_direction,offsets[def_index]);
									validMoves.add(firstMove);
									action = -1; 
									continue; 
								}
								return new Move(action);
							}
							
							
							
							int index = Util.directionToIndex(currentLocation, next_action, offsets);
							if (terrain[index] != LAND) continue;
							//if (hasExplorer[index]) continue;
							
							CoastFollowerData newMove = new CoastFollowerData(new Move(next_action),(getDirection(interestingPoints.get(i).getOffsetFromCurrent().x, interestingPoints.get(i).getOffsetFromCurrent().y)), offsets[index]);
							
							if (next_action == Util.oppositeDirection(action)) {
								newMove.setOpposite(true);
							}
							
							validMoves.add(newMove);
							continue;
						}
					}
				}
			}
		}
		
		//Go through all the valid moves and pick one
		if (validMoves.size() > 0) {
			//Randomly pick the first move if they're all bad
			if (action == -1) {
				Random random = new Random();
				int nextMove = random.nextInt(validMoves.size());
				action = validMoves.get(nextMove).getMove().getAction();
				leaning_direction = validMoves.get(nextMove).getLeaningDirection();
				return validMoves.get(nextMove).getMove();
			}
			
			//If there's only one, just pick it
			if (validMoves.size() == 1) {
				action = validMoves.get(0).getMove().getAction(); 
				return validMoves.get(0).getMove();
			}
			
			//If they're all the same, just pick it
			boolean equal = true;
			for (int i = 0; i < validMoves.size(); i++) {
				if (validMoves.get(0).getMove().getAction() != validMoves.get(i).getMove().getAction())
					equal = false;
			}
			if (equal) {
				action = validMoves.get(0).getMove().getAction();
				return new Move(action);
			}
			
			//Find the one closest to the current leaning direction.
			int closestIndex = -1;
			int closest = 20;
			for (int i = 0; i < validMoves.size(); i++) {
				if (validMoves.get(i).isOpposite()) continue;
				
				//Weigh by visiting. A cell that hasn't been visited is always preferred over one
				//that has been visited.
				//The cell closest to the leaning direction that has been visited is selected among
				//cells that have been visited, if that is all there is to choose from.
				if (hasVisited(validMoves.get(i).getTarget())) {
					if (getCloseness(validMoves.get(i).getMove()) + 5 + timesVisited(validMoves.get(i).getTarget()) < closest) {
						closestIndex = i;
						closest = getCloseness(validMoves.get(i).getMove()) + 5 + timesVisited(validMoves.get(i).getTarget());
					}
				}
				else {
					if (getCloseness(validMoves.get(i).getMove()) < closest) {
						closestIndex = i;
						closest = getCloseness(validMoves.get(i).getMove());
					}
				}
				
			}
			
			leaning_direction = validMoves.get(closestIndex).getLeaningDirection();
			
			pushCounter++;
			action = validMoves.get(closestIndex).getMove().getAction();
		
			
			if (hasVisited(validMoves.get(closestIndex).getTarget())) self_trap_breaker++;
			else self_trap_breaker = 0;
			
			return validMoves.get(closestIndex).getMove();
		}
	
		//Don't know what to do!!
		setInactive();
		return null;
	}
	
	private void addVisitedPoint(Point currentLocation) {
		if (cellsVisited.containsKey(currentLocation)) {
			Integer i = cellsVisited.get(currentLocation);
			cellsVisited.put(currentLocation, i + 1);
			assert(cellsVisited.get(currentLocation).equals(i + 1));
		} else {
			cellsVisited.put(currentLocation, 1);
		}
	}
	
	private boolean hasVisited(Point location) {
		if (cellsVisited.containsKey(location)) {
			return true;
		} 
		return false;
	}
	
	private int timesVisited(Point location) {
		if (cellsVisited.containsKey(location)) {
			return cellsVisited.get(location).intValue();
		} 
		else return 0;
	}

	private int getCloseness(Move move) throws Exception {
		if (leaning_direction == WEST) {
			switch (move.getAction()) {
			case WEST: return 0;
			case NORTHWEST: return 1;
			case SOUTHWEST: return 1;
			case NORTH: return 2;
			case SOUTH: return 2;
			case NORTHEAST: return 3;
			case SOUTHEAST: return 3;
			case EAST: return 4;
			}
		}
		if (leaning_direction == NORTHWEST) {
			switch (move.getAction()) {
			case WEST: return 1;
			case NORTHWEST: return 0;
			case SOUTHWEST: return 2;
			case NORTH: return 1;
			case SOUTH: return 3;
			case NORTHEAST: return 2;
			case SOUTHEAST: return 4;
			case EAST: return 3;
			}
		}
		if (leaning_direction == SOUTHWEST) {
			switch (move.getAction()) {
			case WEST: return 1;
			case NORTHWEST: return 2;
			case SOUTHWEST: return 0;
			case NORTH: return 3;
			case SOUTH: return 1;
			case NORTHEAST: return 4;
			case SOUTHEAST: return 2;
			case EAST: return 3;
			}
		}
		if (leaning_direction == SOUTH) {
			switch (move.getAction()) {
			case WEST: return 2;
			case NORTHWEST: return 3;
			case SOUTHWEST: return 1;
			case NORTH: return 4;
			case SOUTH: return 0;
			case NORTHEAST: return 3;
			case SOUTHEAST: return 1;
			case EAST: return 2;
			}
		}
		if (leaning_direction == NORTH) {
			switch (move.getAction()) {
			case WEST: return 2;
			case NORTHWEST: return 1;
			case SOUTHWEST: return 3;
			case NORTH: return 0;
			case SOUTH: return 4;
			case NORTHEAST: return 1;
			case SOUTHEAST: return 3;
			case EAST: return 2;
			}
		}
		if (leaning_direction == EAST) {
			switch (move.getAction()) {
			case WEST: return 4;
			case NORTHWEST: return 3;
			case SOUTHWEST: return 3;
			case NORTH: return 2;
			case SOUTH: return 2;
			case NORTHEAST: return 1;
			case SOUTHEAST: return 1;
			case EAST: return 0;
			}
		}
		if (leaning_direction == NORTHEAST) {
			switch (move.getAction()) {
			case WEST: return 3;
			case NORTHWEST: return 2;
			case SOUTHWEST: return 4;
			case NORTH: return 1;
			case SOUTH: return 3;
			case NORTHEAST: return 0;
			case SOUTHEAST: return 2;
			case EAST: return 1;
			}
		}
		if (leaning_direction == SOUTHEAST) {
			switch (move.getAction()) {
			case WEST: return 3;
			case NORTHWEST: return 4;
			case SOUTHWEST: return 2;
			case NORTH: return 3;
			case SOUTH: return 1;
			case NORTHEAST: return 2;
			case SOUTHEAST: return 0;
			case EAST: return 1;
			}
		}
		return 5;
	}

	/**
	 * Best used if points are distance 1 away
	 */
	public boolean isOrthogonal(Point a, Point b) {
		if (a.x == b.x) {
			if (a.y == b.y - 1 || a.y == b.y + 1) return true;
		}
		else if (a.y == b.y) {
			if (a.x == b.x - 1 || a.x == b.x + 1) return true;
		}
		return false;
	}
	
	/** 
	 * Best used if points are distance 1 diagonally away
	 */
	public boolean isDiagonal(Point a, Point b) {
		if (a.x == b.x - 1) {
			if (a.y == b.y - 1 || a.y == b.y + 1) return true;
		}
		else if (a.x == b.x + 1) {
			if (a.y == b.y - 1 || a.y == b.y + 1) return true;
		}
		else if (a.y == b.y - 1) {
			if (a.x == b.x - 1 || a.x == b.x + 1) return true;
		}
		else if (a.y == b.y + 1) {
			if (a.x == b.x - 1 || a.x == b.x + 1) return true;
		}
		return false;
	}

	//Assumes an adjacent square
	public int getDirection(int _dx, int _dy) {
		if (_dx == -1) {
			switch (_dy) {
			case -1: return NORTHWEST;
			case 0: return WEST;
			case 1: return SOUTHWEST;
			}
		}
		else if (_dx == 0) {
			switch(_dy) {
			case -1: return NORTH;
			case 0: return STAYPUT;
			case 1: return SOUTH;
			}
		}
		else if (_dx == 1) {
			switch(_dy) {
			case -1: return NORTHEAST;
			case 0: return EAST;
			case 1: return SOUTHEAST;
			}
		}
		assert(false); //Should never get here
		return -1;
	}

	public String getName() {
		return "coast following";
	}

	public void setActive(int time) {
		this.maxtime = time;
		this.currenttime = 0;
		action = -1;
		coastRunnerActive = true;
	}
	
	public boolean isCoastFollowingTime(int currentTime) {
		if (currenttime < maxtime) return true;
		return false;
	}
}
