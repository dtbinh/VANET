package simulation.events.system;
import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.utils.*;

/** event notified when a position is modified 
 * @author Jean-Paul Jamont
 */
public final class PositionModificationEvent extends Event{

	/** the new absisse */
	private int x;
	/** the new ordinate */
	private int y;


	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param x absisse of the new position
	 * @param y ordinate of the new position
	 */
	public PositionModificationEvent(ObjectSystemIdentifier raiser,int x,int y)
	{
		super(raiser);
		this.x=x;
		this.y=y;
	}

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param newPosition the new position
	 */
	public PositionModificationEvent(ObjectSystemIdentifier raiser, IntegerPosition newPosition)
	{
		this(raiser,newPosition.x,newPosition.y);
	}


	public IntegerPosition getNewPosition()
	{
		return new IntegerPosition(this.x,this.y);
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+" moves to ("+this.x+","+this.y+")";
	}

}
