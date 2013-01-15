package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

public class RecMASMessage_DecomposeRequest extends RecMASMessage{
	

	public RecMASMessage_DecomposeRequest(int sender,int layer)
	{
		super(sender,layer,RecMASMessage.msgDECOMPOSE_REQUEST);
	}
	
	public String toString()
	{
		return "[DECOMPOSE REQUEST from (id="+this.getSender()+",level="+this.getLayer()+")";//    "+RecMASMessage.debugByteArray(this.toByteSequence())+"]";
	}
}
