package simulation.multiagentSystem;

import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.multiagentSystem.ObjectUserIdentifier;
import simulation.utils.IntegerPosition;
import simulation.entities.Agent;
import simulation.entities.Object;
import simulation.events.*;
import simulation.events.system.PositionModificationEvent;

/**
 * The simulated object 
 * @author Jean-Paul Jamont
*/
public class SimulatedObject{
	/** the object */
	protected Object object;
	/** the position of the simulated object */
	protected IntegerPosition position;

	/** Constructs the simulated object
	 * @param obj the object which is simulated
	 * @param pos the position of the simulated object
	 */
	public SimulatedObject(Object obj, IntegerPosition pos)
	{
		this.object = obj;
		this.position = pos;
		//obj.getMAS().notifyEvent(new  PositionModificationEvent(obj.getId(),pos.x,pos.y));
	}

	/** Constructs the simulated object
	 * @param obj the object which is simulated
	 * @param x absissa of the position of the simulated object
	 * @param y ordinate of the position of the simulated object
	 */
	public SimulatedObject(Object obj, int x, int y)
	{
		this(obj,new IntegerPosition(x,y));
	}

	/** allows to know if the object is or not an agent
	 * @return true if the current object is an agent
	 */
	public boolean isAgent()
	{
		return object instanceof Agent;
	}

	/** allows to know the position of the current object 
	 * @return the position of the current object 
	 */
	public IntegerPosition getPosition()
	{
		return this.position;
	}

	/** Allows to know the mas 
	 * return a reference to the MAS
	 */
	private MAS getMAS()
	{
		return object.getMAS();
	}

	/** sets the position of the current object 
	 * @param newPosition the position of the current object 
	 */
	public void setPosition(IntegerPosition newPosition)
	{
		this.position=newPosition;
		this.getMAS().notifyEvent(new PositionModificationEvent(object.getSystemId(),newPosition));
	}

	/** allows to know the reference of the not simulated object 
	 * @return a reference of the object
	 */
	public Object getObject()
	{
		return object;
	}

	/** allows to if the object is in the range of another object
	 * @param sObj the sender agent
	 * @return true if the current object is in the range of the simulated object
	 */
	public boolean inRange(SimulatedObject sObj)
	{
		//System.out.println("RANGE: ("+this.getObject().getId()+")@"+this.getPosition()+"  to  ("+sObj.getObject().getId()+")@"+sObj.getPosition()+"  DISTANCE="+this.position.distance(sObj.getPosition())+" RANGE="+this.getObject().getRange()+"/"+sObj.getObject().getRange());
		return (this.position.distance(sObj.getPosition()) < sObj.getObject().getRange());
	}

	/** returns the string signature of the simulated object
	 * @return the string signature of the simulated object
	 */
	public String toString()
	{
		return "Simulated object "+this.object.getUserId()+" at position "+this.position.toString();
	}


}
