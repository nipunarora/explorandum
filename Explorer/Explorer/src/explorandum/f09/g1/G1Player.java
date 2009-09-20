package explorandum.f09.g1;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;


public class G1Player implements Player{

	public Map map;
	
	
	@Override
	public Color color() throws Exception {
		
		return Color.cyan;
	}

	@Override
	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) throws Exception {
		
		//map.setMapExplored( currentLocation, offsets, hasExplorer, visibleExplorers, terrain, time, StepStatus);
		Random rand = new Random();
		
		int action = ACTIONS[rand.nextInt(ACTIONS.length)];

		return new Move(action);
	}

	@Override
	public String name() throws Exception {
		
		return "Team1" ;
	}

	@Override
	public void register(int explorerID, int rounds, int explorers, int range,
			Logger log, Random rand) {
		
		
	}

}
