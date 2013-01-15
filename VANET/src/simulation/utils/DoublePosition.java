package simulation.utils;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
/*

/**
 * @author JPeG
 */

/** Model a 2D-position = (x,y) 
 * @author Jean-Paul Jamont
 */
public class DoublePosition implements Serializable{


	/** absissa */
	public double x;
	/** ordinate */
	public double y;



	/** Constructor of the 2-uple
	 */
	public DoublePosition()
	{
		this(0,0);
	}

	/** Constructor of the 2-uple
	 * @param x absissa
	 * @param y ordinate	  
	 */
	public	DoublePosition(double x,double y)
	{
		this.x=x;
		this.y=y;
	}

	/** Constructor of the 2-uple
	 * @param p Position 
	 */
	public DoublePosition(DoublePosition p)
	{
		this(p.x,p.y);
	}
	
	/** Constructor of the 2-uple
	 * @param p Position 
	 */
	public DoublePosition(IntegerPosition p)
	{
		this(p.x,p.y);
	}

	/** Compares two positions
	 * @param pt the reference position with which to compare.  
	 * @return True if there are a same position
	 */
	public boolean equal(DoublePosition pt)
	{
		return (pt.x==x)&&(pt.y==y);
	}

	/** Compares two positions
	 * @param x An absissa
	 * @param y An ordinate  
	 * @return True if there are a same position
	 */
	public boolean equal(double x,double y)
	{
		return (this.x==x)&&(this.y==y);
	}

	/** Add two coordinates 
	 *  @param pt reference to the added position 
	 */
	public void add(DoublePosition pt)
	{
		this.x+=pt.x;
		this.y+=pt.y;
	}

	/** Substract two coordinates 
	 *  @param pt reference to the subtracted position 
	 */
	public void sub(DoublePosition pt)
	{
		this.x-=pt.x;
		this.y-=pt.y;
	}

	/** Multiplication by an integer
	 *  @param i the factor
	 */
	public void multi(int i)
	{
		this.x*=i;
		this.y*=i;
	}

	/** Multiplication by a float
	 *  @param i the factor
	 */
	public void multi(float f)
	{
		this.x*=f;
		this.y*=f;
	}


	/** Compute the distance with another Position
	 *  @param pos the other position 
	 */
	public double distance(DoublePosition pos)
	{
		double dx = pos.x-x;
		double dy = pos.y-y;

		return Math.sqrt( dx*dx + dy*dy );
	}		


	/** is this position in a rectangle
	 * 
	 * @param origin origin of the circle area
	 * @param radius radius of the circle area
	 * @return true if the position is in the defined circle
	 */
	public boolean inCircleArea(DoublePosition origin, float radius)
	{
		return distance(origin)<=radius;
	}	

	/** is this position in a rectangle
	 *  @param p1 upper coin
	 *  @param p2 lower coin 
	 *   
	 *   p1
	 *    -------------------
	 *    |                 |
	 *    -------------------
	 *                      p2
	 *                    
	 * @return true if the position is in the defined rectangle
	 */
	public boolean inRectangleArea(DoublePosition p1, DoublePosition p2)
	{
		return this.x>=p1.x && this.x<=p2.x && this.y>p1.y && this.y<p2.y;
	}		

	/**
	 * Clones this position object
	 * @return a reference to a cloned position
	 */
	public DoublePosition clone()
	{
		return new DoublePosition(this.x,this.y);
	}

	/** Returns a String object representing this Integer's value 
	 *  @return a reference to the string representation
	 */
	public String toString()
	{
		return new String("("+x+","+y+")");
	}

}
