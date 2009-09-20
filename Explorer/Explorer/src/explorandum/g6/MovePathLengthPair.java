package explorandum.g6;

import explorandum.Move;

public class MovePathLengthPair {

	public Move move;
	public double pathLength;
	
	public MovePathLengthPair(Move m, int length)
	{
		this.move = m;
		this.pathLength = length;
	}
}
