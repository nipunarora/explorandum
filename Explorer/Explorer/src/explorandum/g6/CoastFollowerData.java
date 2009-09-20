package explorandum.g6;

import java.awt.Point;

import explorandum.Move;

/**
 * Stuff the coast follower needs
 *
 */
public class CoastFollowerData{
	private Point offset_from_current, offset_from_start, target_offset;
	private int terrain;
	private boolean explorer;
	private int leaningDirection;
	private Move move;
	private boolean isOpposite;

	//Other cell attributes:
	//earliest time seen and distance at time
	//closest distance seen and time
	
	public CoastFollowerData(Point offset_current, Point offset_total, int trn, boolean exp) {
		offset_from_current = offset_current;
		offset_from_start = offset_total;
		terrain = trn;
		explorer = exp;
	}
	
	public CoastFollowerData(Move mov, int lean, Point target) {
		move = mov;
		leaningDirection = lean;
		target_offset = target;
	}
	
	public Point getOffsetFromCurrent() {
		return offset_from_current;
	}

	
	public Point getOffset() {
		return offset_from_start;
	}
	
	public int getTerrainType() {
		return terrain;
	}
	
	public boolean hasExplorer() {
		return explorer;
	}
	
	public void setLeaningDirection(int act) {
		leaningDirection = act;
	}
	
	public int getLeaningDirection() {
		return leaningDirection;
	}
	
	public void setMove(Move act) {
		move = act;
	}
	
	public void setTarget(Point target) {
		target_offset = target;
	}
	
	public Move getMove() {
		return move;
	}
	
	public Point getTarget() {
		return target_offset;
	}
	
	public boolean isOpposite() {
		return isOpposite;
	}
	
	public void setOpposite(boolean opp) {
		isOpposite = opp;
	}
}
