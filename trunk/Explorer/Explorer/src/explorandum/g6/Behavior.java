package explorandum.g6;

import java.awt.Point;

import explorandum.Move;

public interface Behavior {

	public Move move(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] otherExplorers,
			Integer[] terrain, int time) throws Exception;

	public String getName();

}
