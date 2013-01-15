package simulation.solutions.custom.MWACSocial.Messages;


public class MWACSocialMessage_Introduction extends MWACSocialMessage
{
	

	public MWACSocialMessage_Introduction(int sender)
	{
		super(sender,MWACSocialMessage.BROADCAST,MWACSocialMessage.msgINTRODUCTION);
	}

	public MWACSocialMessage_Introduction(int sender,int receiver)
	{
		super(sender,receiver,MWACSocialMessage.msgINTRODUCTION);
	}
	
	
	public  byte[] toByteSequence() 
	{
		return super.toByteSequence();
	}

	public String toString()
	{
		return "Introduction message instancied by "+this.getSender()+" to all surrounding neighboors";
	}
}
