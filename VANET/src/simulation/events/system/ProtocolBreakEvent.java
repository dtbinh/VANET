package simulation.events.system;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

public class ProtocolBreakEvent extends Event{

	
	public ProtocolBreakEvent(ObjectSystemIdentifier raiser)
	{
		super(raiser);
	}
	
	public String toString()
	{
		return "Object #"+getRaiser()+": Protocol break detected";
	}
}
