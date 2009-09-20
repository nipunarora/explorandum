package explorandum.g4;


import java.awt.Point;
import java.util.*;
import org.teneighty.heap.BinaryHeap;
import org.teneighty.heap.Heap.Entry;


public class OpenShortestPath {

	
	Map map;
	Point sourcePoint;
  BinaryHeap<Double, Cell> all;
	int range;
	
	double totalOpenness;
	double minOpenness;
	double maxOpenness;
	
	OpenShortestPath(Map _map, int _range){
		map = _map;		
		range = _range;
	}
	
	public double getShortestDistance(Point destPoint){ //done
		
		Cell destCell = map.getCell(destPoint); //done
		if(destCell != null) //done
			return destCell.openMinDist; //done
		return -1; //done
	}
	
	void printAllCellData(){
		
		Set<Point> allPoints = map.getAllDiscoveredCoordinates(); //done

		for(Point point : allPoints){
			Cell cell = map.getCell(point);
			if(cell != null){
				//if(cell.openPrevious == null)
					//System.out.println("Location: " + cell.location + ",Terrain:" +cell.terrain +  ",Min Dist:" + cell.openMinDist + ",Previosus:" + cell.openPrevious);
				//else
					//System.out.println("Location: " + cell.location + ",Terrain:" +cell.terrain +  ",Min Dist:" + cell.openMinDist + ",Previosus:" + cell.openPrevious.location);
					
			}
		}
		
		//System.out.println("AllNULLS");
		for(Point point : allPoints){
			Cell cell = map.getCell(point);
//			if(cell != null){
//				if(cell.openPrevious == null)
					//System.out.println("Location: " + cell.location +",Terrain:" +cell.terrain +  ",Min Dist:" + cell.openMinDist + ",Previosus:" + cell.openPrevious);
//			}
		}
		
		
	}
	
	public ArrayList<Cell> getShortestDistancePath(Point destPoint){ //done
		ArrayList<Cell> path = new ArrayList<Cell>(); //done
		Cell destCell = map.getCell(destPoint); //done
		Cell sourceCell = map.getCell(sourcePoint); //done
		Cell tracer = destCell; //done
		if (destCell == null || destCell.openMinDist == Double.MAX_VALUE) { //done
			//System.out.println("ERROR: get shortest distance to an unreachable cell"); //done
			return null; //done
    }
		for(;tracer != sourceCell; ){ //done
			path.add(tracer); //done
			tracer = tracer.openPrevious; //done
		}
		Collections.reverse(path); //done
		return path; //done
	}

	
	public void updateShortestPath(Point _sourcePoint) { //done
		
		sourcePoint = _sourcePoint; //done
		Cell sourceCell = map.getCell(sourcePoint); //done

		setDistanceToInfinity(); //done
		setSourceDistanceToZero(); //done
		runDijkstra(sourceCell); //done
	}

	
	private void setDistanceToInfinity(){//And previous to null //done
		Set<Point> allPoints = map.getAllDiscoveredCoordinates(); //done
    all = new BinaryHeap<Double, Cell>();
		
		totalOpenness = 0;
		minOpenness = Double.MAX_VALUE;
		maxOpenness = Double.MIN_VALUE;
		
		for(Iterator<Point> i=allPoints.iterator(); i.hasNext();){ //done
			Point p = i.next(); //done
			Cell cell = map.getCell(p); //done
			cell.openPrevious = null;  //done
			cell.openMinDist = Double.MAX_VALUE; //done
			updateOpenness(cell);
			cell.heap_entry = all.insert(new Double(cell.openMinDist), cell);
		}
	}
	
	private double getCostWithOpenness(Link link){
		
		double cost = link.cost;
		double openness = link.destination.openness;
		int total = all.getSize();
		double mean = totalOpenness/total;
		double change;
		
		if(openness > mean)
			change = 0.5 + ( ( openness - mean ) * (0.5) / (maxOpenness - mean) );
		else
			change = ( ( mean - openness ) * (0.5) / ( mean - minOpenness ) );
		
		double openCost = cost - ( change * cost );
			
		return openCost;

	}
	
	private void updateOpenness(Cell c) {
		double openness = map.getCellOpenness(c.location, Math.min(3, range));
		
		if(openness > maxOpenness)
			maxOpenness = openness;
		if(openness < minOpenness)
			minOpenness = openness;
		
		c.openness = openness;
		totalOpenness += openness;
		
	}

	private void setSourceDistanceToZero(){ //done
		Cell sourceCell = map.getCell(sourcePoint); //done
		sourceCell.openMinDist = 0; //done
    all.decreaseKey(sourceCell.heap_entry, 0.0);
	}
	
	private void runDijkstra(Cell source){ //done
		
		//Implement Dijkstra's here
		
		while(all.getSize() != 0){ //done
      Entry<Double, Cell> entry = all.getMinimum();
      Cell cell = entry.getValue();
      all.delete(entry);

      if(cell.minDist == Integer.MAX_VALUE)
        break;

			for(int i=0; i<cell.neighbors.size(); i++){ //done
				Link link = cell.neighbors.get(i); //done
				Cell dest = cell.neighbors.get(i).destination; //done
				double openCost = getCostWithOpenness(link);
				
        double dist = cell.openMinDist + openCost; //done
				
				if(dist < dest.openMinDist){ //done
          all.decreaseKey(dest.heap_entry, dist);
					dest.openMinDist = dist; //done
					dest.openPrevious = cell; //done
				}
			}
		}
	}
	
	
	
	private Cell getMin(ArrayList<Cell>all){ //done
		double min = all.get(0).openMinDist; //done
		int minIndex = 0; //done
		
		for(int i=1; i<all.size(); i++){ //done
			if(all.get(i).openMinDist < min){ //done
				minIndex = i; //done
				min = all.get(i).openMinDist; //done
			}
		}
		
		Cell v = all.get(minIndex); //done
		all.remove(minIndex); //done
		return v; //done

 	}
	
	
}
