package explorandum;

import java.awt.Color;

public class Explorer
{
	private int id;
	private String name;
	private Color color;
	
	public Explorer(int ID, String NAME, Color COLOR)
	{
		id = ID;
		name = NAME;
		color = COLOR;
	}
	
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}

	public Color getColor()
	{
		return color;
	}	
}
