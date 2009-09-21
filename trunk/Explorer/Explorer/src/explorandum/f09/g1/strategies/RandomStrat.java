package explorandum.f09.g1.strategies;

import java.awt.Point;
import java.util.Random;

import explorandum.f09.g1.Map;
import static explorandum.GameConstants.*;

public class RandomStrat extends Strategy{
	
	public RandomStrat() {
	}
	
	@Override
	public int getMove(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) {
		
		Random rand = new Random();
		int move = ACTIONS[rand.nextInt(ACTIONS.length)];
		System.out.println("move: " + move);
		return move;
	}

}
