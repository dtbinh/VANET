package simulation.solutions.custom.RecMAS.MWAC.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.solutions.custom.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage;
import simulation.utils.BytesArray;

/**
 * MWAC specialized frame 
 * @author Jean-Paul Jamont
 */

public class MWACFrame extends Frame {

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
	public static MWACFrame createFrameFromBytes(BytesArray msg)
	{
		return MWACFrame.createFrameFromBytes(msg.array);
	}
	public static MWACFrame createFrameFromBytes(byte[] msg)
	{

		ByteBuffer buf=ByteBuffer.wrap(msg);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte[] data = new byte[msg.length-8];
		for(int i=0;i<msg.length-8;i++) data[i]=buf.get();

		MWACFrame createdFrame=new MWACFrame(sender,receiver,data);
		System.out.print("\n*!*IN CREATE FRAME "+MWACMessage.debugByteArray(msg)+" -> "+MWACMessage.debugByteArray(data)+"  sender="+sender+"  receiver="+receiver+"  interpretation="+(createdFrame==null ? "!!!Not understanble!!!" : createdFrame.toString()));


		return createdFrame;
	}

	public MWACFrame(int sender,int receiver,Message msg)
	{
		super(sender,receiver,msg);
	}
	public MWACFrame(int sender,int receiver,byte[] msg)
	{
		super(sender,receiver,msg);
	}


	public String toString()
	{	
		int receiver = super.getReceiver();
		if (receiver==-1)
		{
			try
			{
				return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is "+MWACMessage.createMessage(super.getData()).toString();
			}
			catch(java.lang.NullPointerException ex1)
			{
				try
				{
					return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is "+RecMASMessage.createMessage(super.getData()).toString();
				}
				catch(java.lang.NullPointerException ex2)
				{
					return "Frame from "+super.getSender()+" to all surrounding neighboors. Message is not a MWAC/RecMAS2MWAC applicative message "+simulation.utils.BytesArray.displayByteArray(super.getData());
				}
			}
		}
		else
		{
			try
			{
				MWACMessage nMsg = MWACMessage.createMessage(super.getData());
				return "Frame from "+super.getSender()+" to "+super.getReceiver()+". Message is  "+(nMsg==null ? "!!!not understandable!!!" : nMsg.toString());
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

	public MWACMessage getMessage()
	{
		return MWACMessage.createMessage(this.getData());
	}
}
