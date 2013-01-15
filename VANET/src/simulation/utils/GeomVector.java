package simulation.utils;

public class GeomVector extends DoublePosition {


	/** Constructor of the 2-uple
	 */
	public GeomVector()
	{
		this(0,0);
	}

	/** Constructor of vector
	 * @param x absissa
	 * @param y ordinate	  
	 */
	public	GeomVector(double x,double y)
	{
		this.x=x;
		this.y=y;
	}

	/** Constructor of vector
	 * @param p Position 
	 */
	public GeomVector(DoublePosition p)
	{
		this(p.x,p.y);
	}
	
	public void setVector (GeomVector v)
	{
		this.x=v.x;
		this.y=v.y;
	}
	
	public GeomVector getVector()
	{
		GeomVector v= new GeomVector();
		//this.normalize();
		v.x=this.x;
		v.y=this.y;
		return v;
	}
	
	/** Constructor of vector
	 * @param p Position 
	 */
	public GeomVector(DoublePosition p1,DoublePosition p2)
	{
		this(p2.x-p1.x,p2.y-p1.y);
	}
	
	
	/** Constructor Constructor of vector
	 * @param p Position 
	 */
	public GeomVector(IntegerPosition p)
	{
		this(p.x,p.y);
	}

	public double length()
	{
		return Math.sqrt(this.x*this.x+this.y*this.y);
	}
	


	public void normalize()
	{
		double length= this.length();
		if (length !=0) 
		{
		this.x=this.x/length;
		this.y=this.y/length;
		}
	}
}