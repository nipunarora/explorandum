package explorandum.g6;

import java.awt.Point;
import java.util.Random;

import explorandum.GameConstants;
import explorandum.Move;

public class Timer extends Thread{

	private Point currentLocation;
	private Point[] offsets;
	private Boolean[] hasExplorer;
	private Integer[][] otherExplorers;
	private Integer[] terrain;
	private int time;
	private Move m;
	private G6Player ourPlayer;
	
	public Timer(G6Player ourPlayer, Random rand, Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time)
			{
		this.ourPlayer = ourPlayer;
		this.currentLocation = currentLocation; 
		this.offsets = offsets;
		this.hasExplorer = hasExplorer;
		this.otherExplorers = otherExplorers;
		this.terrain = terrain;
		this.time = time;
		setDaemon(true);
	}
	
	public Move getMove()
	{
		return m;
	}
	
	public void run()
	{
		try{
			m = ourPlayer.realMove(currentLocation, offsets, hasExplorer, otherExplorers, terrain, time);
		}
		catch(Throwable error){
			error.printStackTrace();
			m = Util.randomMove();
		}
	}
}
