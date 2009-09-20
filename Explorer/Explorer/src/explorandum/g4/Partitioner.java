package explorandum.g4;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Point;

public class Partitioner {

	private ArrayList<Partition> allPartitions;
	private int current;

	Partitioner() {
		allPartitions = new ArrayList<Partition>();
		current = 0;
	}

	public void updatePartitions(Point[] offsets, Map map) {
		for (int i = 0; i < offsets.length; i++) {
			addCellToPartition(offsets[i], map);
		}
	}

	private Partition createPartition(int terrain) {
		Partition newPartition = new Partition(current++, terrain);
		allPartitions.add(newPartition);
		return newPartition;
	}

	private void addCellToPartition(Point p, Map map) {

		Cell cell = map.getCell(p);
		int terrain = cell.terrain;

		ArrayList<Partition> possiblePartitions = new ArrayList<Partition>();

		// Cell already exists in one of the partitions
		if (getPartitionByCell(cell, map) != null)
			return;

		ArrayList<Cell> neighbors = new ArrayList<Cell>();

		neighbors.add(map.getCell(p.x - 1, p.y + 1));
		neighbors.add(map.getCell(p.x - 1, p.y));
		neighbors.add(map.getCell(p.x - 1, p.y - 1));
		neighbors.add(map.getCell(p.x, p.y + 1));
		neighbors.add(map.getCell(p.x, p.y - 1));
		neighbors.add(map.getCell(p.x + 1, p.y + 1));
		neighbors.add(map.getCell(p.x + 1, p.y));
		neighbors.add(map.getCell(p.x + 1, p.y - 1));
		
		for(int i=0; i<neighbors.size(); i++){
			if(neighbors.get(i) == null)
				neighbors.remove(i);
		}

		
		Iterator<Cell> it = neighbors.iterator();
		for (; it.hasNext(); ) {
			Cell iteratorCell = it.next();
			if (iteratorCell == null)
				continue;
			if (iteratorCell.terrain == terrain) {
				if(getPartitionByCell(iteratorCell, map) != null)
					possiblePartitions.add(getPartitionByCell(iteratorCell, map));
			}
		}

		if (possiblePartitions.size() == 0) {
			// Create new partition
			Partition newPartition = createPartition(cell.terrain);
			newPartition.addCell(cell);
		}

		if (possiblePartitions.size() == 1) {
			// Add it to the current partition
			Partition onlyPartition = possiblePartitions.get(0);
			onlyPartition.addCell(cell);
		}

		if (possiblePartitions.size() > 1) {
			// Merge Partitions
			Partition mergedPartition = createPartition(cell.terrain);
			for (int i = 0; i < possiblePartitions.size(); i++) {
				Partition current = possiblePartitions.get(i);
				mergedPartition.addAll(current);
				allPartitions.remove(current);
			}
			mergedPartition.addCell(cell);
		}

	}
	
	
	
	
	public int getPartitionSizePoint(int x, int y, Map map) {
		return getPartitionSizePoint(new Point(x,y), map);
	}
	

	public int getPartitionSizePoint(Point point, Map map) {
		Cell cell = map.getCell(point);
		for (int i = 0; i < allPartitions.size(); i++) {
			if (allPartitions.get(i).hasCell(cell))
				return allPartitions.get(i).getSize();
		}
		return -1;
	}

	public Partition getPartitionByPoint(int x, int y, Map map) {
		return getPartitionByPoint(new Point(x,y), map);
	}

	
	public Partition getPartitionByPoint(Point point, Map map) {
		Cell cell = map.getCell(point);
		for (int i = 0; i < allPartitions.size(); i++) {
			if (allPartitions.get(i).hasCell(cell))
				return allPartitions.get(i);
		}
		return null;
	}
	
	public Partition getPartitionByCell(Cell cell, Map map) {
		for (int i = 0; i < allPartitions.size(); i++) {
			if (allPartitions.get(i).hasCell(cell))
				return allPartitions.get(i);
		}
		return null;
	}


	public void printPartitions() {
		for (int i = 0; i < allPartitions.size(); i++) {
			//System.out.println(allPartitions.get(i).getId() + " : "
			//		+ allPartitions.get(i).getSize());
		}
	}

	
//	private Partition getPartitionByID(int id) {
//	for (int i = 0; i < allPartitions.size(); i++) {
//		if (allPartitions.get(i).getId() == id)
//			return allPartitions.get(i);
//	}
//	return null;
//}

}
