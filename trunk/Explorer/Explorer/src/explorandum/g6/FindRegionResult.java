package explorandum.g6;

import explorandum.Move;

public class FindRegionResult {
	public static final int GOING_TO_REGION = 0;
	public static final int AT_REGION = 1;
	public static final int NO_REGION = 3;
	public int status;
	public Move move;
}
