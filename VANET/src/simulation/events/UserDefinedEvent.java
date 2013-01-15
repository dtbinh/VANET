package simulation.events;

import simulation.multiagentSystem.ObjectSystemIdentifier;

/** to allow an user to define its own events
 * @author Jean-Paul Jamont
 */
public class UserDefinedEvent  extends Event{

	
	/** constructs an event
	 * 
	 * @param raiser identifier of the object which raise the event
	 */
	public UserDefinedEvent(ObjectSystemIdentifier raiser)
	{
		super(raiser);
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event*/
	public String toString()
	{
		return "User undefined event";
	}
}
