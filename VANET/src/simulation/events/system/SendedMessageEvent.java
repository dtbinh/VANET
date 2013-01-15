package simulation.events.system;
import simulation.events.Event;
import simulation.messages.*;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when a message is sended 
 * @author Jean-Paul Jamont
 */
public final class SendedMessageEvent extends Event{

	/** the sended message */
	private Message message;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the sended message 
	 */
	public SendedMessageEvent(ObjectSystemIdentifier raiser,Message msg)
	{
		super(raiser);
		this.message=msg;
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
		return "Object #"+getRaiser()+": Sended message "+this.message.toString();
	}


}
