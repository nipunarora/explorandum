package explorandum;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Cell implements GameConstants
{
	private Boolean Observed;
	private Double ObservedDistance;
	private Explorer ObservedBy;
	private Boolean Stepped;
	private ArrayList<CellListener> listeners;
	

	/*Cell bounds*/
	private Point Location;
	private Point2D.Double Center;
	private Double Dimensions;
	
	
	/*Cell Properties*/
	private int CellTerrain;
	private Boolean blocked;
	private Boolean startable;
	private ArrayList<Explorer> explorers = new ArrayList<Explorer>();
	
	

	public Cell(int x, int y, Double dimensions)
	{
		CellBounds(new Point(x, y), dimensions);
		Stepped = false;
		Observed = false;
		ObservedDistance = Double.MAX_VALUE;
		ObservedBy = new Explorer(0, "EMPTY", Color.WHITE);
		
		listeners = new ArrayList<CellListener>();
	}

	public Cell(Point location, Double dimensions)
	{
		CellBounds(location, dimensions);
		Stepped = false;
		Observed = false;
		ObservedDistance = Double.MAX_VALUE;
		ObservedBy = new Explorer(0, "EMPTY", Color.WHITE);

		listeners = new ArrayList<CellListener>();
	}

	public void CellBounds(Point location, Double dimensions)
	{
		this.setLocation(location.x, location.y);
		Dimensions = dimensions;
		blocked = false;
		startable = false;
		explorers = new ArrayList<Explorer>();
		
		Center = new Point2D.Double(((double)Location.x + 0.5d)*Dimensions, ((double)Location.y + 0.5d)*Dimensions);
	}
	

	/**
	 * @return
	 */
	
	public Boolean isSteppedOn()
	{
		// Do something here
		return Stepped;
	}
	
	public Boolean getObserved()
	{
		return new Boolean(Observed);
	}

	/**
	 * @return
	 */
	public Double getObservedDistance()
	{
		return new Double(ObservedDistance);
	}

	/**
	 * @return
	 */
	public Explorer getObservedBy()
	{
		return ObservedBy;
	}
	
	/**
	 * @param e
	 * @param from
	 * @return
	 */
	public Boolean Observe(Explorer e, Cell from)
	{
		Double d = distanceToCell(from);
		if(d < ObservedDistance)
		{
			Observed = true;
			ObservedBy = e;
			ObservedDistance = d;
			
			notifyListeners();
			
			if(ObservedDistance == 0)
			{
				Stepped = true;
			}
			
			return true;
		}
		
		return false;
	}
	

	/**
	 * @param c
	 * @return
	 */
	public Double distanceToCell(Cell c)
	{
		return Center.distance(c.Center);
	}
	
	/**
	 * @param c
	 * @return
	 */
	public Cell copy(Cell c)
	{
		return new Cell(c.Location, c.Dimensions);
	}

	/**
	 * @return
	 */
	public Point getLocation()
	{
		return new Point(Location);
	}

	/**
	 * @return
	 */
	public Point2D.Double getCenter()
	{
		return new Point2D.Double(Center.x,Center.y);
	}

	/**
	 * @return
	 */
	public Double getDimensions()
	{
		return new Double(Dimensions);
	}

	/**
	 * @return
	 */
	public int getCellTerrain()
	{
		return new Integer(CellTerrain);
	}
	
	public Boolean hasExplorers()
	{
		if(explorers.size() > 0)
			return true;
		else
			return false;
	}
	
	public Boolean containsExplorer(Explorer e)
	{
		for (int i = 0; i < explorers.size(); i++)
		{
			Explorer explorer = explorers.get(i);
			if(explorer.getId() == e.getId())
				return true;
		}
		
		return false;
	}
	
	public ArrayList<Explorer> getExplorers()
	{
		return new ArrayList<Explorer>(explorers);
	}
	
	public int getNumExplorers()
	{
		return explorers.size();
	}

	public Element toXML(Document doc){
		Element e = doc.createElement("CELL");
		e.setAttribute("x", Integer.toString(Location.x));
		e.setAttribute("y",Integer.toString(Location.y));
		//e.setAttribute("dimensions", Double.toString(Dimensions));
		e.setAttribute("terrain", TERRAIN_NAMES[CellTerrain]);
		if(startable)
			e.setAttribute("startable", "true");
		else
			e.setAttribute("startable", "false");
		return e;
	}
	
	public boolean addExplorer(Explorer e)
	{
		boolean b = explorers.add(e);
		notifyListeners();
		return b;
	}
	
	public boolean removeExplorer(Explorer e)
	{
		boolean b = explorers.remove(e);
		notifyListeners();
		return b;
	}
	
	public void setLocation(int x, int y)
	{
		Location = new Point(x, y);
	}

	public void setCellTerrain(int cellTerrain)
	{
		CellTerrain = cellTerrain;
		if(CellTerrain == LAND)
			blocked = false;
		else
			blocked = true;
		notifyListeners();
	}
	
	public Boolean isBlocked()
	{
		return new Boolean(blocked);
	}
	
	public Boolean isStartable()
	{
		return new Boolean(startable);
	}

	public void setStartable(Boolean startable)
	{
		this.startable = startable;
		notifyListeners();
	}
	
	public static interface CellListener{
		public void CellUpdated(Cell source);
	}

	public void addCellListener(CellListener l){
		listeners.add(l);
	}
	public void removeCellListener(CellListener l){
		listeners.remove(l);
	}
	private void notifyListeners(){
		Iterator<CellListener> it = listeners.iterator();
		while(it.hasNext())
			it.next().CellUpdated(this);
	}
	
	@Override
	public String toString()
	{
		return "(" + Location.x + "," + Location.y + ")" + (hasExplorers()?"E":"") + ":" + TERRAIN_NAMES[CellTerrain] 
		       + (Observed?" Observed(" + ObservedDistance + "):" + ObservedBy.getName() + "[" + ObservedBy.getId() + "]": "");
	}
	
}
