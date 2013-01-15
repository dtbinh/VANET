package simulation.events.user;
import simulation.messages.*;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when a message is sended 
 * @author Ha Hoang
 */
public final class SendedRecursiveMessageEvent extends RecursiveAgentEvent{


	/** the sended message */
	private Message message;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the sended message 
	 */
	public SendedRecursiveMessageEvent(ObjectSystemIdentifier raiser,int id, int level,Message msg)
	{
		//ObjectSystemIdentifier raiser,int id, int level,byte typeEvent
		super(raiser,id,level,RecursiveAgentEvent.SendedMessage);
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
		return "Agent ("+this.getId()+","+this.getLevel()+")/"+this.getRaiser().getId()+" Sended "+this.message.toString();
	}


}
