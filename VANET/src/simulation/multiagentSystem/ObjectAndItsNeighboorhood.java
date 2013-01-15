package simulation.multiagentSystem;

import java.util.ArrayList;
import java.util.HashSet;

import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.multiagentSystem.ObjectUserIdentifier;

public class ObjectAndItsNeighboorhood {

	/** the object/agent */
	private SimulatedObject object;
	
	/** its neighboorhood */
	private HashSet<ObjectSystemIdentifier> neighboorhood;
	
	public ObjectAndItsNeighboorhood()
	{
		this.object=null;
		this.neighboorhood=null;
	}
	
	public ObjectAndItsNeighboorhood(SimulatedObject object)
	{
		this.object=object;
		this.neighboorhood=new HashSet<ObjectSystemIdentifier>();
	}
	
	public ObjectAndItsNeighboorhood(SimulatedObject object,HashSet<ObjectSystemIdentifier> neighboorhood)
	{
		this.object=object;
		this.neighboorhood=neighboorhood;
	}
	
	public SimulatedObject getSimulatedObject()
	{
		return this.object;
	}
	public HashSet<ObjectSystemIdentifier> getOriginalNeighboorhood()
	{
		return this.neighboorhood;
	}
	
	public HashSet<ObjectSystemIdentifier> getClonedNeighboorhood()
	{
		return (HashSet<ObjectSystemIdentifier>) this.neighboorhood.clone();
	}
	public void setNeighboorhood(HashSet<ObjectSystemIdentifier> neighboorhood)
	{
		this.neighboorhood=neighboorhood;
	}
	public String toString()
	{
		return "Object #"+this.object.getObject().getSystemId()+" has in its neighboorhood "+this.neighboorhood.toString();
	}
}
