package explorandum;

public class MoveWrapper implements GameConstants
{
	private int _action;
	private int _round;
	private Boolean moved;

	public MoveWrapper(int __action, int __round)
	{
		_action = (__action);
		_round = __round;
		moved = false;
	}

	public int get_action()
	{
		return _action;
	}

	public int get_round()
	{
		return _round;
	}
	
	public Boolean canMoveNow(int time)
	{
		switch (_action)
		{
		case STAYPUT:
			return true;
		case EAST:
		case WEST:
		case SOUTH:
		case NORTH:
			return (time >= _round+ORTHMOVE);
		default:
			return (time >= _round+DIAGMOVE);
		}
	}
	
	public void setMoved()
	{
		moved = true;
	}
	
	public Boolean hasMoved()
	{
		return moved;
	}
}
