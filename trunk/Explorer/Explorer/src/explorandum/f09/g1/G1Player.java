package explorandum.f09.g1;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;


public class G1Player implements Player{

	private static final double NULL = 0;
	public Map map;
	
	
	@Override
	public Color color() throws Exception {
		
		return Color.cyan;
	}

	@Override
	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus) throws Exception {
		
		map.setMapExplored( currentLocation, offsets, hasExplorer, visibleExplorers, terrain, time, StepStatus);
		
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
	
	public void openness(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus){
		
		ArrayList visibleCells= new ArrayList();
		
		for(int i=0; i<offsets.length; i++)
		{
			int distance=0;
			distance = (int)offsets[i].distance(new Point(0,0)); //Should this be lower bound?
			Point location = new Point();
			location.x=currentLocation.x+ offsets[i].x;
			location.y=currentLocation.y + offsets[i].y;
			if(map.hasExplored(p))
		}
		
		
		
	}

}
