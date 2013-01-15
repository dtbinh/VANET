package simulation.events.user;

import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when a recursive message is received 
 * @author Ha Hoang
 */
public final class  ReceivedRecursiveMessageEvent extends RecursiveAgentEvent{


	/** the received message */
	private Message message;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the received message
	 */
	public ReceivedRecursiveMessageEvent(ObjectSystemIdentifier raiser,int id, int level,Message msg)
	{
		//ObjectSystemIdentifier raiser,int id, int level,byte typeEvent
		super(raiser,id,level,RecursiveAgentEvent.ReceivedMessage);
		this.message=msg;
		this.setId(id);
		this.setLevel(level);
	}

	public Message getMessage()
	{
		return this.message;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
public String toString()
	{
		return "Agent ("+this.getId()+","+this.getLevel()+")/"+this.getRaiser().getId()+" Received "+this.message.toString();
	}



}
