package explorandum.g6;

public class Cell {
	private int terrain;
	private double distanceSeenFrom;
	private boolean opponentWasOn;
	
	//Other cell attributes:
	//earliest time seen and distance at time
	//closest distance seen and time
	
	public double getDistanceSeenFrom()
	{
		return distanceSeenFrom;
	}
	
	public boolean wasOpponentSeenOn()
	{
		return opponentWasOn;

	}

	public Cell(int terrain, Integer[] explorers, double dist) {
		this.terrain = terrain;
		this.distanceSeenFrom = dist;
		for(int i = 0 ; i < explorers.length ; ++i)
		{
			if(explorers[i] != null)
				if(explorers[i] != G6Player.explorerID)
				{
					opponentWasOn = true;
					break;
				}
		}
	}

	public int getTerrainType() {
		return terrain;
	}
	
	
	public boolean equals(Cell other) {
		return terrain == other.terrain && opponentWasOn == other.opponentWasOn && distanceSeenFrom == other.distanceSeenFrom;
	}
}
