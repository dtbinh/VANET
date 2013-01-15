package simulation.events.system;

import simulation.events.Event;
import simulation.messages.Message;
import simulation.multiagentSystem.ObjectSystemIdentifier;

public class SolicitsAgentEvent extends Event{
	
	private int Cpt;
   
	/** basic constructor 
	 * @param raiser identifier of the event raiser 
	 */
	
	public SolicitsAgentEvent(ObjectSystemIdentifier raiser, int cpt)
	{
		super(raiser);
		this.Cpt=cpt;
		//System.out.println(" je suis passé par la méthode SolicitsAgentEvent et cpt == "+this.Cpt);
		
	}

	public int get_cpt()
	{
		return this.Cpt;
	}

	public String toString()
	{
		return "Agent #"+super.getRaiser().getId()+" is solicited : "+this.Cpt +"times";
	}


}
