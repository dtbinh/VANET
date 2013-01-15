package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when an agent died 
 * @author Jean-Paul Jamont
 */
public class AgentDeathEvent  extends Event{

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public AgentDeathEvent(ObjectSystemIdentifier raiser)
	{
		super(raiser);
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Agent #"+getRaiser()+" is death";
	}

}


