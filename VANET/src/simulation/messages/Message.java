package simulation.messages;

import java.io.Serializable;
import java.lang.Cloneable;
import java.nio.ByteBuffer;

/** A message 
 * @author Jean-Paul Jamont
 */
public abstract class Message implements Cloneable,Serializable
{

	
	/** receiver */
	public abstract int getReceiver();
	
	/** sender */
	public abstract int getSender();
	
	/** returns the byte representation of this message
	 * @return the byte representation
	 */	
	public byte[] toByteSequence(){return null;}
	
	/** returns the volume of the message
	 * @return the volume of the message (bytes)
	 */	
	final public int volume()
	{
		return toByteSequence().length;
	}
	
	/** returns true if the two messages are the same (same sender, receiver and data)
	 * @return true if the messages have the same sender, the same receiver and the same data
	 */	
	public final boolean isSameMessageThan(Message msg)
	{
		byte[] data1 = this.toByteSequence();
		byte[] data2 = msg.toByteSequence();
		
		if(data1.length!=data2.length) return false;

		for(int i=0;i<data1.length;i++)
			if(data1[i]!=data2[i]) return false;
		
		return true;
		
	}
	/**
	 * allows to clone the object
	 * @return reference to the cloned object
	 */
	public Object clone() 
	{
		try 
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			return null;
		}
	}
	
	/**
	 * returns a string representation of the message
	 @return the string representation
	 */
	public String toString()
	{
		return "Message class???";
	}

}
