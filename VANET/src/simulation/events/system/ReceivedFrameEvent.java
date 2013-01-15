package simulation.events.system;
import simulation.events.Event;
import simulation.messages.*;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/** event notified when a frame is received 
 * @author Jean-Paul Jamont
 */
public final class  ReceivedFrameEvent extends Event{

	/** the received frame*/
	private Frame frame;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param frame the received frame
	 */
	public ReceivedFrameEvent(ObjectSystemIdentifier raiser,Frame frame)
	{
		super(raiser);
		this.frame=frame;
	}

	public Frame getFrame()
	{
		return this.frame;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object "+getRaiser()+": Received frame "+this.frame.toString();
	}


}
