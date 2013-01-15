package simulation.events.system;

import simulation.events.Event;
import simulation.events.UserDefinedEvent;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when an agent is created 
 * @author Jean-Paul Jamont
 */
public class UserIdentifierModificationEvent  extends UserDefinedEvent{

	/* new identifier given by user */
	private int new_user_id;
	/* new identifier given by user */
	private int old_user_id;


	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public UserIdentifierModificationEvent(ObjectSystemIdentifier raiser,int old_user_id,int new_user_id)
	{
		super(raiser);
		this.old_user_id=old_user_id;
		this.new_user_id=new_user_id;
	}
	

	/**
	 * returns the new user id of the agent
	 * @return the new user identifier
	 */
	public int getNewUserId()
	{
		return this.new_user_id;
	}
	
	/**
	 * returns the old user id of the agent
	 * @return the old user identifier
	 */
	public int getOldUserId()
	{
		return this.new_user_id;
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
		return "Agent #"+super.getRaiser().getId()+" user identifier modified : sid="+super.getRaiser().getId()+"  new uid="+this.new_user_id+"  (old uid was"+this.new_user_id+")";
	}

}


