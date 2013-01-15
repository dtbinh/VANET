package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

public class UsedAgentEvent extends Event{
	
	public UsedAgentEvent(ObjectSystemIdentifier agent)
			
	{
		super (agent);
		
	}
	
	public String toString()
	{
		return "Object #"+getRaiser()+": is used in the simulation ";
	}

}
