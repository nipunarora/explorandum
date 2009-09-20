/* 
 * 	$Id: Move.java,v 1.1 2007/09/06 14:51:49 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;

public class Move implements GameConstants{
	private int _action;
	
	
    public Move(int __action){
        _action = __action;
    }
    
    public int getAction() throws Exception {
        return _action;
    }

    public void setAction(int __action) throws Exception {
        _action = __action;
    }
    
    public String toString() {
    	if(_action == STAYPUT){
    		return "Staying put";
    	}
    	return "Moving " + _action;
    }
}
