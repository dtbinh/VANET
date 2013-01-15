package simulation.events;

import simulation.multiagentSystem.ObjectSystemIdentifier;


/**
 * 
 * @author JPeG
 *
 */
public class ProtocolBreakEvent extends Event{

	/**
	 * 
	 * @param raiser
	 */
	public ProtocolBreakEvent(ObjectSystemIdentifier raiser)
	{
		super(raiser);
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+": Protocol break detected";
	}
}
