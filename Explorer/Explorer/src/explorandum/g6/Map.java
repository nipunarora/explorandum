package explorandum.g6;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import explorandum.GameConstants;

/**
 * Stores (or returns) copies of the Cells, not references to the originals.
 */
public class Map implements GameConstants {

	private Cell[][] map = new Cell[2000][2000];
	int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
	private Set<Point> region;	
	
	public Map() {
		region = new HashSet<Point>();
	}

	/**
	 * returns the cell corresponding to (x,y) coordinates
	 */
	public Cell get(int x, int y) {
		return map[x + 1000][y + 1000];
	}
	
	/**
	 * @return Returns a copy of the keys in the map.
	 */
	public Set<Point> getExploredPoints() {
		Set<Point> points = new HashSet<Point>();
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (get(x, y) != null) {
					points.add(new Point(x, y));
				}
			}
		}
		return points;
	}
	
	public Set<Point> getMapPoints() {
		return getExploredPoints();
	}

	/**
	 * adds a cell to the map 
	 */
	void set(int x, int y, Cell c) {
		if(x < minX)
			minX = x;
		if(x > maxX)
			maxX = x;
		if(y < minY)
			minY = y;
		if(y > maxY)
			maxY = y;
		map[x + 1000][y + 1000] = c;
	}

	int getMinX() {
		return minX;
	}
	
	int getMaxX() {
		return maxX;
	}
	
	int getMinY() {
		return minY;
	}
	
	int getMaxY() {
		return maxY;
	}
	
	public String toString(Point currentLocation) {
		String result = new String();
		for (int y = getMinY(); y <= getMaxY(); y++) {
			String row = Integer.toString(y);
			while (row.length() < 3) {
				row += " ";
			}
			result += row + " ";
			for (int x = getMinX(); x <= getMaxX(); x++) {
				String suffix;
				if (region.contains(new Point(x, y))) {
					suffix = "*";
				} else {
					suffix = " ";
				}
				Cell cell = get(x, y);
				if (currentLocation.x == x && currentLocation.y == y) {
					result += "X" + suffix;
				} else if (cell == null) {
					result += " " + suffix;
				} else if (cell.getTerrainType() == LAND) {
					result += "-" + suffix;
				} else if (cell.getTerrainType() == WATER) {
					result += "~" + suffix;
				} else if (cell.getTerrainType() == MOUNTAIN) {
					result += "^" + suffix;
				} else {
					assert false : "Map.toString: Unknown terrain type";
					result += cell.getTerrainType() + suffix;
				}
			}
			result += "\n";
		}
		return result;
	}

	public void setRegion(List<Point> region) {
		this.region.clear();
		if (region == null) {
			return;
		}
		for (Point point : region) {
			this.region.add(point);
		}
	}
}
