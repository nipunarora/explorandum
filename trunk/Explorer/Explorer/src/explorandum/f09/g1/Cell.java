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
    private int status;
    private double rank;
    
    public void setCell( int x, int y, int status )
    {
    	this.x=x;
    	this.y=y;
    	this.status=status;
    }
    
    public void setRank(double rank)
    {
    	this.rank= rank;
    }
    
}
