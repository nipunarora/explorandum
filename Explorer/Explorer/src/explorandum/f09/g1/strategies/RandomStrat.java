package explorandum.f09.g1.strategies;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import explorandum.f09.g1.Cell;
import explorandum.f09.g1.Map;
import explorandum.f09.g1.Utilities;
import static explorandum.GameConstants.*;

public class RandomStrat extends Strategy{
	
	public RandomStrat(Map memory) {
		this.memory = memory;
	}
	
	@Override
	public int getMove(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) {
		
		ArrayList<Integer> validMoves = new ArrayList<Integer>();
		ArrayList<Integer> validNewMoves = new ArrayList<Integer>();
		
		for(Cell c : this.memory.getMapExplored().values()) {
			System.out.println(c);
		}
		
		for(int i = 0; i < offsets.length; i++) {
			Point currPoint = offsets[i];
			int distance = Utilities.euclidDistance(currentLocation, currPoint);
			if(distance == 1) {
				if(terrain[i] == 0) {
					int dir = Utilities.getHeading(currentLocation, currPoint);
					validMoves.add(dir);
					if(!memory.hasVisited(currPoint)) {
						validNewMoves.add(dir);
					}
				}
			}
		}
		
		int move;
		Random rand = new Random();			
		if(validNewMoves.size() == 0) {
			move = validMoves.get(rand.nextInt(validMoves.size()));
		} else {
			move = validNewMoves.get(rand.nextInt(validNewMoves.size()));
		}
		
		return move;
	}
}
