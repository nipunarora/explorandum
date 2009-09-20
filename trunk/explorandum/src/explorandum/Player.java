/* 
 * 	$Id: Player.java,v 1.2 2007/11/13 02:49:55 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;
/**
 * The Player interface must be implemented by all user defined players. This
 * is the only interface through which user code interacts with the game.
 */
public interface Player extends GameConstants{
    //public void register(GameEngine engine) throws Exception;
	/**
	 * Returns the name of this player.
	 * @return The name of the player.
	 */
    public String name() throws Exception;
    /**
     * Returns the player's color. 
     * @return The color of explorer.
     */
    public Color color() throws Exception;

    /**
     * @param explorerID ID of the explorer
     * @param rounds Total number of rounds in the game
     * @param explorers Number of explorers in the game
     * @param range Visibility range
     * @param log Logging class
     * @param rand Random number generator
     */
    public void register(int explorerID, int rounds, int explorers, int range, Logger log, Random rand);
    
    /**
     * The move is called for a player and stored until it can be executed. 
     * ie- 2 rounds for orthogonal moves and 3 rounds for diagonal moves.
     * After executing the move of the explorer, the next move is asked from the player,
     * after providing the player with the information about the cells visible to them.
     * 
     * @param currentLocation Offset of current cell from the start cell of the explorer
     * @param offsets Offset of the cell referred, from the explorer's starting cell 
     * @param hasExplorer Boolean to indicate if the cell contains an explorer
     * @param visibleExplorers IDs of the explorers in that cell
     * @param terrain Terrain type of the cell
     * @param time Round number of this move
     * @return Action (StayPut/North/West/...)
     * @throws Exception
     */
    public Move	move(	Point currentLocation,
    					Point[] offsets,
    					Boolean[] hasExplorer,
    					Integer[][] visibleExplorers,
    					Integer[] terrain,
    					int time,
    					Boolean StepStatus) throws Exception;
}
