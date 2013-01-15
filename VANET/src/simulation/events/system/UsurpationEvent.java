package simulation.events.system;

import simulation.events.UserDefinedEvent;
import simulation.multiagentSystem.ObjectSystemIdentifier;




/** event raised at each energy modification 
 * @author Jean-Paul Jamont
 */
public final class UsurpationEvent extends UserDefinedEvent{

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param energyLevel the new energy level
	 */
	public UsurpationEvent(ObjectSystemIdentifier raiser)
	{
		super(raiser);
	}


	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+": Usurpation detected";
	}

}
