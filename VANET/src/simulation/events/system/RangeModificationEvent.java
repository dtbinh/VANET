package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when a range is modified 
 * @author Jean-Paul Jamont
 */
public final class RangeModificationEvent extends Event{


	/** the new range */
	private int range;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param range the new range
	 */
	public RangeModificationEvent(ObjectSystemIdentifier raiser,int range)
	{
		super(raiser);
		this.range=range;
	}

	public int getRange()
	{
		return this.range;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+": Range becomes "+this.range;
	}


}
