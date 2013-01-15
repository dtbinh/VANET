package simulation.events.system;
import simulation.events.Event;
import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event raised when a message has not been received by a receiver
 * @author Jean-Paul Jamont
 */
public final class  MessageNotTransmittedEvent extends Event{

	/** the not transmitted message */
	private Message message;
	/** the string representation of the error */
	private String error;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the not transmitted message
	 * @param error a string representation of the error
	 */
	public MessageNotTransmittedEvent(ObjectSystemIdentifier raiser,Message msg,String error)
	{
		super(raiser);
		this.message=msg;
		this.error=error;
	}

	public Message getMessage()
	{
		return this.message;
	}

	public String getError()
	{
		return this.error;
	}


	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+" cannot transmit a message. [Message="+this.message.toString()+"  ,  Error="+this.error+"]";
	}


}
