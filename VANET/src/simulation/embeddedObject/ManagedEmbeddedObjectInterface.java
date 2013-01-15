package simulation.embeddedObject;

import simulation.*;
import simulation.embeddedObject.serialCommunication.FrameReceivedInterface;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * specify the services which must be supplied by an embedded object manager 
 * @author Jean-Paul Jamont
 */
public interface ManagedEmbeddedObjectInterface {

	
	
	/** 
	 * get the identifier of the managed embedded object 
	 * @return the identifier
	 */
	public ObjectSystemIdentifier getId();
	
	/** 
	 * open connection
	 */
	public void open();

	/** 
	 * close  connection
	 */
	public void close();
	
	/**
	 * set the receiver
	 */
	public void setReceiver(FrameReceivedInterface receiver);
	/** 
	 * get the number of frame which has been received to the managed embedded object 
	 * @return the number of frame which wait to be processed
	 */
	public int getNumberOfWaitingFrame();

	/** 
	 * get the number of frame which has been received to the managed embedded object 
	 * @return the frame (bytes representation)
	 */
	public byte[] getReceivedFrame();
	
	/** 
	 * send a frame
	 * @param frm the frame which must be send  
	 */
	public boolean sendFrame(byte[] frm);

	//public simulation.Object getObjectReference();

}
