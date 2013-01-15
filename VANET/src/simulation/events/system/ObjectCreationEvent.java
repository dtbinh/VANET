package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event raised at the creation of each object 
 * @author Jean-Paul Jamont
 */
public class ObjectCreationEvent  extends Event{

	/* identifier given by user */
	private int user_id;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public ObjectCreationEvent(ObjectSystemIdentifier raiser)
	{
		this(raiser,-1);
	}
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 */
	public ObjectCreationEvent(ObjectSystemIdentifier raiser,int user_id)
	{
		super(raiser);
		this.user_id=user_id;
	}

	/**
	 * returns the user id of the object
	 * @return the user identifier
	 */
	public int getUserId()
	{
		return this.user_id;
	}
	
	/**
	 * returns the user id of the object
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
		return "Object #"+super.getRaiser().getId()+" is created : sid="+super.getRaiser().getId()+"  uid="+this.user_id;
	}

}


