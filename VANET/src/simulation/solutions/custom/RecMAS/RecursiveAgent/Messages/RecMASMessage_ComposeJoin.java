package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

public class RecMASMessage_ComposeJoin extends RecMASMessage{
	public RecMASMessage_ComposeJoin(int sender,int layer)
	{
		super(sender,layer,RecMASMessage.msgCOMPOSE_JOIN);
	}
	public String toString()
	{
		return "[COMPOSE JOIN from (id="+this.getSender()+",level="+this.getLayer()+")";//    "+RecMASMessage.debugByteArray(this.toByteSequence())+"]";
	}
	
}
