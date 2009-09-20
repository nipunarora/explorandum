package explorandum.g4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.*;
import org.teneighty.heap.Heap.Entry;

public class Cell {
  public Point location;
  public int terrain;
  public ArrayList<Link> neighbors = new ArrayList<Link>();
  public HashSet<Integer> unexplored_directions = new HashSet<Integer>();
  
  
  public Cell previous;
  public double minDist;
  public Entry<Double, Cell> heap_entry;
  public int nullvalue = 0;
  public double openness = 0;
  
  
  
  public Cell openPrevious;
  public double openMinDist;
  public int openNullValue = 0;
  
  public double claimed_distance_other;
  public HashSet<Integer> other_players = new HashSet<Integer>();


  // The closest distance from our player to the cell.
  // E.g. if we see the cell for the second time and this time it is closer, we
  // update claimed_distance and claimed_time;
  public double claimed_distance;

  // The last time claimed_distance get updated.
  public int claimed_time;
}

class nullcompare implements Comparator
{
	public int compare(Object o1,Object o2)
	{
		int n1,n2;
		Cell c1 = (Cell)(o1);
		Cell c2 = (Cell)(o2);
		n1 = c1.nullvalue;
		n2 = c2.nullvalue;
		return n1 == n2 ? 0 : (n1 > n2) ? -1 : 1;
	}
	
}
