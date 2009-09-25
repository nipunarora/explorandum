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
    
    public Cell(int x, int y, int terrain, int stepStatus , int distance) {
    	this.x=x;
    	this.y=y;
    	this.terrain=terrain;
    	this.stepStatus=stepStatus;
    	this.distance= distance;
    }
    
    public void setRank(double rank) {
    	this.rank= rank;
    }
    
    //checks if the cell has been visited before
    public boolean checkVisitedCell() {
    	if(this.stepStatus == 3)
    		return false;
    	else
    		return true;
    }
    
    public String toString() {
    	return "x: " + this.x + ", y: " + this.y + ", status:" + this.stepStatus;
    }

    
    //getters and setter
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getTerrain() {
		return terrain;
	}

	public void setTerrain(int terrain) {
		this.terrain = terrain;
	}

	public int getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(int stepStatus) {
		this.stepStatus = stepStatus;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public double getRank() {
		return rank;
	}
}
