/* 
 * 	$Id: Board.java,v 1.6 2007/11/28 16:30:18 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import dom.Writer;

/**
 * The board class encapsulates all board related game play logic and data. The
 * board is made up of cell objects and provides an interface for accessing the
 * information contained in those cells based on their coordinates. The board
 * class also provides the means of loading and saving a board, checking a
 * board's sanity, and performing reachability tests.
 * 
 * @author johnc
 * 
 */
public final class Board implements GameConstants
{

	private Cell[][] cells;
	private int width;
	private int height;
	private int range;
	private Random random;
	private ArrayList<Cell> explorerCells;

	public Board(int width, int height, int range)
	{
		this.width = width + range * 2;
		this.height = height + range * 2;
		random = new Random();
		this.range = range;
		init();
	}

	public Board(String file) throws IOException, BoardSanityException
	{
		random = new Random();
		load(new File(file));

	}

	public void setRange(int range)
	{
		this.range = range;
	}

	public void load(File f) throws IOException, BoardSanityException
	{
		DOMParser parser = new DOMParser();
		try
		{
			FileInputStream fis = new FileInputStream(f);
			InputSource s = new InputSource(fis);
			parser.parse(s);
		} catch (SAXException e)
		{
			throw new IOException("XML Parsing exception: " + e);
		}
		try
		{
			Document doc = parser.getDocument();
			Node dim = doc.getElementsByTagName("DIMENSIONS").item(0);

			NamedNodeMap dim_attrs = dim.getAttributes();
			width = Integer.parseInt(dim_attrs.getNamedItem("width").getNodeValue()) + range * 2;
			height = Integer.parseInt(dim_attrs.getNamedItem("height").getNodeValue()) + range * 2;

			init();

			NodeList nodes = doc.getElementsByTagName("CELL");
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Node e = nodes.item(i);
				NamedNodeMap attrs = e.getAttributes();
				int x = Integer.parseInt(attrs.getNamedItem("x").getNodeValue()) + range;
				int y = Integer.parseInt(attrs.getNamedItem("y").getNodeValue()) + range;
				String terrain = attrs.getNamedItem("terrain").getNodeValue();
				Boolean startable = Boolean.parseBoolean(attrs.getNamedItem("startable").getNodeValue());
				cells[x][y].setLocation(x, y);
				for (int j = 0; j < TERRAIN_NAMES.length; j++)
					if (terrain.equals(TERRAIN_NAMES[j]))
					{
						cells[x][y].setCellTerrain(j);
						break;
					}
				cells[x][y].setStartable(startable);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new IOException("Problem loading board xml file: " + e);
		}

		// sanityCheck();

	}

