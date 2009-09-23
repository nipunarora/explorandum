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

public class DumbPlayer2 implements Player
{
	ArrayList<Point> CellMemory;
	Logger log;
	Random rand;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand)
	{
		log = _log;
		rand = _rand;
		CellMemory = new ArrayList<Point>();
		CellMemory.add(new Point(0,0));
	}

	public Color color() throws Exception
	{
		return new Color(160, 0, 160);
	}

	//Picks a valid cell in visible range, tries to head towards this cell 
	//(if the adjacent cell is blocked this player doesn't re-think)
	public Move move(Point currentLocation, Point[] offsets, Boolean[] hasExplorer, Integer[][] otherExplorers, Integer[] terrain, int time,Boolean StepStatus) throws Exception
	{
		
		for(Point p : offsets) {
			p.y = -p.y;
		}

		//Random output to understand move function
		
		if(StepStatus==false)
		{
			System.out.println("no error");
		}
		//System.out.println(" Current Location" + currentLocation.x + " , " + currentLocation.y );

		/*
		
		System.out.println("time: " + time);
		if(offsets.length==terrain.length)
		{
			System.out.println(" The offset length is " + offsets.length);
			System.out.print("Offsets Length verified");
		}
		
				*/
		for (int j=0; j<offsets.length; j++)
		{
			//System.out.print(" : ");
			System.out.print(offsets[j].x + " , " + offsets[j].y + " - ");
			System.out.println(terrain[j]);

			//System.out.print(hasExplorer[j] + " , " + hasExplorer[j] );
		}

		// Build a list of valid locations
		ArrayList<Point> valid = new ArrayList<Point>();
		for (int i = 0; i < offsets.length; i++)
		{
			Point point = offsets[i];
			if(CellMemory.contains(point))
			{
				continue;
				
			}
			if (point.x == 0 && point.y == 0)
				continue;

			if (terrain[i] == LAND)
				valid.add(point);
		}

		// if no valid location, stay put
		if (valid.size() == 0)
			return new Move(STAYPUT);

		// Pick a valid location
		Point goTo = offsets[rand.nextInt(valid.size())];
		CellMemory.add(goTo);

		// Try to round it down to one of the adjacent cells
		if(goTo.x!=0)
		goTo.x /= Math.abs(goTo.x);
		if(goTo.y!=0)
		goTo.y /= Math.abs(goTo.y);

		// interpret the action to go to this location
		int action = 0;
		for (int i = 0; i < _dx.length; i++)
			if (_dx[i] == goTo.x && _dy[i] == goTo.y)
			{
				action = i;
				break;
			}

		return new Move(action);
	}

	public String name() throws Exception
	{
		return "Dumb Player 2";
	}
}
