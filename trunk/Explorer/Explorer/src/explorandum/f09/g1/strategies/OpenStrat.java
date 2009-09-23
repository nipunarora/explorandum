package explorandum.f09.g1.strategies;

import java.awt.Point;
import java.util.Arrays;

import com.sun.tools.javac.code.Attribute.Array;

import explorandum.f09.g1.Cell;
import explorandum.f09.g1.Map;
import explorandum.f09.g1.Utilities;

public class OpenStrat extends Strategy {

	public OpenStrat(Map memory, Map view, int d) {
		this.memory = memory;
		this.range = d;
		this.view = view;
	}
	
	@Override
	public int getMove(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) {

		double[] scores = new double[offsets.length];
		Point currentNeig;
		
		Point bestNeighborPoint = new Point();
		double bestScore = 0;
		
		System.out.println("Step: " + time);
		
		//go through each neighbor
		for(int i = 0; i < offsets.length; i++) {
			currentNeig = offsets[i];
			
			
			//if the neighbor is not land then skip it
			if(terrain[i] != 0) {
				scores[i] = 0;
				continue;
			}
			
			//go through each neighbors neighbor
			for(int x = -this.range; x < this.range; x++) {
				for(int y = -this.range; y < this.range; y++) {
					double distanceMult, terrainScore, statusScore, prevSeenMult; 
					Point currPoint = new Point(currentNeig.x + x, currentNeig.y + y);
					double distance = Utilities.euclidDistance(currPoint, currentNeig);
					
					Cell currCell = Utilities.checkMemory(this.memory, this.view, currPoint);
					
					terrainScore = getTerrainScore(currCell);
					statusScore = getStatusScore(currCell);
					
					if(distance != 0) {
						distanceMult = (.8 + (.4/distance));
						prevSeenMult = (1.2 - (.4/distance));
					} else {
						distanceMult = 1.3;
						prevSeenMult = .7;
					}
					
					scores[i] += distanceMult * prevSeenMult * (terrainScore + statusScore);
				}
			}	
			
			if(scores[i] > bestScore) {
				bestNeighborPoint = offsets[i];
				bestScore = scores[i];
			}
			
			System.out.println(offsets[i] + " - score: " + scores[i] + " terrain: " + terrain[i]);
		}
		
//		for(int k = 0; k < scores.length; k++) {
//			System.out.println(offsets[k] + " - score: " + scores[k] + " terrain: " + terrain[k]);
//		}
		
		return Utilities.getHeading(currentLocation, bestNeighborPoint);
	}
	
	public double getTerrainScore(Cell c) {
		if(c == null) {
			return 15;
		} else if(c.getTerrain() == 0) {
			return 20;
		} else if(c.getTerrain() == 1) {
			return 30;
		} else {
			return 5;
		}
	}

	public double getStatusScore(Cell c) {
		if(c == null) {
			return 40;
		} else {
			return ((c.getDistance()/this.range) * 20);
		}
	}
}
