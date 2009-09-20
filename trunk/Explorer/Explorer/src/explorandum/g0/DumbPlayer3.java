/* 
 * 	$Id: DumbPlayer.java,v 1.3 2007/11/14 22:04:58 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum.g0;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class DumbPlayer3 implements Player
{
	Logger log;
	Random rand;
	ArrayList<Point> CellMemory;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand)
	{
		// TODO Auto-generated method stub
		log = _log;
		rand = _rand;
		CellMemory = new ArrayList<Point>();
		CellMemory.add(new Point(0,0));
	}

	public Color color() throws Exception
	{
		return Color.PINK;
	}

	//Picks a random adjacent cell that is not blocked
	//(Always picks a valid move)
	public Move move(Point currentLocation, Point[] offsets, Boolean[] hasExplorer, Integer[][] otherExplorers, Integer[] terrain, int time,Boolean StepStatus) throws Exception
	{

		//To build a list of valid locations
		ArrayList<Integer> valid = new ArrayList<Integer>();

		//Loop through adjacent cells
		for (int i = 0; i < _dx.length; i++)
			if (_dx[i] != 0 || _dy[i] != 0)
			{
				//Find this cell in visible cells list
				for (int j = 0; j < offsets.length; j++)
				{
					Point point = offsets[j];
					if(CellMemory.contains(point))
					{
						break;
						
					}
					
					//if adjacent cell and LAND
					if (point.x == currentLocation.x + _dx[i] && point.y == currentLocation.y + _dy[i] && terrain[j] == LAND)
					{
						//add to valid actions list
						//(i is the index of the action needed to move to this cell)
						valid.add(i);
						break;
					}
				}
			}

		// if no valid location, stay put
		if (valid.size() == 0)
			return new Move(STAYPUT);

		//pick a random value from valid actions
		
		int retnum = valid.get(rand.nextInt(valid.size()));
		Point tempPoint = new Point(currentLocation.x + _dx[retnum], currentLocation.y + _dy[retnum]);
		CellMemory.add(tempPoint);
		return new Move(retnum);
	}

	public String name() throws Exception
	{
		return "Dumb Player 3";
	}
}


