package simulation.events;

import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.multiagentSystem.ObjectUserIdentifier;
import simulation.utils.aDate;

import java.io.Serializable;

/** This class is used by the simulator to compute the statistics, the spy windows... 
 * @author Jean-Paul Jamont
 */
public class Event implements Serializable{


	/** Identifier of the object which has raised the event */
	private ObjectSystemIdentifier raiser;
	/** instance date of this event */
	private long date;


	/** constructs the <it>idle</it> debug event */
	public Event()
	{
		this(null);
	}
	/** constructs an event
	 * 
	 * @param raiser identifier of the object which raise the event
	 */
	public Event(ObjectSystemIdentifier raiser)
	{
		this.raiser=raiser;
		this.date= new aDate().getTimeInMillis();
	}

	/** returns the identifier of the object which raise the event
	 * @return identifier of the event raiser
	 */
	public ObjectSystemIdentifier getRaiser()
	{
		return this.raiser;
	}

	/** returns the instance date of the event
	 * @return the instance date of the event
	 */
	public aDate getDate()
	{
		return new aDate(this.date);
	}

	/** returns the instance date in ms of the event
	 * @return the instance date in ms of the event
	 */
	public long getDateInMs()
	{
		return this.date;
	}

	/** reserved
	 */
	public void alignDate(long correction)
	{
		this.date=correction;
	}

	/** returns a string representation of the event (seen by simulator user)
	 * @return a string representation
	 */
	public String toString()
	{
		return "Idle event";
	}

}
