/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package explorandum.f09.g1;

import java.awt.Point;

/**
 *
 * 
 */
public class Cell {

    private int x;
    private int y;
    private int terrain;
    private int stepStatus;//1 conquered, 2 conquered by someone else, 3 unknown
    private double rank;// tbd
    private int distance;//distance from which this cell was last observed
    
    public void setCell( int x, int y, int terrain, int stepStatus , int distance)
    {
    	//System.out.println("Entered setCell");
    	this.x=x;
    	this.y=y;
    	this.terrain=terrain;
    	this.stepStatus=stepStatus;
    	this.distance= distance;
    }
    
    public void setRank(double rank)
    {
    	this.rank= rank;
    }
    
    //checks if the cell has been visited before
    public boolean checkVisitedCell(){
    	if(this.stepStatus==3)
    		return false;
    	else
    		return true;
    }
    
    public void printCell()
    {
    	System.out.println(this.x + ", " + this.y + ", " + this.terrain + ", " + this.stepStatus + ", "+ this.distance);
    }
    
}
