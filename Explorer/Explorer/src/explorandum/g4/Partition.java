package explorandum.g4;

import java.util.ArrayList;

public class Partition {

	public ArrayList<Cell> partition;
	private int id;
	private int terrain;
	
	public Partition(int _id , int _terrain){
		terrain = _terrain;
		id = _id;
		partition = new ArrayList<Cell>();
	}
	
	
	public void addAll(Partition source){
		for(int i=0; i<source.partition.size(); i++){
			if(!partition.contains(source.partition.get(i))){
				partition.add(source.partition.get(i));
			}
		}
	}
	
	boolean hasCell(Cell cell){
		return partition.contains(cell);
	}
	
	void addCell(Cell cell){
		if(!partition.contains(cell))
			partition.add(cell);
	}
	
	public int getId() {
		return id;
	}


	public int getTerrain() {
		return terrain;
	}
	
	public int getSize(){
		return partition.size();
	}
	
	
}
