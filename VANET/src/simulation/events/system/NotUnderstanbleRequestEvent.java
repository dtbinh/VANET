package simulation.events.system;

import java.awt.Color;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;

public class NotUnderstanbleRequestEvent extends Event{


	private int sender;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param color the new color
	 */
	public NotUnderstanbleRequestEvent(ObjectSystemIdentifier raiser,int sender)
	{
		super(raiser);
		this.sender=sender;
	}

	public int getSenderOfTheNotUnderstanbleRequest()
	{
		return this.sender;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+": I listen an not understanble request from "+this.sender;
	}
}