package simulation.events.system;
import simulation.events.Event;
import simulation.messages.*;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when a frame is sended 
 * @author Jean-Paul Jamont
 */
public final class SendedFrameEvent extends Event{

	/** the sended frame */
	private Frame frame;

	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param frame the sended frame
	 */
	public SendedFrameEvent(ObjectSystemIdentifier raiser,Frame frame)
	{
		super(raiser);
		this.frame=frame;
	}

	/**
	 * getter of the frame
	 * @return a reference to the frame
	 */
	public Frame getFrame()
	{
		return this.frame;
	}

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
public String toString()
	{
		return "Object "+getRaiser()+": Sended frame "+this.frame.toString();
	}


}
