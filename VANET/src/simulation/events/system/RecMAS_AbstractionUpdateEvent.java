package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.RecMAS.MWAC.MWACAgent;

/** event notified when an agent is created 
 * @author Jean-Paul Jamont
 */
public class RecMAS_AbstractionUpdateEvent  extends Event{

	/* level of abstraction */
	private int level;
	/* identifier given by user */
	private int abstractionId;
	/* role of the abstraction */
	private byte role;
	/* aggregated agent list */
	private int[] aggregatedList;
	
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public RecMAS_AbstractionUpdateEvent(ObjectSystemIdentifier raiser)
	{
		this(raiser,-1,-1,(byte) 0xFF,new int[0]);
	}
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public RecMAS_AbstractionUpdateEvent(ObjectSystemIdentifier raiser,int abstractionId,int level,byte role,int[] aggregatedList)
	{
		super(raiser);
		this.abstractionId=abstractionId;
		this.level=level;
		this.role=role;
		this.aggregatedList=MWACRouteAssistant.cloneRoute(aggregatedList);
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
	
	
	public byte getRole()
	{
		return this.role;
	}
	
	public int[] getAggregatedList()
	{
		return this.aggregatedList;
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
		return "Abstract Agent (id="+this.abstractionId+",lvl="+this.level+") is updated (role="+MWACAgent.roleToString(this.role)+","+MWACRouteAssistant.routeToString(this.aggregatedList)+").";
	}

}


