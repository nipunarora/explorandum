/* 
 * 	$Id: GameConstants.java,v 1.3 2007/11/19 19:44:51 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;

public interface GameConstants {
	public static final int NUMEXPLORERS = 3;
	
	public static final int MIN_EXTSTATE = 0;
    public static final int MAX_EXTSTATE = 255;
	
    public static final int MAXROUNDS = 1000; //default game rounds
    public static final int NUMGAMES = 10; //default tournament games
    
    public static final int STAYPUT = 0;
    public static final int NORTH = 1;
    public static final int NORTHEAST = 2;
    public static final int EAST = 3;
    public static final int SOUTHEAST = 4;
    public static final int SOUTH = 5;
    public static final int SOUTHWEST = 6;
    public static final int WEST = 7;
    public static final int NORTHWEST = 8;
    
    public static final int[] ACTIONS = {STAYPUT, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST};
    public static final String[] ACTION_NAMES = {"STAYPUT", "NORTH", "NORTHEAST","EAST","SOUTHEAST","SOUTH","SOUTHWEST","WEST","NORTHWEST"};
    
    /**
     * When indexed by an ACTION enumeration value, this array provides the
     * x direction to move.
     */
    public static final int[] _dx = {0, 0, 1, 1, 1, 0, -1, -1, -1};
    /**
     * When indexed by an ACTION enumeration value, this array provides the
     * y direction to move.
     */
    public static final int[] _dy = {0, -1, -1, 0, 1, 1, 1, 0, -1};
    

    public static final int LAND = 0;
    public static final int WATER = 1;
    public static final int MOUNTAIN = 2;
    
    public static final String[] TERRAIN_NAMES = {"LAND", "WATER", "MOUNTAIN"};
    
    public static final int ORTHMOVE = 2;
    public static final int DIAGMOVE = 3;
    
    public static final int DEFAULTRANGE = 3;
}
