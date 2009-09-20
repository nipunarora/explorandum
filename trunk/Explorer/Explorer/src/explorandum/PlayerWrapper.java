/* 
 * 	$Id: PlayerWrapper.java,v 1.2 2007/11/13 02:49:55 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;


import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public final class PlayerWrapper implements GameConstants{
	private Player player;
	private Logger log;
	private Logger playerLog;
	private Random rand;
	private Explorer explorer;
	private MoveWrapper move;
	private Cell start;
	private Boolean strikeout;
	
	public PlayerWrapper(GameConfig config, int id, int rounds, int explorers, int range, Cell startCell){
		Class<Player> playerClass = config.getActivePlayerList()[id];
		log = new Logger(config.getSimulatorLogLevel(), this.getClass());
		playerLog = new Logger(config.getPlayerLogLevel(), playerClass);
		rand = new Random(config.getSeed());
		move = new MoveWrapper(STAYPUT, 0);
		start = startCell;
		move.setMoved();
		strikeout = false;
		
		try {
			player = (Player)playerClass.newInstance();
			player.register(id, rounds, explorers, range, playerLog, rand);
			explorer = new Explorer(id, getName(), getColor());
		} catch (Exception e) {
			log.fatal("PANIC: Error intantiating player.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Color getColor(){
		try {
			return player.color();
		} catch (Exception e) {
			log.error("Player threw Exception in getColor():\n"+e);
			return Color.GRAY;
		}
	}
	
	public String getName(){
		try {
			return player.name();
		} catch (Exception e) {
			log.error("Player threw Exception in getName():\n"+e);
			return "Default Name";
		}
	}
	
	public MoveWrapper	nextMove(
			Point currentLocation,
			Point[] offsets,
			Boolean[] hasExplorer,
			Integer[][] visibleExplorers,
			Integer[] terrain,
			int time,
			Boolean StepStatus
			){
		//Move m;
		try {
			if(strikeout){//took too long, out of the game
				move = new MoveWrapper(STAYPUT, time);
				log.debug("STRIKEOUT: " + player.name());
			}else{

				Mover mover = new Mover(this, currentLocation, offsets,hasExplorer, visibleExplorers, terrain, time,StepStatus);
				mover.setName("Mover Thread");
				mover.setDaemon(true);
				mover.start();
				try {
					mover.join(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} //wait for 1 second
				if(mover.isAlive()){
					log.info("Player "+this.getName()+ " took too long to move!");
					strikeout=true;
					move = new MoveWrapper(STAYPUT, time);
				
					//mover.stop();
				}	
			}
		} catch (Exception e) {
			log.error("Player threw Exception in move(...):\n"+e);
			log.warn("Player threw Exception in move(...):\n"+e);
			//e.printStackTrace();
			move = new MoveWrapper(STAYPUT, time);
		}
		
		return move;
	}

	private void moveHelper(
			Point currentLocation,
			Point[] offsets,
			Boolean[] hasExplorer,
			Integer[][] visibleExplorers,
			Integer[] terrain,
			int time,
			Boolean StepStatus
			){
		Move m;
		try{
			m = player.move(currentLocation, offsets,hasExplorer, visibleExplorers, terrain, time,StepStatus);
			move = new MoveWrapper(m.getAction(), time);
		}catch (Exception e){
			log.error("Player threw Exception in move(...):\n"+e);
			log.warn("Player threw Exception in move(...):\n"+e);
			//e.printStackTrace();
			move = new MoveWrapper(STAYPUT, time);
		}
	}

	public Explorer getExplorer()
	{
		return explorer;
	}
	
	public MoveWrapper getLastMove()
	{
		return move;
	}

	public Cell getStart()
	{
		return start;
	}
	
	private class Mover extends Thread{
		Point currentLocation;
		Point[] offsets;
		Boolean[] hasExplorer;
		Integer[][] visibleExplorers;
		Integer[] terrain;
		Boolean StepStatus;
		int time;
		PlayerWrapper w;
		
		public Mover(PlayerWrapper p,
				Point currentLocation,
				Point[] offsets,
				Boolean[] hasExplorer,
				Integer[][] visibleExplorers,
				Integer[] terrain,
				int time,
				Boolean StepStatus
				){
			this.currentLocation = currentLocation;
			this.offsets = offsets;
			this.hasExplorer = hasExplorer;
			this.visibleExplorers = visibleExplorers;
			this.terrain = terrain;
			this.time = time;
			this.w = p;
			this.StepStatus = StepStatus;
		}
		public void run(){
			w.moveHelper(currentLocation, offsets,hasExplorer, visibleExplorers, terrain, time,StepStatus);
		}	
	}

	public Boolean getStrikeout()
	{
		return strikeout;
	}
	
	
}
