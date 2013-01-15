package simulation.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import simulation.solutions.custom.MWAC.Messages.MWACMessage;
import simulation.utils.BytesArray;

/** a frame 
 * @author Jean-Paul Jamont
 */
public class Frame implements Cloneable,Serializable {

	/** to signal a broadcasted frame */
	final public static int BROADCAST = -1;

	/** sender identifier */
	private int sender;

	/** receiver identifier */
	private int receiver;

	/** message */
	private byte[] data;

	public Frame(){}
	/**
	 * parametrized constructor
	 * @param sender  sender identifier
	 * @param receiver receiver identifier
	 * @param msg message
	 */
	public Frame(int sender,int receiver,Message msg)
	{
		this(sender,receiver,msg.toByteSequence());
	}

	/**
	 * parametrized constructor
	 * @param sender  sender identifier
	 * @param receiver receiver identifier
	 * @param bytes the message seen as a byte array 
	 */
	public Frame(int sender,int receiver,byte[] bytes)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.data=bytes;
	}


	/**
	 * parametrized constructor
	 * @param bytes the frame seen as a byte array 
	 */
	public Frame(byte[] byteSequence)
	{

		ByteBuffer buf = ByteBuffer.wrap(byteSequence);
		this.sender=buf.getInt();
		this.receiver=buf.getInt();
		this.data = new byte[byteSequence.length-8];
		for (int i = 0; i < byteSequence.length-8; i++) this.data[i] = buf.get(); 
	}

	/**
	 * parametrized constructor
	 * @param bytes the frame seen as a byte array 
	 */
	public Frame(BytesArray byteSequence)
	{
		this(byteSequence.array);
	}

	/**
	 * getter of the sender identifier
	 * @return the sender identifier
	 */
	public int getSender()
	{
		return this.sender;
	}

	/**
	 * getter of the receiver identifier
	 * @return the receiver identifier
	 */
	public int getReceiver()
	{
		return this.receiver;
	}

	/**
	 * getter of the message
	 * @return the message
	 */
	public byte[] getData()
	{
		return this.data;
	}
	
	/**
	 * getter of the message
	 * @return the message
	 */
	public Message getMessage()
	{
		System.err.println("\n!!! Your own Message class must implements method \"getMessage\" when you use real world embedded agents");
		return null;
	}
	
	/**
	 * returns the volume of the frame in bytes
	 * @return the number of bytes of the frame
	 */
	public int getVolume()
	{
		return toByteSequence().length;
	}

	/**
	 * allow to convert the frame in a bytes sequence
	 * @return the bytes sequence
	 */

	public byte[] toByteSequence()
	{
		return ByteBuffer.allocate(8+data.length).putInt(this.sender).putInt(this.receiver).put(data).array();
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


	/** returns the volume of the message
	 * @return the volume of the message (bytes)
	 */	
	final public int volume()
	{
		return this.toByteSequence().length;
	}

	/**
	 * returns a string representation of the frame
	 * @return the string representation
	 */
	public String toString()
	{
		if(data==null)
			return "Frame from "+sender+" to "+receiver+" contains nothing";
		else if(receiver==Frame.BROADCAST)
			return "Frame from "+sender+" to "+receiver+" contains "+data.toString();
		else 
			return "Frame from "+sender+" to all surrounding neighboors contains "+data.toString();
	}

	public boolean isSurrondingMessage(int id)
	{
		return !(this.receiver>0 && this.receiver==id);
	}

}
