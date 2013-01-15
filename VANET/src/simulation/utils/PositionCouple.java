package simulation.utils;

/** Model a 2-uple of positions ( (x1,y1) , (x2,y2) ) 
 * @author Jean-Paul Jamont
 */
public class PositionCouple
{
	/** the first position */
	public IntegerPosition position1;
	/** the second position */
	public IntegerPosition position2;
	
	/** default constructor
	 * @param position1 the first position
	 * @param position2 the second position
	 */
	public PositionCouple(IntegerPosition position1, IntegerPosition position2)
	{
		this.position1=position1;
		this.position2=position2;
	}
}
