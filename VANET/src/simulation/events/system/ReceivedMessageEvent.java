package simulation.events.system;
import simulation.events.Event;
import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when a message is received 
 * @author Jean-Paul Jamont
 */
public final class  ReceivedMessageEvent extends Event{

	/** the received message */
	private Message message;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the received message
	 */
	public ReceivedMessageEvent(ObjectSystemIdentifier raiser,Message msg)
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
		return "Object #"+getRaiser()+": Received message "+this.message.toString();
	}


}
