package explorandum.g6;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import explorandum.Logger;
import explorandum.Move;
import explorandum.Player;

public class PlayerSpiral implements Player
{

	Logger log;
	Random rand;

	public void register(int explorerID, int rounds, int explorers, int range, Logger _log, Random _rand)
	{
		log = _log;
		rand = _rand;

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
		return "Spiral Player";
	}
}
