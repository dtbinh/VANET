package simulation.events.system;
import java.awt.Color;

import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when the color of an agent/object is modified 
 * @author Jean-Paul Jamont
 */
public final class ColorModificationEvent extends Event{

	/** the new color */
	private int color;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param color the new color
	 */
	public ColorModificationEvent(ObjectSystemIdentifier raiser,Color color)
	{
		super(raiser);
		this.color=(color.getRed()+(color.getGreen()<<8)+(color.getBlue()<<16));
	}

	public Color getColor()
	{
		return new Color(color);
	}
	public int getRVBColor()
	{
		return this.color;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object #"+getRaiser()+": Color become "+Integer.toString(this.color,16);
	}

}

