package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

import java.nio.ByteBuffer;



public class RecMASMessage_msgExternSystemMessageTransport extends  RecMASMessage{

	private int receiver;
	private RecMASMessage data;
	private Label label;





	public RecMASMessage_msgExternSystemMessageTransport(RecMASMessage_msgInternSystemMessageTransport internMsg,int myId)
	{
		this(internMsg.getSender(),myId,internMsg.getLayer(),new Label(internMsg.getLabel()),internMsg.getData());
	}

	public RecMASMessage_msgExternSystemMessageTransport(int sender,int receiver,int layer,Label label,  RecMASMessage data)
	{
		super(sender,layer,RecMASMessage.msgEXTERN_SYSTEM_MESSAGE_TRANSPORT);
		this.receiver=receiver;
		this.label=label;
		this.data=data;
	}


	public RecMASMessage_msgExternSystemMessageTransport(int sender,int layer, byte[] data)
	{
		super(sender,layer,RecMASMessage.msgEXTERN_SYSTEM_MESSAGE_TRANSPORT);

		ByteBuffer buf=ByteBuffer.wrap(data);

		this.receiver=buf.getInt();
		this.label=new Label(buf.getInt());
		int size = buf.getInt();
		byte[] embeddedMessage = new byte[size];
		for(int i=0;i<size;i++) embeddedMessage[i]=buf.get();

		this.data=RecMASMessage.createMessage(embeddedMessage);
	}

	public  byte[] toByteSequence() 
	{
		byte[] embeddedMessage = this.data.toByteSequence();

		ByteBuffer buf=ByteBuffer.allocate(4 /* receiver*/+4 /*label */+4/* size */+embeddedMessage.length);
		buf.putInt(this.receiver);
		buf.putInt(this.label.value);
		buf.putInt(embeddedMessage.length);
		buf.put(embeddedMessage);

		return super.toByteSequence(buf.array());
	}

	public String toString()
	{
		return "[RecMSG EXTERNAL MESSAGE TRANSPORT from (id="+this.getSender()+",layer="+this.getLayer()+") labelized "+this.label+" to (id="+this.getReceiver()+",layer="+this.getLayer()+")    data="+this.data.toString();//+"                   "+RecMASMessage.debugByteArray(this.toByteSequence())+")";
	}

	public int getReceiver()
	{
		return this.receiver;
	}
	public void setReceiver(int receiver)
	{
		this.receiver=receiver;
	}

	public int getLabel()
	{
		return this.label.value;
	}
	public RecMASMessage getData()
	{
		return this.data;
	}


	
}
