package simulation.events.system;
import java.util.HashSet;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when a neighboorhood is modified 
 * @author Jean-Paul Jamont
 */
public final class NeighboorhoodModificationEvent extends Event{

	/** the neighboorhood */
	private HashSet<Integer> neighboorhood;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param neighboorhood the new neighboorhood
	 */
	public NeighboorhoodModificationEvent(ObjectSystemIdentifier raiser,HashSet<Integer> neighboorhood)
	{
		super(raiser);
		this.neighboorhood=neighboorhood;
	}

	public HashSet<Integer> getNeighboorhood()
	{
		return this.neighboorhood;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+": Neighboorhood becomes "+this.neighboorhood.toString();
	}

}



