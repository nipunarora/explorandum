package explorandum.f09.g1.strategies;

import java.awt.Point;
import java.util.Arrays;


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

		System.out.println(Arrays.toString(offsets));
		System.out.println(Arrays.toString(terrain));


		double[] scores = new double[offsets.length];
		Point currentNeig;
		
		Point bestNeighborPoint = new Point();
		double bestScore = 0;
		
		//System.out.println("Step: " + time);
		
		//go through each neighbor
		for(int i = 0; i < offsets.length; i++) {
			currentNeig = offsets[i];
			
			// skip the cell you are on
			if(currentNeig.x==currentLocation.x && currentNeig.y==currentLocation.y){
				continue;
			}
			
			//if the neighbor is not land then skip it
			if(terrain[i] != 0) {
				scores[i] = 0;
				continue;
			}
			
			//go through each neighbors neighbor
			for(int x = -this.range; x <=this.range; x++) {
				for(int y = -this.range; y <= this.range; y++) {
					double distanceMult, terrainScore, statusScore, prevSeenMult; 
					Point currPoint = new Point(currentNeig.x + x, currentNeig.y + y);
					
					double distance = Utilities.euclidDistance(currPoint, currentNeig);
					
					Cell currCell = Utilities.checkMemory(this.memory, this.view, currPoint);
					if(currCell==null){
						System.out.print("null");
					}
					
					
					terrainScore = getTerrainScore(currCell);
					statusScore = getStatusScore(currCell);
					
					if(distance != 0) {
						distanceMult = (.8 + (.4/distance));
						prevSeenMult = (1.2 - (.4/distance));
					} else {
						distanceMult = 1.3;
						prevSeenMult = .7;
					}
					
					//(2*this.range- Utilities.euclidDistance(currPoint, currentLocation))*
					
					int weight;
					if (this.memory.hasExplored(currPoint))
						weight=1;
					else
						weight =3;
					
					scores[i] +=  distanceMult* prevSeenMult* (terrainScore + statusScore);
					System.out.println(currPoint.toString()+ "terrainScore:   " +terrainScore + "statusScore  " + statusScore);
				}
			}	
			
			if(scores[i] > bestScore) {
				bestNeighborPoint = offsets[i];
				bestScore = scores[i];
			}
			
			System.out.println(offsets[i] + " - ***************************score: " + scores[i] + " terrain: " + terrain[i]);
		}
		
//		for(int k = 0; k < scores.length; k++) {
//			System.out.println(offsets[k] + " - score: " + scores[k] + " terrain: " + terrain[k]);
//		}
		System.out.println("currentLocation:" + currentLocation.toString() +"   , best neighbor" + bestNeighborPoint.toString() + "  current heading:" + Utilities.getHeading(currentLocation, bestNeighborPoint));
		return Utilities.getHeading(currentLocation, bestNeighborPoint);
	}
	
	public double getTerrainScore(Cell c) {
		if(c == null) {
			return 15;
		} else if(c.getTerrain() == 0) {
			return 20;
		} else if(c.getTerrain() == 1) {
			return 70;
		} else {
			return 5;
		}
	}

	public double getStatusScore(Cell c) {
		if(c == null) {
			return 70;
		} else {
			double StatusScore=(c.getDistance()/this.range) * 25;
			//System.out.println("Current Point:" + c.toString() +"   Status Score:" +StatusScore);
			return (StatusScore);
		}
	}
}
