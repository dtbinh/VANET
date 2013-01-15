package simulation.events.system;

import simulation.embeddedObject.serialCommunication.RS232;
import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;


/** event notified when a frame is received 
 * @author Jean-Paul Jamont
 */
public class SendedBytesByEmbeddedObjectEvent extends Event{

	/** the received frame*/
	private byte[] bytes;
	/** bytes signification */
	private String signification;
	/** is understanble? */
	private boolean isUnderstandable;
	
	
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param frame the sended frame
	 */
	public SendedBytesByEmbeddedObjectEvent(ObjectSystemIdentifier raiser,byte[] bytes,String signification)
	{
		this(raiser,bytes,signification,true);
	}
	/** basic constructor 
	 * @param raiser identifier of the event raiser
	 * @param frame the sended frame
	 */
	public SendedBytesByEmbeddedObjectEvent(ObjectSystemIdentifier raiser,byte[] bytes,String signification,boolean isUnderstandable)
	{
		super(raiser);
		this.bytes = new byte[bytes.length];
		for(int i=0;i<bytes.length;i++) this.bytes[i]=bytes[i];
		this.signification=signification;
		this.isUnderstandable=isUnderstandable;
	
	}


	/**
	 * is the bytes understandable
	 * @return true if the bytes can be translated into a understandable frame
	 */
	public boolean isUnderstandable()
	{
		return this.isUnderstandable;
	}
	
	/**
	 * returns the sended bytes
	 * @return the sended bytes
	 */
	public byte[] getBytes()
	{
		return this.bytes;
	}
	
	/**
	 * returns the signification of the received bytes
	 * @return the signification
	 */
	public String getSignification()
	{
		return this.signification;
	}
	

	/** Returns a string representation of the object 
	 * @return a string representation of the event
	 */
	public String toString()
	{
		return "Object "+getRaiser()+": Sended bytes "+RS232.debugByteArray(this.bytes);
	}


}
