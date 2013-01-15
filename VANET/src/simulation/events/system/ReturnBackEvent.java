package simulation.events.system;

import simulation.events.Event;
import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;

public class ReturnBackEvent extends Event{
	
	
	int previous;
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param previous 
	 */
	public ReturnBackEvent(ObjectSystemIdentifier raiser,int previous)
	{
		super(raiser);
		this.previous=previous;
		
	}

	

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
public String toString()
	{
		return "Object #"+getRaiser()+": a fait un retour arriere vers l'agent <<"+this.previous+">>";
	}


}
