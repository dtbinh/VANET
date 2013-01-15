package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

public class RecMASMessage_ComposeRequest extends RecMASMessage{
	

	public RecMASMessage_ComposeRequest(int sender,int layer)
	{
		super(sender,layer,RecMASMessage.msgCOMPOSE_REQUEST);
	}
	
	public String toString()
	{
		return "[COMPOSE REQUEST from (id="+this.getSender()+",level="+this.getLayer()+")";//    "+RecMASMessage.debugByteArray(this.toByteSequence())+"]";
	}
}
