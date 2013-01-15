package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

public class RecMASMessage_DecomposeInform extends RecMASMessage{
	

	public RecMASMessage_DecomposeInform(int sender,int layer)
	{
		super(sender,layer,RecMASMessage.msgDECOMPOSE_INFORM);
	}
	
	public String toString()
	{
		return "[DECOMPOSE INFORM from (id="+this.getSender()+",level="+this.getLayer()+")";//    "+RecMASMessage.debugByteArray(this.toByteSequence())+"]";
	}
}
