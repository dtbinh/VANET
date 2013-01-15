package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when an agent is created 
 * @author Jean-Paul Jamont
 */
public class SystemExceptionEvent  extends Event{

	/* identifier given by user */
	private String message;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public SystemExceptionEvent(ObjectSystemIdentifier raiser,String message)
	{
			super(raiser);
			this.message=message;
	}
	
	/**
	 * returns the user id of the agent
	 * @return the user identifier
	 */
	public String getMessage()
	{
		return this.message;
	}
		
	/**
	 * returns the user id of the agent
	 * @return the user identifier
	 */
	public ObjectSystemIdentifier getSystemId()
	{
		return super.getRaiser();
	}
	
	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Agent #"+super.getRaiser().getId()+" : SYSTEM ERROR DETECTED : "+this.message ;
	}

}


