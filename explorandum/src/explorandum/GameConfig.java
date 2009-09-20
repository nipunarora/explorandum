/* 
 * 	$Id: GameConfig.java,v 1.4 2007/11/28 16:30:47 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import explorandum.Logger;

/**
 * This file provides an interface to the game configuration parameters.
 * Facilities for loading and saving config parameters as well as changing them
 * at runtime are provided.
 * @author John Cieslewicz
 *
 */
public final class GameConfig implements GameConstants{
	private static Logger.LogLevel ConfigLogLevel = Logger.LogLevel.INFO;
	
	private ArrayList<Class<Player>> availablePlayers;
	private ArrayList<File> availableBoards;
	private Class<Player> playerClass;
	private File boardFile;
	private String confFile;
	private Properties props;
	private Logger log;
	
	private ArrayList<Class<Player>> selectedPlayers;
	
	
	/**
	 * Create a new GameConfig object by reading in a conf file. 
	 * @param confFile Game configuration xml file
	 * @param messages Where to output config errors/warnings
	 */
	public GameConfig(String confFile){
		this.confFile = confFile;
		availablePlayers = new ArrayList<Class<Player>>();
		selectedPlayers = new ArrayList<Class<Player>>();
		availableBoards = new ArrayList<File>();
		props = new Properties();
		log = new Logger(ConfigLogLevel, this.getClass());
		load();

		//PANIC if no players defined.
		if(availablePlayers.size() == 0)
			throw new RuntimeException("Error: No players defined. Exiting.");

	}
	/**
	 * Clone an existing GameConfig object.
	 * @param config The GameConfig object to copy.
	 * @param messages Where to output config errors/messages.
	 */
	public GameConfig(GameConfig config){
		this.confFile = config.confFile;
		availablePlayers = new ArrayList<Class<Player>>();
		selectedPlayers = new ArrayList<Class<Player>>();
		props = new Properties(config.props);
		log = new Logger(ConfigLogLevel, this.getClass());
		extractProperties();

		//PANIC if no players defined.
		if(availablePlayers.size() == 0){
			System.err.println("Error: No players defined. Exiting.");
			System.exit(-1);
		}
	}
	/**
	 * @deprecated
	 * @param file The confFile to use.
	 */
	public void setConfFile(String file){
		confFile = file;
	}
	/**
	 * Obtain the list of all valid player classes specified by the xml config
	 * file.
	 * @return An array of valid player classes.
	 */
	public Class<Player>[] getClassList(){
		Class<Player>[] ret = new Class[availablePlayers.size()];
		return availablePlayers.toArray(ret);
	}
	/**
	 * Obtain the list of all valid boards in the location specified by the xml
	 * configuration file.
	 * @return An array of valid board files.
	 */
	public File[] getBoardList(){
		File[] ret = new File[availableBoards.size()];
		return availableBoards.toArray(ret);
	}
	
	public void addActivePlayer(Class<Player> player)
	{
		selectedPlayers.add(player);
	}
	public void removeActivePlayer(Class<Player> player)
	{
		selectedPlayers.remove(player);
	}
	public Class<Player>[] getActivePlayerList(){
		Class<Player>[] ret = new Class[selectedPlayers.size()];
		return selectedPlayers.toArray(ret);
	}
	public int getActivePlayerNum()
	{
		return selectedPlayers.size();
	}
	
