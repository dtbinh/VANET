package simulation.events.system;

import simulation.events.Event;
import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.AntMWAC.Ant.Messages.AntMessage_Forward;

public class SendedAntEvent extends Event {
	/** the Sended message */
	private AntMessage_Forward message;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param msg the Sended message
	 */
	public SendedAntEvent(ObjectSystemIdentifier raiser,AntMessage_Forward msg)
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
		return "Object #"+getRaiser()+": Send Ant "+this.message.toString();
	}

}
