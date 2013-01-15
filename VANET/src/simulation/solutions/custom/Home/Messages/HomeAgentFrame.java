package simulation.solutions.custom.Home.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;

/**
 * Specialize the fram for a DSR agent 
 * @author Jean-Paul Jamont
 */
public class HomeAgentFrame extends Frame{

	/** to signal a broadcasted frame */
	final public static int BROADCAST = -1;

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static String receiverIdToString(int id)
	{
		if (id==HomeAgentFrame.BROADCAST)
			return "ALL_CLOSE_COMMUNICATING_OBJECT";
		else
			return ""+id;
	}

	/**
	 * parametrized constructor
	 * @param msg message flux 
	 */

	public static HomeAgentFrame createFrame(byte[] msg)
	{

		System.out.println("IN CREATE FRAME "+HomeAgentMessage.debugByteArray(msg));
		ByteBuffer buf=ByteBuffer.wrap(msg);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte[] data = new byte[msg.length-8];
		//System.out.println("IN CREATE FRAME "+AgentHorlogeMessage.debugByteArray(msg)+" -> "+AgentHorlogeMessage.debugByteArray(data)+"  "+sender+"  "+receiver);
		for(int i=0;i<msg.length-8;i++) data[i]=buf.get();
		return new HomeAgentFrame(sender,receiver,data);

	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param msg
	 */
	public HomeAgentFrame(int sender,int receiver,Message msg)
	{
		super(sender,receiver,msg);
		System.out.println("IN AHFRAME CONSTRUCTOR sender="+sender+" receiver="+receiver+" msg="+HomeAgentMessage.debugByteArray(msg.toByteSequence()));

	}
	
	/**
	 * parametrized constructor
	 * @param sender  sender identifier
	 * @param receiver receiver identifier
	 * @param bytes the message seen as a byte array 
	 */
	public HomeAgentFrame(int sender,int receiver,byte[] msg)
	{
		super(sender,receiver,HomeAgentMessage.createMessage(msg));
		System.out.println("IN AHFRAME CONSTRUCTOR<BYTE> sender="+sender+" receiver="+receiver+" msg="+HomeAgentMessage.debugByteArray(msg));
		
	}

	public HomeAgentMessage getAgentHorlogeMessageInFrame()
	{
		return  HomeAgentMessage.createMessage(super.getData());
	}

	/**
	 * returns a string representation of the frame
	 * @return the string representation
	 */
		public String toString()
	{	
		return "Frame de "+this.getSender()+" pour "+this.getReceiver()+". Le message est "+HomeAgentMessage.createMessage(super.getData()).toString();
	}
}
