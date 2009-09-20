package explorandum.g4;


import java.awt.Point;
import java.util.*;
import org.teneighty.heap.BinaryHeap;
import org.teneighty.heap.Heap.Entry;

public class ShortestPath {

	
	Map map;
	Point sourcePoint;
  BinaryHeap<Double, Cell> all;
	
	ShortestPath(Map _map){
		map = _map;		
	}
	
	public double getShortestDistance(Point destPoint){
		
		Cell destCell = map.getCell(destPoint);
		if(destCell != null)
			return destCell.minDist;
		return -1;
	}
	
	public ArrayList<Cell> getShortestDistancePath(Point destPoint){
		ArrayList<Cell> path = new ArrayList<Cell>();
		Cell destCell = map.getCell(destPoint);
		Cell sourceCell = map.getCell(sourcePoint);
		Cell tracer = destCell;
		if (destCell == null || destCell.minDist == Integer.MAX_VALUE) {
			//Util.debugMessage("shortest path",
//                        "ERROR: get shortest distance to an unreachable cell: "
//                         + destPoint);
			return null;
    }
		for(;tracer != sourceCell; ){
			path.add(tracer);
			tracer = tracer.previous;
		}
		Collections.reverse(path);
		return path;
	}

	
	public void updateShortestPath(Point _sourcePoint) {
		
		sourcePoint = _sourcePoint;
		Cell sourceCell = map.getCell(sourcePoint);

		setDistanceToInfinity();
		setSourceDistanceToZero();
		runDijkstra(sourceCell);
	}

	
	private void setDistanceToInfinity(){//And previous to null
		Set<Point> allPoints = map.getAllDiscoveredCoordinates();
    all = new BinaryHeap<Double, Cell>();
		for(Iterator<Point> i=allPoints.iterator(); i.hasNext();){
			Point p = i.next();
			Cell cell = map.getCell(p);
			cell.minDist = Integer.MAX_VALUE;
			cell.heap_entry = all.insert(new Double(cell.minDist), cell);
			cell.previous = null;
		}
	}
	
	
	private void setSourceDistanceToZero(){
		Cell sourceCell = map.getCell(sourcePoint);
		sourceCell.minDist = 0;
    all.decreaseKey(sourceCell.heap_entry, 0.0);
	}

	
	
	private void runDijkstra(Cell source){
		
		//Implement Dijkstra's here
		
		while(all.getSize() != 0){
      Entry<Double, Cell> entry = all.getMinimum();
      Cell cell = entry.getValue();
      all.delete(entry);

      if(cell.minDist == Integer.MAX_VALUE)
        break;

			for(int i=0; i<cell.neighbors.size(); i++){
				Link link = cell.neighbors.get(i);
				Cell dest = cell.neighbors.get(i).destination;
				
        double dist = cell.minDist + link.cost;
				
				if(dist < dest.minDist){
          all.decreaseKey(dest.heap_entry, dist);
					dest.minDist = dist;
					dest.previous = cell;
				}
			}
		}
	}
	
	
	
	private Cell getMin(ArrayList<Cell>all){
		double min = all.get(0).minDist;
		int minIndex = 0;
		
		for(int i=1; i<all.size(); i++){
			if(all.get(i).minDist < min){
				minIndex = i;
				min = all.get(i).minDist;
			}
		}
		
		Cell v = all.get(minIndex);
		all.remove(minIndex);
		return v;

 	}
	
	
}
