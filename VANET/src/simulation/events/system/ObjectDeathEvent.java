package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event raised at the creation of each object 
 * @author Jean-Paul Jamont
 */
public class ObjectDeathEvent extends Event{


	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public ObjectDeathEvent(ObjectSystemIdentifier raiser)
	{
		super(raiser);
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+" is destructed";
	}
}



