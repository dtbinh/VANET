package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;

public class MWACSocialFrame extends Frame{

	/** to signal a link agent broadcasted frame */
	final public static int BROADCAST_LINK = -2;
	/** to signal a representative agent broadcasted frame */
	final public static int BROADCAST_REPRESENTATIVE = -3;

	public static String receiverIdToString(int id)
	{
		switch(id)
		{
		case BROADCAST_LINK: return "ALL_CLOSE_LINK_AGENT";
		case BROADCAST_REPRESENTATIVE: return "ALL_CLOSE_REPRESENTATIVE_AGENT";
		default: return ""+id;
		}
	}

	public static MWACSocialFrame createASTRO_Frame(byte[] msg)
	{
		
		ByteBuffer buf=ByteBuffer.wrap(msg);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte[] data = new byte[msg.length-8];
		for(int i=0;i<msg.length-8;i++) data[i]=buf.get();
		System.out.println("IN CREATE FRAME "+MWACSocialMessage.debugByteArray(msg)+" -> "+MWACSocialMessage.debugByteArray(data)+"  "+sender+"  "+receiver);
		return new MWACSocialFrame(sender,receiver,data);

	}
	public MWACSocialFrame(int sender,int receiver,Message msg)
	{
		super(sender,receiver,msg);
	}
	public MWACSocialFrame(int sender,int receiver,byte[] msg)
	{
		super(sender,receiver,msg);
	}
	
	
	public String toString()
	{	
		int receiver = super.getReceiver();
		if (receiver==-1)
			return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is "+MWACSocialMessage.createMessage(super.getData()).toString();
		else
			try
		{
				return "Frame from "+super.getSender()+" to "+super.getReceiver()+". Message is  "+MWACSocialMessage.createMessage(super.getData()).toString();
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
