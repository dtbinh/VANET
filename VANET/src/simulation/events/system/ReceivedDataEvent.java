package simulation.events.system;

import simulation.events.Event;
import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACMessage_Data;

public class ReceivedDataEvent extends Event {
	
	/** the received message data
	 *  */
	private MWACMessage_Data message;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the receiveded message
	 */
	public ReceivedDataEvent(ObjectSystemIdentifier raiser,MWACMessage_Data msg)
	{  
		super(raiser);
		this.message=msg;
	}
   
	public MWACMessage_Data getMessage()
	{
		return this.message;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
public String toString()
	{
		return "Object #"+getRaiser()+": received data  "+this.message.toString();
	}

}
