package simulation.solutions.custom.MWACSocial.Messages;


public class MWACSocialMessage_NeighboorhoodServicesRequest extends MWACSocialMessage
{
	

	public MWACSocialMessage_NeighboorhoodServicesRequest(int sender)
	{
		super(sender,MWACSocialMessage.BROADCAST,MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REQUEST);
	}

	public MWACSocialMessage_NeighboorhoodServicesRequest(int sender,int receiver)
	{
		super(sender,receiver,MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REQUEST);
	}
	
	
	public  byte[] toByteSequence() 
	{
		return super.toByteSequence();
	}

	public String toString()
	{
		return "Neighboorhood services discovers message instancied by "+this.getSender()+" to all surrounding neighboors";
	}
}
