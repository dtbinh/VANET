package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;

public class MWACSocialMessage_Data extends MWACSocialMessage
{
	protected String msg;


	public MWACSocialMessage_Data(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACSocialMessage.msgDATA);
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.msg="";
		while(buf.hasRemaining())
		{
			char c=(char)buf.get();
			msg+=c;
		}
	}

	public MWACSocialMessage_Data(int sender,int receiver,String msg)
	{
		super(sender,receiver,MWACSocialMessage.msgDATA);
		this.msg=msg;
	}


	public  byte[] toByteSequence() 
	{
		char[] ch=msg.toCharArray();
		ByteBuffer buf=ByteBuffer.allocate(ch.length);
		for(int i=0;i<ch.length;i++) buf.put((byte)(ch[i]));
		return super.toByteSequence(buf.array());
	}

	public String getMsg()
	{
		return this.msg;
	}

	public String toString()
	{
		return "Data Message from "+this.getSender()+" to "+this.getReceiver()+".  [msg="+this.msg+"]";
	}
}
