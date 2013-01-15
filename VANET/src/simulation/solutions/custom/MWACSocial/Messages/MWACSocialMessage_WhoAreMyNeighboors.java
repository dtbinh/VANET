package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.MWACSocial.MWACSocialGroupAssistant;


public class MWACSocialMessage_WhoAreMyNeighboors extends MWACSocialMessage_Presentation{

	public MWACSocialMessage_WhoAreMyNeighboors(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,data);
	}
	
	public MWACSocialMessage_WhoAreMyNeighboors(int sender,byte role,int[] groups)
	{
		super(sender,role,groups);
	}
	

	public String toString()
	{
		return "WhoAreMyNeighboors message instancied by "+this.getSender()+" : [role="+this.role+",group="+MWACSocialGroupAssistant.groupsToString(this.getGroups())+"]";
	}
}
