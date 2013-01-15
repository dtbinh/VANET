package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.solutions.custom.RecMAS.MWAC.Messages.MWACFrame;
import simulation.solutions.custom.RecMAS.MWAC.Messages.MWACMessage;

public class RecMASMessage_msgApplicativeMessage extends  RecMASMessage{

	private int receiver;
	private MWACMessage data;
	
		
	
	public RecMASMessage_msgApplicativeMessage(int sender,int receiver, int layer, MWACMessage data)
	{
		super(sender,layer,RecMASMessage.msgAPPLICATIVE_MESSAGE);
		this.receiver=receiver;
		this.data=data;
	}
	

	public RecMASMessage_msgApplicativeMessage(int sender,int layer, byte[] data)
	{
		super(sender,layer,RecMASMessage.msgAPPLICATIVE_MESSAGE);
		
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.receiver=buf.getInt();
		int size = buf.getInt();
		
		byte[] embeddedMessage = new byte[size];
		for(int i=0;i<size;i++) embeddedMessage[i]=buf.get();
		
		this.data=MWACMessage.createMessage(embeddedMessage);
	}
	
	public int getReceiver()
	{
		return this.receiver;
	}
	public  byte[] toByteSequence() 
	{
		byte[] embeddedMessage = this.data.toByteSequence();
		
		ByteBuffer buf=ByteBuffer.allocate(4 /* receiver*/+4/* size */+embeddedMessage.length);
		
		buf.putInt(this.receiver);
		buf.putInt(embeddedMessage.length);
		buf.put(embeddedMessage);
		
		return super.toByteSequence(buf.array());
	}
	
	public String toString()
	{
		return "[RecMSG applicative frame transport message from (id="+this.getSender()+",layer="+this.getLayer()+") to (id="+this.receiver+",layer="+this.getLayer()+")   data="+this.data.toString();//+"                   "+MWACMessage.debugByteArray(this.toByteSequence())+"]";

	}
	
	public MWACMessage getData()
	{
		return this.data;
	}
	public MWACFrame getDataUnderFrameView()
	{
		return new MWACFrame(this.getSender(),MWACFrame.BROADCAST,this.data);
	}
	
	

}
