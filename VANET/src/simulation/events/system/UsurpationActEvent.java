package simulation.events.system;

import simulation.events.UserDefinedEvent;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when an agent is created 
 * @author Jean-Paul Jamont
 */
public class UsurpationActEvent  extends UserDefinedEvent{

	/* new identifier given by user */
	private int real_user_id;
	/* new identifier given by user */
	private int usurped_user_id;


	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public UsurpationActEvent(ObjectSystemIdentifier raiser,int real_user_id,int usurped_user_id)
	{
		super(raiser);
		this.real_user_id=real_user_id;
		this.usurped_user_id=usurped_user_id;
	}
	

	/**
	 * returns the usurped user id of the agent
	 * @return the usurped user identifier
	 */
	public int getUsurpedUserId()
	{
		return this.usurped_user_id;
	}
	
	/**
	 * returns the real user id of the agent
	 * @return the real user identifier
	 */
	public int getRealUserId()
	{
		return this.real_user_id;
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
		return "Agent #"+super.getRaiser().getId()+" user identifier modified : sid="+super.getRaiser().getId()+"uid="+this.real_user_id+"  has usurped the uid"+this.usurped_user_id;
	}

}


