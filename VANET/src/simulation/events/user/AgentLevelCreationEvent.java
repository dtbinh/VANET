package simulation.events.user;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when an agent is created 
 * @author Jean-Paul Jamont
 */
public class AgentLevelCreationEvent  extends Event{

	/* identifier given by user */
	private int level;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public AgentLevelCreationEvent(ObjectSystemIdentifier raiser)
	{
		this(raiser,-1);
	}
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public AgentLevelCreationEvent(ObjectSystemIdentifier raiser,int level)
	{
		super(raiser);
		this.level=level;
	}

	/**
	 * returns the user id of the agent
	 * @return the user identifier
	 */
	public int getUserId()
	{
		return this.level;
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
		return "Agent #"+super.getRaiser().getId()+" create a new level : level="+this.level;
	}



}
