package explorandum.f09.g1.strategies;

import java.awt.Point;

import explorandum.f09.g1.Map;

public abstract class Strategy {
	
	protected Map memory;
	protected Map view;
	protected int range;
	
	public abstract int getMove(Point currentLocation, Point[] offsets,
			Boolean[] hasExplorer, Integer[][] visibleExplorers,
			Integer[] terrain, int time, Boolean StepStatus);
	
}
