package explorandum.f09.g1.strategies;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

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
			
		int j = 0;
		for(int i = 0; i < offsets.length; i++) {
			Point currPoint = offsets[i];
			int distance = Utilities.euclidDistance(currentLocation, currPoint);
			//System.out.println("p1: " + currentLocation + ", p2: " + currPoint);
			if(distance == 1) {
				if(terrain[i] == 0) {
					validMoves.add(i);
					j++;
				}
			}
		}
		
		
		Random rand = new Random();
		int move = validMoves.get(rand.nextInt(validMoves.size()));
		System.out.println("j = " + j + " move: " + move);
		return move;
	}
}