	public void save(File f) throws IOException, BoardSanityException
	{

		// sanityCheck();

		Document xmlDoc = new DocumentImpl();
		Element root = xmlDoc.createElement("BOARD");

		Element dim = xmlDoc.createElement("DIMENSIONS");
		dim.setAttribute("width", Integer.toString(width));
		dim.setAttribute("height", Integer.toString(height));

		root.appendChild(dim);

		Element cells = xmlDoc.createElement("CELLS");
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				cells.appendChild(this.cells[x][y].toXML(xmlDoc));

		root.appendChild(cells);

		xmlDoc.appendChild(root);

		Writer writer = new Writer();

		FileOutputStream os = new FileOutputStream(f);
		writer.setOutput(os, null);
		writer.write(xmlDoc);
		os.close();
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Cell getCell(int x, int y)
	{
		return cells[x][y];
	}

	private void init()
	{
		cells = new Cell[width][];
		for (int x = 0; x < width; x++)
		{
			cells[x] = new Cell[height];
			for (int y = 0; y < height; y++)
			{
				cells[x][y] = new Cell(x, y, 1d);
				if (range != 0)
				{
					// Default terrain to water when not editor mode
					cells[x][y].setCellTerrain(WATER);
				}
			}
		}

		explorerCells = new ArrayList<Cell>();
	}

	/**
	 * Find cell containing that explorer
	 * 
	 * @param e
	 * @return
	 */
	public Cell getExplorer(Explorer e)
	{
		for (int i = 0; i < explorerCells.size(); i++)
		{
			Cell c = explorerCells.get(i);
			if (c.containsExplorer(e))
				return c;
		}

		// if the previous method fails,
		// revert to looping,
		// just in case there is a bug somewhere.
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				if (getCell(i, j).containsExplorer(e))
					return getCell(i, j);
		return null;
	}

	public boolean moveExplorer(Explorer e, Cell cellTo)
	{
		Cell cellFrom = getExplorer(e);
		if (cellFrom != null)
		{
			//if explorer is on another cell, remove from that cell
			cellFrom.removeExplorer(e);
			explorerCells.remove(cellFrom);
		}

		//add explorer to new cell
		cellTo.addExplorer(e);
		explorerCells.add(cellTo);
		return true;
	}

	/**
	 * Generate a random board. There will be at least one reachable nest
	 * location from the starting location.
	 * 
	 * @param height
	 *            The height of the new random board.
	 * @param width
	 *            The width of the new random board.
	 * @param numStartable
	 *            The number of nestable cells
	 * @param fractionBlocked
	 *            The fraction of cells that should be blocked. Must be less
	 *            than .9
	 */
	public void random(int height, int width, int numStartable, double fractionBlocked)
	{
		this.width = width;
		this.height = height;
		init();

		if (fractionBlocked > .9)
			throw new RuntimeException("Fraction blocked must be less than .9");
		if (numStartable <= 0)
			throw new RuntimeException("Number of nests must be greater than 0");

		if (width < 0 || height < 0)
			throw new RuntimeException("Illegal height and/or width");

		// place random blocked squares.
		int nBlocked = 0;
		while (nBlocked < height * width * fractionBlocked)
		{
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			Cell c = getCell(x, y);
			if (!c.isBlocked())
			{
				c.setCellTerrain(random.nextInt(2) + 1);
				nBlocked++;
			}
		}

		// now place nests and queen
		boolean sane = false;
		do
		{
			// first clear nests and queen (in case we're trying again)
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
				{
					Cell c = getCell(i, j);
					c.setStartable(false);
				}

			// place nests
			int numNests = 0;
			do
			{
				int x = random.nextInt(width);
				int y = random.nextInt(height);
				Cell c = getCell(x, y);
				if (!c.isBlocked() && !c.isStartable())
				{
					c.setStartable(true);
					numNests++;
				}
			} while (numNests < numStartable);

			sane = true;

			// try {
			// sanityCheck();
			// sane = true;
			// } catch (BoardSanityException e) {
			// sane = false;
			// }
		} while (!sane);
	}

	/**
	 * Determines that there is at least one nestable cell reachable from the
	 * starting location.
	 * 
	 * @throws BoardSanityException
	 */
	public void sanityCheck() throws BoardSanityException
	{

		int x, y;
		x = -1;
		y = -1;

		// Find all cells reachable from the queen.
		ArrayList<Cell> reachable = reachable(x, y);

		// there should be at least two reachable start location
		int numNests = 0;
		Iterator<Cell> it = reachable.iterator();
		while (it.hasNext())
		{
			Cell cell = it.next();
			if (cell.isStartable())
				numNests++;
		}

		if (numNests <= 1)
			throw new BoardSanityException("Board is not sane. There must be at least one startable cell location.");
	}

	public ArrayList<Cell> reachable(int x, int y)
	{
		// ArrayList<Cell> reachable = new ArrayList<Cell>();
		// recursiveDFS(reachable, x, y);
		// return reachable;
		return iterativeDFS(x, y);
	}

	private ArrayList<Cell> iterativeDFS(int x, int y)
	{
		ArrayList<Cell> reachable = new ArrayList<Cell>();
		Stack<Cell> inProgress = new Stack<Cell>();
		// Start the stack...
		if (inBounds(x, y))
		{
			inProgress.push(getCell(x, y));
		}
		// while there are still cells to process, keep going
		while (!inProgress.isEmpty())
		{
			Cell current = inProgress.pop();
			reachable.add(current); // add this cell to the reachable cells
			x = current.getLocation().x;
			y = current.getLocation().y;
			for (int dir = 0; dir < ACTIONS.length; dir++)
			{
				int new_x = x + _dx[dir];
				int new_y = y + _dy[dir];
				if (inBounds(new_x, new_y))
				{
					Cell c = getCell(new_x, new_y);
					if (!c.isBlocked() && !reachable.contains(c) && !inProgress.contains(c))
					{
						// if we haven't finished this cell and it isn't in
						// progress, push into stack
						inProgress.push(c);
					}
				}
			}
		}
		return reachable;
	}

	/**
	 * @deprecated
	 * @param reachable
	 * @param x
	 * @param y
	 */
	private void recursiveDFS(ArrayList<Cell> reachable, int x, int y)
	{
		if (inBounds(x, y))
		{
			// valid location, continue...
			Cell c = getCell(x, y);
			if (!reachable.contains(c) && !c.isBlocked())
			{
				// we haven't been to this cell yet.
				reachable.add(c); // add this cell
				// go in every direction.
				for (int dir = 0; dir < ACTIONS.length; dir++)
				{
					recursiveDFS(reachable, x + _dx[dir], y + _dy[dir]);
				}
			}
		}
	}

	public boolean inBounds(int x, int y)
	{
		return (x >= 0 && x < width) && (y >= 0 && y < height);
	}

	static public class BoardSanityException extends Exception
	{
		public BoardSanityException(String string)
		{
			super(string);
		}

		private static final long serialVersionUID = 1L;
	}

	public boolean[] getBlocked(int x, int y)
	{
		boolean[] ret = new boolean[ACTIONS.length - 1];
		for (int i = 0; i < ACTIONS.length - 1; i++)
		{
			int new_x = x + _dx[ACTIONS[i]];
			int new_y = y + _dy[ACTIONS[i]];
			if (inBounds(new_x, new_y))
			{
				Cell c = getCell(new_x, new_y);
				ret[i] = c.isBlocked();
			} else
			{
				ret[i] = true; // off the map is "blocked"
			}
		}

		return ret;
	}

	public boolean[] hasExplorers(int x, int y)
	{
		boolean[] ret = new boolean[ACTIONS.length - 1];
		for (int i = 0; i < ACTIONS.length - 1; i++)
		{
			int new_x = x + _dx[ACTIONS[i]];
			int new_y = y + _dy[ACTIONS[i]];
			if (inBounds(new_x, new_y))
			{
				Cell c = getCell(new_x, new_y);
				ret[i] = c.getNumExplorers() > 0;
			} else
			{
				ret[i] = false; // off the map cell has no ants
			}
		}
		// special case for this cell -- don't count me.
		ret[STAYPUT] = getCell(x, y).getNumExplorers() - 1 > 0;
		return ret;
	}

	public int countStartable()
	{
		int t = 0;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
			{
				Cell c = getCell(i, j);
				if (c.isStartable())
					t++;
			}
		return t;
	}

	public Cell[] getStartCells(int n)
	{
		int t = countStartable();
		if (n > t)
			n = t;

		Cell[] cells = new Cell[n];

		int index = 0;
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				Cell c = getCell(i, j);
				if (c.isStartable())
				{
					cells[index] = c;
					index++;
					if (index >= n)
						return cells;
				}
			}
			if (index >= n)
				return cells;
		}

		return cells;
	}

	public int getScore(Explorer e)
	{
		int score = 0;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
			{
				Cell c = getCell(i, j);
				if (c.getObserved())
					if (c.getObservedBy().getId() == e.getId())
						score++;
			}
		return score;
	}
}
