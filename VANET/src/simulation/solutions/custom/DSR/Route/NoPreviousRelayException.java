package simulation.solutions.custom.DSR.Route;
/**
 * No previous relay exception 
 * @author Jean-Paul Jamont
 */

/** exception raised when in a DSR route there is not a relay before another one*/
public class NoPreviousRelayException  extends Exception{

	public NoPreviousRelayException()
	{
		super();
	}
}