	/**
	 * Write out configuration as a text file.
	 *
	 */
	public void save(){
		try{
			FileOutputStream f = new FileOutputStream("properties.xml");
			props.storeToXML(f, "test");
		}catch(IOException e){
			System.err.println("Error writing out conf file: " +e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Read in configuration file.
	 * @param file
	 */
	public void load(){
		try{
			FileInputStream in = new FileInputStream(confFile);
			props.loadFromXML(in);
		}catch(IOException e){
			System.err.println("Error reading configuration file:" + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		extractProperties();
	}
	/**
	 * Get the game configuration parameters out of the Property object.
	 *
	 */
	private void extractProperties(){			
		String s;

		//READ IN CLASSES
		s = props.getProperty("explorers.classes");
		if(s!= null){
			String []names = s.split(" ");
			for(int i = 0; i < names.length; i++){
				try{
					availablePlayers.add((Class<Player>)Class.forName(names[i]));
				}catch(ClassNotFoundException e){
					log.error("[Configuration] Class not found: " + names[i]);
				}
			}
		}
		if(availablePlayers.size() ==0)
			log.fatal("No player classes loaded!!!");
		else
			playerClass = availablePlayers.get(0);
		readBoards();
	}
	/**
	 * Sets the directory from which to read board files.
	 * @param f The path to the board files directory.
	 * @return true if board directory set, false if not. False usually occurs
	 * if f is not a directory.
	 */
	public boolean setBoardDir(File f){
		if(f.isDirectory()){
			props.setProperty("explorers.board.dir", f.getPath());
			return true;
		}
		return false;
	}
	/**
	 * Read all xml files from the board directory. Accept them only if valid.
	 *
	 */
	public void readBoards(){
		availableBoards.clear();
		String s = props.getProperty("explorers.board.dir");
		if(s == null){
			log.error("No board directory specified in conf file.");
		}
		
		File dir = new File(s);
		if(!dir.isDirectory()){
			log.error("Board directory is invalid " + s);
			return;
		}
		
		File[] files = dir.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".xml");
			}
		}
		);
		/*Board b = new Board(1,1);*/
		for(int i = 0; i < files.length; i++){
			/*try{
				b.load(files[i]);
				availableBoards.add(files[i]);
			}catch(IOException e){
				log.error("Problem loading board file " + files[i]);
			}
			catch(BoardSanityException e){
				log.error("Sanity problem loading board file " +files[i]+". " + e);
			}*/
			availableBoards.add(files[i]);
		}		
		if(availableBoards.size() > 0)
			boardFile = availableBoards.get(0);
		else
			boardFile = null;
	}
	/**
	 * Returns the number of ants to be used as the initial colony size.
	 * @return The number of ants (integer).
	 */
	public int getNumExplorers(){
		String s = props.getProperty("explorers.numexps");
		int numAnts;
		try{
			numAnts = Integer.parseInt(s);
		}catch(Exception e){
			numAnts = NUMEXPLORERS;
			//log.warn("explorers.numexps invalid or absent in configuration file, using default.");
			props.setProperty("explorers.numexps", Integer.toString(numAnts));
		}
		if(numAnts <= 0){
			numAnts = NUMEXPLORERS;
			log.warn("explorers.numexps must be positive, using default.");
			props.setProperty("explorers.numexps", Integer.toString(numAnts));
		}
		return numAnts;
	}
	/**
	 * Set the number of ants to be used as the initial colony size.
	 * @param a The number of explorers.
	 */
	public void setNumExplorers(int a){
		props.setProperty("explorers.numexps", Integer.toString(a));
	}
	/**
	 * Returns the maximum number of rounds that a game may last.
	 * @return The number of rounds.
	 */
	public int getMaxRounds() {
		String s = props.getProperty("explorers.rounds");
		int maxRounds;
		try{
			maxRounds = Integer.parseInt(s);				
		}catch(Exception e){
			maxRounds = MAXROUNDS;
			log.warn("explorers.rounds invalid or absent in configuration file, using default.");
		}
		if(maxRounds <= 0){
			maxRounds = MAXROUNDS;
			log.warn("explorers.rounds must be positive, using default.");
		}
		return maxRounds;
	}
	/**
	 * Set the number of ants to be used as the initial colony size.
	 * @param a The number of explorers.
	 */
	public void setRange(int a){
		props.setProperty("explorers.range", Integer.toString(a));
	}
	/**
	 * Returns the maximum number of rounds that a game may last.
	 * @return The number of rounds.
	 */
	public int getRange() {
		String s = props.getProperty("explorers.range");
		int range;
		try{
			range = Integer.parseInt(s);				
		}catch(Exception e){
			range = DEFAULTRANGE;
			log.warn("explorers.range invalid or absent in configuration file, using default.");
		}
		if(range <= 0){
			range = DEFAULTRANGE;
			log.warn("explorers.range must be positive, using default.");
		}
		return range;
	}
	public void setSingleStart(boolean a){
		props.setProperty("explorers.singlestart", Boolean.toString(a));
	}
	/**
	 * Returns the maximum number of rounds that a game may last.
	 * @return The number of rounds.
	 */
	public Boolean getSingleStart() {
		String s = props.getProperty("explorers.singlestart");
		Boolean singlestart;
		try{
			singlestart = Boolean.parseBoolean(s);				
		}catch(Exception e){
			singlestart = false;
			log.warn("explorers.singlestart invalid or absent in configuration file, using default=false.");
		}
		return singlestart;
	}
	/**
	 * Sets the the maximum number of rounds that a game may last.
	 * @param rounds The number of rounds.
	 */
	public void setMaxRounds(int rounds) {			
		if(rounds <= 0)
			log.warn("Max rounds must be greater than zero!");
		else
			props.setProperty("explorers.rounds", Integer.toString(rounds));
	}
	/**
	 * The number of games to be played in a tournament. That is, the number of
	 * times each configuration (player + board) should be played.
	 * @return The number of games.
	 */
	public int getTournamentGames(){
		String s = props.getProperty("explorers.numgames", Integer.toString(NUMGAMES));
		int ret =  Integer.parseInt(s);
		if(ret <=0){
			log.warn("Number of tournament rounds must be greater than 0.");
			ret = NUMGAMES;//set to default
		}
		return ret;
	}
	/**
	 * Set the number of games to be played using each player + board 
	 * combination during a tournament.
	 * @param games The number of games.
	 */
	public void setTournamentGames(int games){
		if(games <=0)
			log.warn("Number of tournament rounds must be greater than 0.");
		else
			props.setProperty("explorers.numgames", Integer.toString(games));
	}
	/**
	 * Set the selected board file.
	 * @param file The board file to use.
	 */
	public void setSelectedBoard(File file) {
		boardFile = file;
	}
	/**
	 * Get the selected board file.
	 * @return The selected board file.
	 */
	public File getSelectedBoard(){
		return boardFile;
	}
	public long getSeed(){
		
		String s = props.getProperty("explorers.seed");
		if(s == null){
			log.warn("No seed specified, using system time.");
		}else{
			try{
				return Long.parseLong(s);
			}catch(NumberFormatException e){
				log.warn("Seed formatted improperly, using system time.");
			}
		}
		return System.currentTimeMillis();
	}
	public Logger.LogLevel getPlayerLogLevel(){
		//READ IN PlayerLogLevel
		Logger.LogLevel level;
		String s = props.getProperty("explorers.Logger.PlayerLogLevel");
		if(s == null){
			log.error("Specified player log level is invalid.");
			level = Logger.LogLevel.TRACE;
		}else{
			try{
				level = Logger.LogLevel.valueOf(s);
			}catch(IllegalArgumentException e){
				log.error("Specified player log level is invalid.");
				level = Logger.LogLevel.TRACE;
			}
		}
		return level;	
	}
	public Logger.LogLevel getSimulatorLogLevel(){
		//READ IN SimulatorLogLevel
		Logger.LogLevel level;
		String s = props.getProperty("explorers.Logger.SimulatorLogLevel");	
		if(s == null){
			log.error("Specified player log level is invalid.");
			level = Logger.LogLevel.TRACE;
		}else{
			try{
				level = Logger.LogLevel.valueOf(s);
			}catch(IllegalArgumentException e){
				log.error("Specified simulator log level is invalid.");
				level = Logger.LogLevel.TRACE;
			}
		}
		return level;
	}
}
