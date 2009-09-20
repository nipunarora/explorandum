package explorandum.g4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Printer {

	
	
	public static void printOtherPlayersFound(Map map){
		
		Set<Point> all = map.getAllDiscoveredCoordinates();
		HashMap<Integer, ArrayList<Point>> players = new HashMap<Integer, ArrayList<Point>>();
		
		
		for(Point point : all){
			Cell cell = map.getCell(point);
			
			for(Integer id : cell.other_players){
				if(players.containsKey(id)){
					players.get(id).add(cell.location);
				}
				else{
					ArrayList<Point> points = new ArrayList<Point>();
					points.add(cell.location);
					players.put(id, points);
				}
			}
		}
		
		System.out.println("List:");
		for(int player : players.keySet()){
			
			System.out.println("Player id: " + player);
			System.out.println("Cells: " + players.get(player));
			System.out.println("-=-=-=-=-=-=-=");
		}
	}



}
