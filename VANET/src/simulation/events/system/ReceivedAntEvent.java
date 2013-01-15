package simulation.events.system;

import simulation.events.Event;
import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.AntMWAC.Ant.Messages.AntMessage_Forward;

public class ReceivedAntEvent extends Event {

	/** the received message */
	private AntMessage_Forward message;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the received message
	 */
	public ReceivedAntEvent(ObjectSystemIdentifier raiser,AntMessage_Forward msg)
	{
		super(raiser);
		this.message=msg;
	}

	public AntMessage_Forward getMessage()
	{
		return this.message;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
public String toString()
	{
		return "Object #"+getRaiser()+": Received Ant "+this.message.toString();
	}

}
