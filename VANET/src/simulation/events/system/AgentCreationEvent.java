package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when an agent is created 
 * @author Jean-Paul Jamont
 */
public class AgentCreationEvent  extends Event{

	/* identifier given by user */
	private int user_id;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public AgentCreationEvent(ObjectSystemIdentifier raiser)
	{
		this(raiser,-1);
	}
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public AgentCreationEvent(ObjectSystemIdentifier raiser,int user_id)
	{
		super(raiser);
		this.user_id=user_id;
	}

	/**
	 * returns the user id of the agent
	 * @return the user identifier
	 */
	public int getUserId()
	{
		return this.user_id;
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
		return "Agent #"+super.getRaiser().getId()+" is created : sid="+super.getRaiser().getId()+"  uid="+this.user_id;
	}

}


