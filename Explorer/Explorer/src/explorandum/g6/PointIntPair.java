package explorandum.g6;

import java.awt.Point;


public class PointIntPair {
	 public Point p;
     public double value;

     public PointIntPair(Point p, double value)
     {
             this.p = p;
             this.value = value;
     }

     public double getValue()
     {
             return value;
     }

     public Point getPoint()
     {
             return this.p;
     }

     /*public int compareTo(PointIntPair o) {
             if(equals(o))
                     return 0;
             int comp =  value.compareTo(o.value);
             return comp != 0 ? comp : ((Integer)this.p.x).compareTo(o.p.x);
     }
      */
   /*  public boolean equals(Object o) {
             if (o instanceof PointIntPair)
            	 return ((Integer)value).compareTo(((PointIntPair)o).value);
             return false;
     }*/

	
}
