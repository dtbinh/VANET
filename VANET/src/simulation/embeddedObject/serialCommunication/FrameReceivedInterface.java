package simulation.embeddedObject.serialCommunication;

import simulation.multiagentSystem.ObjectSystemIdentifier;

/** This class represents an object which is able to receive frame from the serial interface 
 * @author Jean-Paul Jamont
 */
public interface FrameReceivedInterface
{
	/** method which must compute the frame
	 * @param sys_id id of the receiver
	 * @param bytes array of the frame bytes' 
	 */
	public void receivedBytes(ObjectSystemIdentifier sys_id,byte[] bytes);
}