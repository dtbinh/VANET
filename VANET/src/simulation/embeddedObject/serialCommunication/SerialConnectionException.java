package simulation.embeddedObject.serialCommunication;

/** exception raised bue the serial connection
 * @author Jean-Paul Jamont
 */
public class SerialConnectionException extends Exception
{
	/**
	 * exception used when a exception occurs over a serial connection
	 * @param msg the exception message 
	 */
	public SerialConnectionException(String msg)
	{
		super(msg);
	}
}