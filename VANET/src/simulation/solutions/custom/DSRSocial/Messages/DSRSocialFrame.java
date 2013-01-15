package simulation.solutions.custom.DSRSocial.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;

public class DSRSocialFrame extends Frame{

	/** to signal a broadcasted frame */
	final public static int BROADCAST = -1;

	public static String receiverIdToString(int id)
	{
		if (id==DSRSocialFrame.BROADCAST)
			return "ALL_CLOSE_COMMUNICATING_OBJECT";
		else
			return ""+id;
	}

	public static DSRSocialFrame createDSR_Frame(byte[] msg)
	{

		ByteBuffer buf=ByteBuffer.wrap(msg);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte[] data = new byte[msg.length-8];
		for(int i=0;i<msg.length-8;i++) data[i]=buf.get();
		System.out.println("IN CREATE FRAME "+DSRSocialMessage.debugByteArray(msg)+" -> "+DSRSocialMessage.debugByteArray(data)+"  "+sender+"  "+receiver);
		return new DSRSocialFrame(sender,receiver,data);

	}
	public DSRSocialFrame(int sender,int receiver,Message msg)
	{
		super(sender,receiver,msg);
	}
	public DSRSocialFrame(int sender,int receiver,byte[] msg)
	{
		super(sender,receiver,msg);
	}



	public String toString()
	{	
		int receiver = super.getReceiver();
		if (receiver==-1)
			return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is "+DSRSocialMessage.createMessage(super.getData()).toString();
		else
			try
		{
				return "Frame from "+super.getSender()+" to "+super.getReceiver()+". Message is  "+DSRSocialMessage.createMessage(super.getData()).toString();
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
