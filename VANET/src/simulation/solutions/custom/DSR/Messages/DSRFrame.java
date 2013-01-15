package simulation.solutions.custom.DSR.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;

/**
 * Specialize the fram for a DSR agent 
 * @author Jean-Paul Jamont
 */
public class DSRFrame extends Frame{

	/** to signal a broadcasted frame */
	final public static int BROADCAST = -1;

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static String receiverIdToString(int id)
	{
		if (id==DSRFrame.BROADCAST)
			return "ALL_CLOSE_COMMUNICATING_OBJECT";
		else
			return ""+id;
	}

	/**
	 * parametrized constructor
	 * @param msg message flux 
	 */

	public static DSRFrame createDSR_Frame(byte[] msg)
	{

		ByteBuffer buf=ByteBuffer.wrap(msg);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte[] data = new byte[msg.length-8];
		for(int i=0;i<msg.length-8;i++) data[i]=buf.get();
		System.out.println("IN CREATE FRAME "+DSRMessage.debugByteArray(msg)+" -> "+DSRMessage.debugByteArray(data)+"  "+sender+"  "+receiver);
		return new DSRFrame(sender,receiver,data);

	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param msg
	 */
	public DSRFrame(int sender,int receiver,Message msg)
	{
		super(sender,receiver,msg);
	}
	
	/**
	 * parametrized constructor
	 * @param sender  sender identifier
	 * @param receiver receiver identifier
	 * @param bytes the message seen as a byte array 
	 */
	public DSRFrame(int sender,int receiver,byte[] msg)
	{
		super(sender,receiver,msg);
	}


	/**
	 * returns a string representation of the frame
	 * @return the string representation
	 */
		public String toString()
	{	
		int receiver = super.getReceiver();
		if (receiver==-1)
			return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is "+DSRMessage.createMessage(super.getData()).toString();
		else
			try
		{
				return "Frame from "+super.getSender()+" to "+super.getReceiver()+". Message is  "+DSRMessage.createMessage(super.getData()).toString();
		}
		catch(Exception e)
		{
			System.out.println("SENDER="+super.getSender()+"  DEST="+super.getReceiver());
			System.out.println("MESSAGE="+super.getData());
			e.printStackTrace();
			System.out.flush();
			System.exit(-1);
			return null;
		}
	}
}
