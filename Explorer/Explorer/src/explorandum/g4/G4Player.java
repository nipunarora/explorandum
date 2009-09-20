/* 
 * 	$Id: DumbPlayer.java,v 1.3 2007/11/14 22:04:58 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum.g4;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;
import explorandum.GameConstants;
import java.util.*;

public class G4Player implements Player {

	Logger log;
	Random rand;
	Map map;
  int range;
  int num_explorers;
  int num_rounds;
	int previous_direction = 1;
  OpenShortestPath sp;
  Point current_location;
  Point loc1 = new Point(5,3);
  Point loc2 = new Point(2,2);
  Point loc3 = new Point(4,6);
  int d_init = 6;
  int dcounter;
  int explorerId;

	public void register(int _explorerID, int rounds, int explorers, int _range,
			Logger _log, Random _rand) {
		explorerId = _explorerID;
		log = _log;
		rand = _rand;
		map = new MapInHash();
    range = _range;
    num_explorers = explorers;
    num_rounds = rounds;
    sp = new OpenShortestPath(map, range);
    map.setNumRound(num_rounds);

		log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers
				+ "\nRange:" + range);
	}

	public Color color() throws Exception {
		return Color.YELLOW;
	}

  // This function implements the boundary tracing algorithm. It tries all 8
  // directions and find the one that have the large openness value. Openness
  // is defined as:
  //   - Near to many unexplored cells.
  //   - Near to many unexplored water.
  //   - NOT near mountains.
  //   - NOT near explored cells.
  // This function returns -1 if all nearby cells are visited.
  public int explore(int minimum_score) {
    double neighbor_scores[] = new double[9];
    double direction_compensate[] = {1, 1.15, 1.3, 1, 1};

    // Test each direction and find the best one.
    int best_direction = 0;
    double best_score = 0;
    for (int i = 1; i <= 8; i++) {
      Point neighbor_point = map.getNeighborPoint(current_location, i);
      // Measure the openness of nearby cells.
      double openness = map.getCellOpenness(neighbor_point, range);
      openness *= direction_compensate[
        Math.abs((previous_direction - i + 12) % 8 - 4)];
      if (i % 2 == 0)
        openness /= 2;

      // Measure how good this direction is. It make the player keep turning
      // right whenever possible.
      double direction_score = (i - (previous_direction + 3) + 16) % 8;

      // The final score is the combination of the openness and the direction
      // score.
      neighbor_scores[i] = ((int) (openness)) * 10 + direction_score;
      Util.debugMessage("explore", "explore score for direction " + i + " is " + neighbor_scores[i]);
      if (neighbor_scores[i] > best_score) {
        best_score = neighbor_scores[i];
        best_direction = i;
      }
    }

    Util.debugMessage("explore", "explore chooses " + best_direction + ", score is " + best_score);
    if (best_score >= minimum_score) {
      previous_direction = best_direction;
      return best_direction;
    } else {
      return -1;
    }
  }

  boolean sameCellStart(Point[] offsets, Integer[][] otherExplorers){
	  for(int i=0; i<offsets.length; i++){
		  Point p = offsets[i];
		  if(p.x == 0 && p.y==0){
			  if(otherExplorers[i].length == num_explorers)
				  return true;
		  }
	  }
	  return false;
	  
  }
  
  
	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time,Boolean StepStatus) {
    
		//System.out.println("Curr = " + Integer.toString(currentLocation.x) +" "+  Integer.toString(currentLocation.y));
		for(int myloop=0;myloop<offsets.length;myloop++)
		{
			//System.out.println("Point (" + myloop + ") = " + offsets[myloop].x + "," + offsets[myloop].y + " T:" + terrain[myloop] + " SS :" + StepStatus + " Has E:" + hasExplorer[myloop]);
		}
		//System.out.print("Boolean:");
//		for(int myloop=0;myloop<hasExplorer.length;myloop++)
//		{
//			System.out.println(myloop + ") = " + hasExplorer[myloop]);
//		}
//		System.out.print("Visible exp:");
//		for(int myloop=0;myloop<otherExplorers.length;myloop++)
//		{
//			for(int myloop2=0;myloop2<otherExplorers[myloop].length;myloop2++)
//			{
//					System.out.println("[" + myloop + "," + myloop2 +  "] = " + otherExplorers[myloop][myloop2]);
//			}
//		}
		
		
		try {
    	
     	if(time < 2){
     		boolean sameStart = sameCellStart(offsets, otherExplorers);
    		//System.out.println(sameStart);
        	if(sameStart){
        		return(new Move(0));
        	}
    	}
    	
      Util.debugMessage("move", "move at time " + time);
      current_location = currentLocation;
      loc3 = loc2;
      loc2 = loc1;
      loc1 = currentLocation;
      if(loc3.x == loc1.x && loc3.y == loc1.y)
      {
        Util.debugMessage("move", "deadlock!");
      	dcounter = d_init;
      	d_init += 6;
      }
      map.setTime(time);
      map.add(explorerId, currentLocation, offsets, hasExplorer, otherExplorers, terrain,
          time, range);

      // === Game stage 1 - Explore (prefer water and open spaces) ===
      if ((((double) time) / num_rounds < 0.65) && dcounter==0) {
        // Call explore function for the boundary tracing algorithm.
        int direction = explore(60);
        if (direction >= 0)
          return new Move(ACTIONS[direction]);
        else {  // explore function get stuck
          Util.debugMessage("move", "local score too low");
          int direction_to_target = getDirectionToOpenSpace();
          if (direction_to_target > 0)
            return new Move(ACTIONS[direction_to_target]);
        }
      // === Game stage 2 - Go to null cells ===
      } else if ((((double) time) / num_rounds < 0.75) || dcounter>0) {
        dcounter--;
    	  int direction_to_target = getDirectionToOpenSpace();
        if (direction_to_target > 0)
          return new Move(ACTIONS[direction_to_target]);
      // === Game stage 3 - Explore (prefer local area) ===
      } else {
        // Call explore function for the boundary tracing algorithm.
        int direction = explore(10);
        if (direction >= 0)
          return new Move(ACTIONS[direction]);
        else {  // explore function get stuck
          int direction_to_target = getDirectionToOpenSpace();
          if (direction_to_target > 0)
            return new Move(ACTIONS[direction_to_target]);
        }
      }

      // Use random direction if all above approaches don't work.
      Util.debugMessage("move", "use random move");
      int random_direction = 0;
      int count = 0;
      while(true) {
    	count++;
    	if (count > 1000)
    		break;
    	random_direction = rand.nextInt(7) + 1;  
    	Cell next = map.getCell(
    			currentLocation.x + GameConstants._dx[random_direction],
    			currentLocation.y + GameConstants._dy[random_direction]);
    	if (next == null || next.terrain == 0)
    		break;
      }
      
      return new Move(ACTIONS[random_direction]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
	}

  public int getDirectionToOpenSpace() {
    sp.updateShortestPath(current_location);
    
    // Find null targets
    ArrayList<Cell> candidate_targets = endGameTargets();
    for (Cell target_cell : candidate_targets) {
      Util.debugMessage("move", "try target at " + target_cell.location);
      ArrayList<Cell> path =
        sp.getShortestDistancePath(target_cell.location);
      if (path != null && path.size() > 0) {
        Util.debugMessage("move", "go to target at " + target_cell.location);
        Point next = path.get(0).location;
        int direction_to_target = map.getDirection(current_location, next);
        return direction_to_target;
      }
    }

    // Find unstepped cell targets
    candidate_targets = findUnsteppedCellTarget();
    for (Cell target_cell : candidate_targets) {
      Util.debugMessage("move", "try unstepped target at " + target_cell.location);
      ArrayList<Cell> path =
        sp.getShortestDistancePath(target_cell.location);
      if (path != null && path.size() > 0) {
        Util.debugMessage("move", "go to unstepped target at " + target_cell.location);
        Point next = path.get(0).location;
        int direction_to_target = map.getDirection(current_location, next);
        return direction_to_target;
      }
    }

    return 0;
  }

  public ArrayList<Cell> findUnsteppedCellTarget() {
    ArrayList<Cell> targets = new ArrayList<Cell>();
    Cell best_cell = null;
    double min_distance = Double.MAX_VALUE;
    for (Point p : map.getAllDiscoveredCoordinates()) {
      Cell c = map.getCell(p);
      if (c.claimed_distance > 0.5 && sp.getShortestDistance(p) < min_distance) {
        best_cell = c;
        min_distance = sp.getShortestDistance(p);
      }
    }
    if (best_cell != null)
      targets.add(best_cell);
    return targets;
  }

	public ArrayList<Cell> endGameTargets()
	{
		ArrayList<Cell> clist = new ArrayList<Cell>();
		Set<Point> sps = map.getAllDiscoveredCoordinates();
		//sp.updateShortestPath(current_location);
		
		// Let us iterator through all the discovered cells till now.
		if(sps!=null)
		{
			
			
			Iterator<Point> ip = sps.iterator();
			while(ip.hasNext())
			{
				Point p1 = ip.next();
				Cell ctest = map.getCell(p1);
				if(ctest.terrain == 0 && ctest.claimed_distance>0.5) // it has to be a un-stepped land cell
				{
				int totalnulls = 0;
				Set<Integer> ud = ctest.unexplored_directions; // how many un-explored regions
				
				
				Iterator iu = ud.iterator();
				while(iu.hasNext())
				{
					Integer i = (Integer)iu.next();
					switch(i)
					{
					case 1: {totalnulls += nullcells(ctest,1);break;}
					case 2: {totalnulls += nullcells(ctest,1);break;}
					case 3: {totalnulls += nullcells(ctest,2);break;}
					case 4: {totalnulls += nullcells(ctest,2);break;}
				
					case 5: {totalnulls += nullcells(ctest,3);break;}
					case 6: {totalnulls += nullcells(ctest,3);break;}
					case 7: {totalnulls += nullcells(ctest,4);break;}
					case 8: {totalnulls += nullcells(ctest,4);break;}
					
					}
					
					
				}
				ctest.nullvalue = totalnulls - (int)(sp.getShortestDistance(ctest.location)*0.5);
        clist.add(ctest);
				
				
				}// if ends
				
			}
		}

		
		
		
		Collections.sort(clist,new nullcompare());	
		// Let us print the first 5 values: 
		Util.debugMessage("null target", "\n N candidates are: ");
		for(int loop=0;loop<clist.size();loop++)
		{
			Util.debugMessage("null target", "\n C no (" + loop + ") = (" + clist.get(loop).location.x + "," + clist.get(loop).location.y + ") >> " + clist.get(loop).nullvalue);
		}
		
		return clist;
		
	}// my function ends
	
	
	public int nullcells(Cell c,int dirs)
	{
// 		This function return the number of cells in the direction mentioned
//		1 = up right
//		2 = down right
//		3 = down left
//		4 = up left
//		
// 		Current window is of size 3 x 5
		if(dirs == 1)
		{
			int nullcount=0;
			// x
			for(int loop1=0;loop1<4;loop1++)
			{
				//y
				for(int loop2=1;loop2<5;loop2++)
				{
					int xcor = c.location.x + loop1;
					int ycor = c.location.y - loop2;
					
					//if cells are padded, i dont know if this is imp
					//if(xcor<=map.getmin().x && xcor>=map.getmax().x && ycor<=map.getmin().y && ycor>=map.getmax().y)
					{
						Cell ctest = map.getCell(xcor, ycor);
						if(ctest == null)
						{
							nullcount++;
						}
					}
					
				}
			}
			
			return nullcount;
		}// case 1 ends
		
		if(dirs == 2)
		{
			int nullcount=0;
			// x
			for(int loop1=1;loop1<5;loop1++)
			{
				//y
				for(int loop2=0;loop2<4;loop2++)
				{
					int xcor = c.location.x + loop1;
					int ycor = c.location.y + loop2;
					//if(xcor<=map.getmin().x && xcor>=map.getmax().x && ycor<=map.getmin().y && ycor>=map.getmax().y)
					{
						Cell ctest = map.getCell(xcor, ycor);
						if(ctest == null)
						{
							nullcount++;
						}
					}
					
				}
			}
			
			return nullcount;
		}// case 2 ends
		
		if(dirs == 3)
		{
			int nullcount=0;
			// x
			for(int loop1=0;loop1<4;loop1++)
			{
				//y
				for(int loop2=1;loop2<5;loop2++)
				{
					int xcor = c.location.x - loop1;
					int ycor = c.location.y + loop2;
					//if(xcor<=map.getmin().x && xcor>=map.getmax().x && ycor<=map.getmin().y && ycor>=map.getmax().y)
					{
						Cell ctest = map.getCell(xcor, ycor);
						if(ctest == null)
						{
							nullcount++;
						}
					}
					
				}
			}
			
			return nullcount;
		}// case 3 ends
		
		if(dirs == 4)
		{
			int nullcount=0;
			// x
			for(int loop1=1;loop1<5;loop1++)
			{
				//y
				for(int loop2=0;loop2<4;loop2++)
				{
					int xcor = c.location.x - loop1;
					int ycor = c.location.y - loop2;
					//if(xcor<=map.getmin().x && xcor>=map.getmax().x && ycor<=map.getmin().y && ycor>=map.getmax().y)
					{
						Cell ctest = map.getCell(xcor, ycor);
						if(ctest == null)
						{
							nullcount++;
						}
					}
					
				}
			}
			
			return nullcount;
		}// case 1 ends
		
		
		return 1;
	}
	
	public String name() throws Exception {
		return "Syn Diego";
	}
}
