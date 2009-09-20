package explorandum.g6;

import java.util.ArrayList;

/**
 * This is a simple and wrong implementation of the class to get started with.
 */
public class BoundlessCellMatrix {
	
	private ArrayList<ArrayList<Cell>> data;
//	private int minX;
//	private int maxX;
//	private int minY;
//	private int maxY;
	
	public BoundlessCellMatrix() {
		data = new ArrayList<ArrayList<Cell>>(1000);
		for (int i = 0; i < 1000; i++) {
			data.add(new ArrayList<Cell>(1000));
			for (int j = 0; j < 1000; j++) {
				data.get(i).add(null);
			}
		}
//		minX = maxX = minY = maxY = 0;
	}

	/**
	 * x and y can be negative. (-1,-1) != (w-1,h-1).
	 */
	public Cell get(int x, int y) {
		// This is shitty but mod isn't wrappying negative numbers to positive
		// so it'll do for now.
		while (x < 0) {
			x += 1000;
		}
		while (y < 0) {
			y += 1000;
		}
		return data.get(x).get(y);
	}
	
	/**
	 * x and y can be negative. (-1,-1) != (w-1,h-1). 
	 */
	void set(int x, int y, Cell val) {
		// This is shitty but mod isn't wrappying negative numbers to positive
		// so it'll do for now.
		while (x < 0) {
			x += 1000;
		}
		while (y < 0) {
			y += 1000;
		}
		data.get(x).set(y, val);
	}

// These definitions don't make much sense because we don't know what the
// range is.
//	/**
//	 * @return Width = max x - min x + 1.
//	 */
//	int getWidth() {
//		// TODO
//		return 1000;
//	}
//	
//	/**
//	 * @return Height = max y - min y + 1. 
//	 */
//	int getHeight() {
//		// TODO
//		return 1000;
//	}
}
