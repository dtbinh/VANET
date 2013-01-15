package simulation.utils;

public class Dimension extends IntegerPosition{

	/** Constructor of the 2-uple
	 */
	public Dimension()
	{
		super(0,0);
	}

	/** Constructor of the 2-uple
	 * @param x absissa
	 * @param y ordinate	  
	 */
	public	Dimension(int x,int y)
	{
		super(x,y);
	}

	/** Constructor of the 2-uple
	 * @param p Position 
	 */
	public Dimension(IntegerPosition p)
	{
		this(p.x,p.y);
	}
	
}
