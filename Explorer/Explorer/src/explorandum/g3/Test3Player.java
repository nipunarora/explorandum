/* 
 * 	$Id: DumbPlayer.java,v 1.3 2007/11/14 22:04:58 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */

package explorandum.g3;
import java.awt.Color;
import java.awt.Point; //import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

//import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class Test3Player implements Player {
	Logger log;
	Random rand;
	static int lastmove; // last valid move
	//Hashtable<Point,Boolean> walked;
	HashMap<Point,Boolean> walked;
	static Map world;

	int rounds;
	int explorers;
	int range;
	int ID;
	Integer[] TerAndExp;
	static boolean following;
	static int offsetx,	offsety;
	static int currentround; 
	
	public void register(int explorerID, int rounds, int explorers, int range,
			Logger _log, Random _rand) {
		log = _log;
		rand = _rand;

		ID = explorerID;
		this.rounds = rounds;
		this.explorers = explorers;
		this.range = range;

		lastmove = STAYPUT;
		walked = new HashMap<Point,Boolean>();
		world = new Map(ID);
		currentround = 1;
	}

	public Color color() throws Exception {
		return Color.ORANGE;
	}

	public Move move(Point cur, Point[] offsets, Boolean[] hasExplorer,
			Integer[][] otherExplorers, Integer[] terrain, int time,Boolean StepStatus)
	throws Exception {
		
		//update data structures
		world.updateMap(cur, offsets, hasExplorer, otherExplorers, terrain);
		walked.put(cur, true);
		PopulatePlayerSits(otherExplorers, terrain);

		// build a list of valid actions
		ArrayList<Integer> valid = getValid(cur);
		ArrayList<Integer> preferred = getPreferred(cur, valid);
		preferred = prunePref(cur, preferred); // take out walked on cells
		
		// if no valid location, stay put
		// (Just a sanity check. Would never occur on a contiguous board)
		if (valid.size() == 0) {
			lastmove = STAYPUT;
			return new Move(STAYPUT);
		}

		int nextmove = lastmove;
		Point pnext = new Point(cur.x+_dx[nextmove],cur.y+_dy[nextmove]);
		
		// If we can see another player
		if (OtherPlayerVisible(offsets)) {
			// If he is moving in the same direction and in front of us
			if (OtherPlayerSameDirection(cur, lastmove, offsets)) 
				following = true;
			else
				following = false;
		} else 
			following = false;
		
		// If a player is detected in front of us, 
		// we mark the area around him as UNCLAIMABLE_LAND and wrap around it
		if (following)
		{
			int bx = Map.basicX;
			int by = Map.basicY;
			for(int i=0;i<preferred.size(); i++)
			{
				int px = cur.x + _dx[preferred.get(i)];
				int py = cur.y + _dy[preferred.get(i)];
				Map.map[px + bx][py + by] = Map.UNCLAIMABLE_LAND;
			}
			valid.clear();
			valid = getFollowingValid(cur);
			preferred.clear();
			preferred = getPreferred(cur, valid);
			preferred = prunePref(cur, preferred);
		}
		
		// if last direction is still valid OR
		// if the next move results in us going on an already traversed cell
		// if a preferred location exists (i.e. close to mountains or water) OR
		// if we are following a player
		if ( !valid.contains(nextmove) || walked.containsKey(pnext) ||
			 (!preferred.contains(nextmove) && preferred.size() > 0) || following) {
			
			// then time to pick a new direction
			// two cases: 1) no new moves to make 2) there are new moves to make
			
			ArrayList<Integer> unwalked = getUnwalked(cur,valid);
			if (unwalked.size()==0) { // Case 1:
				// get a direction on path to unseen area,
				// or unwalked area from map
				try
				{
					ArrayList<Point> path = world.findNearestArea(cur);
					int n = -1;
					if (path != null) {
						n = directionOf(cur, path.get(0));
					}
					
					if(n != -1)
						nextmove = n;
					else
						throw new Exception();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					if (valid.contains(lastmove)) {
						nextmove = lastmove;
					} else {
						nextmove = valid.get(rand.nextInt(valid.size()));
					}
				}
			} 
			else 
			{ // Case 2: there are new moves to make
				if (preferred.size() > 0) {
					nextmove = preferred.get(rand.nextInt(preferred.size()));
				} else {
					nextmove = unwalked.get(rand.nextInt(unwalked.size()));
				}
			}
		} 
		
		lastmove = nextmove;
		return new Move(nextmove);
	}

	private void PopulatePlayerSits(Integer[][] otherExplorers,
			Integer[] terrain) {
		TerAndExp = new Integer[500];
		for (int i = 0; i < terrain.length; i++)
			TerAndExp[i] = terrain[i];

		for (int i = 0; i < otherExplorers.length; i++)
			if (otherExplorers[i][0] != null)
				if (otherExplorers[i][0] == ID)
					TerAndExp[i] = 4;
				else
					TerAndExp[i] = 5;
	}
	
	// Checks if there is another player in our range of vision, d
	private boolean OtherPlayerVisible(Point[] offsets) {
		boolean ret = false;
		for (int i = 0; i < offsets.length; i++) {
			if (TerAndExp[i] == 5) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	// Checks if another player is moving in the same direction and in front of us
	private boolean OtherPlayerSameDirection(Point cur, int lastmove,
			Point[] offsets) {

		// Logic determined using position diagrams
		Point otherplayer = null;
		int odx = 0, ody = 0;

		for (int i = 0; i < offsets.length; i++) {
			if (TerAndExp[i] == 5) {
				otherplayer = offsets[i];
				break;
			}
		}
		
		if (otherplayer != null) {
			ArrayList<Integer> orthActs = new ArrayList<Integer>();
			orthActs.add(NORTH);
			orthActs.add(SOUTH);
			orthActs.add(EAST);
			orthActs.add(WEST);
			ArrayList<Integer> p = getPreferred(otherplayer, orthActs);
			if(p.size() > 0)
			{			
			odx = cur.x - otherplayer.x;
			ody = cur.y - otherplayer.y;

			if(((lastmove == SOUTH || lastmove == SOUTHEAST || lastmove == SOUTHWEST) && ody <= 0) ||
					((lastmove == NORTH || lastmove == NORTHEAST || lastmove == NORTHWEST) && ody >= 0) || 
					((lastmove == EAST || lastmove == SOUTHEAST || lastmove == NORTHEAST) && odx <= 0) || 
					((lastmove == WEST || lastmove == SOUTHWEST || lastmove == NORTHWEST) && odx >= 0))
				return true;
			else if(((odx == -1 || odx == 1) && ody == 0) || 
			((ody == -1 || ody == 1) && odx == 0) || (odx == 0 && ody == 0))
				return true;
			else
				return false;
			}
			else
				return false;
			
		} else
			return false;			
	}

	// Given 2 points it will give the direction required 
	// to reach one adjacent cell from another
	public int directionOf(Point cur, Point dest) {
		int dx = dest.x - cur.x;
		int dy = dest.y - cur.y;
		for (int a : ACTIONS) {
			if (dx == _dx[a] && dy == _dy[a]) 
				return a;
		}
		return -1;
	}
	
	// given integer weights, normalize to real weights that sum to 1.
	public ArrayList<Double> normalizeArray(ArrayList<Integer> weights) {
		double sum = 0;
		for (Integer w : weights) {
			sum += w;
		}
		ArrayList<Double> rv = new ArrayList<Double>();
		for (Integer w : weights) {
			rv.add(w/sum);
		}
		return rv;
	}
	
	//give me normalized weights i will return a weighted random one.
	public int randomize(ArrayList<Double> weights, ArrayList<Integer> actions) {
		double r = rand.nextDouble();
		double cum = 0;
		for (int i=0; i<weights.size(); i++) {
			cum += weights.get(i);
			if (r<cum) {
				return actions.get(i);
			}
		}
		return -1;
	}
	
	// assign weights to each of ACT moves.
	public ArrayList<Double> weightAct(Point cur, ArrayList<Integer> act) {
		ArrayList<Integer> weight = new ArrayList<Integer>();
		for (int a : act) {
			int wt = 1;
			Point p = new Point(cur.x+_dx[a],cur.y+_dy[a]);
			if (world.adjacentFeature(p, WATER)) wt += 8;
			if (world.adjacentFeature(p, MOUNTAIN)) wt += 6;
			if (!walked.containsKey(p)) wt += 5;
			if (a == lastmove) wt += 2;
			weight.add(wt);
		}
		return normalizeArray(weight);
	}
	
	// returns adjacent non-traversed cells 
	public ArrayList<Integer> getUnwalked(Point cur, ArrayList<Integer> act) {
		ArrayList<Integer> unwalked = new ArrayList<Integer> ();
		for (int a : act) {
			if (!walked.containsKey(new Point(cur.x+_dx[a],cur.y+_dy[a]))) {
				unwalked.add(a);
			}
		}
		return unwalked;
	}

	//	in case we want to change our definition of preferred.
	public ArrayList<Integer> getPreferred(Point cur, ArrayList<Integer> val) {
		return getOrthWaterOrMountain(cur,val);
	}

	//	of valid, which ones put us on a cell that is orthogonally
	//	adjacent to mountains or water
	public ArrayList<Integer> getOrthWaterOrMountain(Point cur, ArrayList<Integer> val) {
		ArrayList<Integer> pref = new ArrayList<Integer>();
		int[] orthActs = {NORTH,EAST,SOUTH,WEST};

		for (int i=0; i< val.size(); i++) { 
			// coord corresponding to act=val[i]
			Point act = new Point(cur.x+_dx[val.get(i)],cur.y+_dy[val.get(i)]);
			// for each orthogonal action
			for (int dir : orthActs) {
				// value of cell in DIR from ACT;
				int cell = world.getCell(act.x+_dx[dir],act.y+_dy[dir]); 
				if (cell == WATER || cell == MOUNTAIN || cell == Map.UNCLAIMABLE_LAND) {
					pref.add(val.get(i));
				}
			}
		}
		return pref;
	}

	//	remove any acts that put us on squares we have already walked on
	public ArrayList<Integer> prunePref(Point cur, ArrayList<Integer> pref) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for (int i = 0; i < pref.size(); i++) {
			Point p = new Point(cur.x+_dx[pref.get(i)], cur.y+_dy[pref.get(i)]);
			if (!walked.containsKey(p)) {
				arr.add(pref.get(i));
			}
		}
		return arr;
	}

	// checks if we've already traversed a cell
	public boolean traversed(Point p) {
		return ( world.getCell(p) == Map.OUR_LAND );
	}

	//	return a list of all valid actions (excluding stayput,
	//	but inclusive of cells we have already traversed)
	public ArrayList<Integer> getValid(Point cur) {
		ArrayList<Integer> val = new ArrayList<Integer>();
		// skip i=0 == STAYPUT
		for(int i=1; i< ACTIONS.length; i++) {
			int cell = world.getCell(cur.x+_dx[i],cur.y+_dy[i]);
			if (cell != WATER && cell != MOUNTAIN) {
				val.add(i);
			}
		}
		return val;
	}
	
	//	return a list of all valid actions (excluding stayput and UNCLAIMABLE_LAND,	
	//	but inclusive of cells we have already traversed)
	public ArrayList<Integer> getFollowingValid(Point cur) {
		ArrayList<Integer> val = new ArrayList<Integer>();
		for(int i=1; i< ACTIONS.length; i++) {
			int cell = world.getCell(cur.x+_dx[i],cur.y+_dy[i]);
			if (cell != WATER && cell != MOUNTAIN && cell != Map.UNCLAIMABLE_LAND) {
				val.add(i);
			}
		}
		return val;
	}

	//	equality counts as adjacency here
	public boolean adjacent(Point p1, Point p2) {
		return ((p1.x == p2.x || p1.x == p2.x + 1 || p1.x == p2.x - 1) && (p1.y == p2.y
				|| p1.y == p2.y + 1 || p1.y == p2.y - 1));
	}

	//	get subset of valid directions that are near water.
	public ArrayList<Integer> getNearWater(ArrayList<Integer> val, Point cur) {
		ArrayList<Integer> pref = new ArrayList<Integer>();
		for (int i = 0; i < val.size(); i++) {
			int act = val.get(i);
			if (world.adjacentFeature(new Point(cur.x + _dx[act], cur.y
					+ _dy[act]), WATER)) {
				pref.add(act);
			}
		}
		return pref;
	}

	//	Return true if two points are diagonally adjacent to one another
	public boolean diagonal(Point cur, Point offset) {
		int dx = Math.abs(cur.x-offset.x);
		int dy = Math.abs(cur.y-offset.y);
		return dx == 1 && dy == 1;
	}

	//	Return true if two points are next to one another but not diagonal
	//	formerly: nextto(p1, p2)
	public boolean orthogonal(Point p1, Point p2) {
		int dx = Math.abs(p1.x-p2.x);
		int dy = Math.abs(p1.y-p2.y);
		return ( adjacent(p1,p2)  && 
				!( (dx == 1 && dy == 1)  || (dx == 0 && dy == 0) ));
	}

	public String name() throws Exception {
		return "CIA Ladies";
	}
}
