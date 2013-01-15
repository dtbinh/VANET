package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

import java.nio.ByteBuffer;

public class RecMASMessage_ComposeAccept extends RecMASMessage{

	private int superiorId;

	public RecMASMessage_ComposeAccept(int sender,int layer,int superiorId)
	{
		super(sender,layer,RecMASMessage.msgCOMPOSE_ACCEPT);
		this.superiorId=superiorId;
	}
	
	public RecMASMessage_ComposeAccept(int sender,int layer,byte[] data)
	{
		super(sender,layer,RecMASMessage.msgCOMPOSE_ACCEPT);
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.superiorId=buf.getInt();	
	}
	
	public byte[] toByteSequence() {
		
		return super.toByteSequence(ByteBuffer.allocate(4).putInt(this.superiorId).array());
	}
	
	public int getSuperiorId()
	{
		return this.superiorId;
	}
	
	public String toString()
	{
		return "[COMPOSE ACCEPT from (id="+this.getSender()+",level="+this.getLayer()+") to (id="+this.superiorId+"level="+(1+this.getLayer())+")";//    "+RecMASMessage.debugByteArray(this.toByteSequence())+"]";
	}

}
