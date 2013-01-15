package simulation.solutions.custom.TrustedRoutingMWAC.Messages;


/**
 * MWAC message : introduction of an agent into the society
 * @author Jean-Paul Jamont
 */

@SuppressWarnings("serial")
public class MWACMessage_Introduction extends MWACMessage
{
	

	public MWACMessage_Introduction(int sender)
	{
		super(sender,MWACMessage.BROADCAST,MWACMessage.msgINTRODUCTION);
	}

	public MWACMessage_Introduction(int sender,int receiver)
	{
		super(sender,receiver,MWACMessage.msgINTRODUCTION);
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
