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

public class DumbPlayer1 implements Player
{
	ArrayList<Point> CellMemory;
	Logger log;
	Random rand;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand)
	{
		log = _log;
		rand = _rand;
		CellMemory = new ArrayList<Point>();

		log.debug("\nRounds:" + rounds + "\nExplorers:" + explorers + "\nRange:" + range);
	}

	public Color color() throws Exception
	{
		return Color.MAGENTA;
	}

	public Move move(Point currentLocation, Point[] offsets, Boolean[] hasExplorer, Integer[][] otherExplorers, Integer[] terrain, int time,Boolean StepStatus) throws Exception
	{

		int action = ACTIONS[rand.nextInt(ACTIONS.length)];

		return new Move(action);
	}

	public String name() throws Exception
	{
		return "Dumb Player 1";
	}
}
