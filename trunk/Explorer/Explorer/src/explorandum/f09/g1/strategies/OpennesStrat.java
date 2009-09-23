package explorandum.f09.g1.strategies;

import java.awt.Point;

import explorandum.GameConstants;
import explorandum.g4.Util;
import explorandum.f09.g1.Cell;
import explorandum.f09.g1.Map;
import explorandum.f09.g1.Utilities;

public class OpennesStrat extends Strategy{

	public OpennesStrat(Map memory, Map view, int d) {
		this.memory = memory;
		this.range = d;
		this.view = view;
	}
	
	@Override
	public int getMove(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) {
		
		double neighbor_scores[] = new double[9];
	    double direction_compensate[] = {1, 1.15, 1.3, 1, 1};

	    // Test each direction and find the best one.
	    int best_direction = 0;
	    double best_score = 0;
	    for (int i = 1; i <= 8; i++) {
	      Point neighbor_point = memory.getNeighborPoint(currentLocation, i);
	      // Measure the openness of nearby cells.
	      double openness = memory.getCellOpenness(neighbor_point, range);
//	      openness *= direction_compensate[
//	        Math.abs((previous_direction - i + 12) % 8 - 4)];
//	      if (i % 2 == 0)
//	        openness /= 2;

	      // Measure how good this direction is. It make the player keep turning
	      // right whenever possible.
	     // double direction_score = (i - (previous_direction + 3) + 16) % 8;

	      // The final score is the combination of the openness and the direction
	      // score.
	      neighbor_scores[i] = (int) (openness) ;
	     // Util.debugMessage("explore", "explore score for direction " + i + " is " + neighbor_scores[i]);
	      
	      if (neighbor_scores[i] > best_score) {
	        best_score = neighbor_scores[i];
	        best_direction = i;
	      }
	    }
		
		
		return best_direction;
	}
	
	

}
