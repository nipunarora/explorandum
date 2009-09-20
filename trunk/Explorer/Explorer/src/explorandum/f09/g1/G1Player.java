package explorandum.f09.g1;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;


public class G1Player implements Player{

	
	public Map map;
	public int rounds;//I am assuming rounds to be given in some fashion but will figure out it later... using this as a temporary variable for calculation purposes...
	
	
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
	
	public int coasthugger(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus)
	{
		
		
		return 0;
	}
	
	public Nearest nearestwateroffset(Point[] offsets, Integer[] terrain)
	{
		Nearest temp= new Nearest();
		temp.isWater=false;
		temp.offsetValue=100;
		
		for(int i=0;i<offsets.length;i++)
		{
			if(terrain[i]==1)
			{
				temp.isWater=true;
				if(temp.offsetValue>offsetdist(offsets[i]))
					temp.offsetValue=i;
			}
			
		}
		return temp;
	}
	
	//Calculates distance
	public int offsetdist(Point p)
	{		
		double tempval= (p.x)^2 + (p.y)^2;
		tempval= Math.sqrt(tempval);
		return (int)Math.floor(tempval);
	}
	
	public double openness(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus)
	{
		
		ArrayList neighbors= new ArrayList();
		int d=2;//A Constant to look around the current cell for openess.... can be dependent on the actual d value sent by the player... we should probably consider a lower bound
		
		for(int i=0; i<offsets.length; i++)
		{
			double currentScore;
			int distance=0;
			
			//weights (these can be based on a time based strategy on how many rounds are left etc. for now keeping them constant taking an initial and unexplored board.
			
			
			distance = (int)offsets[i].distance(new Point(0,0)); //Should this be lower bound?
			
			Point location = new Point();//Cell for which score is being calculated
			location.x=currentLocation.x+ offsets[i].x;
			location.y=currentLocation.y + offsets[i].y;
			
			//check if has visited is required for calculating openness later on
			if(map.hasVisited(location))
			{
				
			}
			// using hasExplored to calculate openness
			if(map.hasExplored(location))
			{
				
			}
			else//unexplored territory
				//for each offset cell we check a distance 4 around it in each direction for the cells we know of if the cells already exist we assign lower value compared to cell being sorrounded by cells which are not already in the map. 
				//this approach should hopefully remove any chances of 
			{
				for (int x=-d; x<=d; x++)
				{
					for(int y=-d; y<=d; y++)
					{
						Point tempLocation= new Point();//temporary location currently being checked for the score.
						tempLocation.x=location.x + x;
						tempLocation.y=location.y +y;
						
						if(map.hasExplored(tempLocation))
						{
							//score calculating function for basic unexplored cells which has neighboring cells which have been explored
							
						}
						else
						{
							//score calculating function for basic explored cells which have 
							
						}
					}
				}
			}
				
		}
		return NULL;
		
		
		
		
	}

}
