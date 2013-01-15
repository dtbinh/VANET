package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when an agent is created 
 * @author Jean-Paul Jamont
 */
public class RecMAS_AbstractionCreationEvent  extends Event{

	/* level of abstraction */
	private int level;
	/* identifier given by user */
	private int abstractionId;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public RecMAS_AbstractionCreationEvent(ObjectSystemIdentifier raiser)
	{
		this(raiser,-1,-1);
	}
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public RecMAS_AbstractionCreationEvent(ObjectSystemIdentifier raiser,int abstractionId,int level)
	{
		super(raiser);
		this.abstractionId=abstractionId;
		this.level=level;
	}

	/**
	 * returns the user id of the abstracted agent
	 * @return the user identifier
	 */
	public int getAbstractionId()
	{
		return this.abstractionId;
	}
	
	/**
	 * returns the level of the abstracted agent
	 * @return the level 
	 */
	public int getLevel()
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
		return "Abstract Agent (id="+this.abstractionId+",lvl="+this.level+") is created.";
	}

}


