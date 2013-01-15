package simulation.solutions.custom.TrustedMWAC.Messages;

import java.nio.ByteBuffer;
import simulation.solutions.custom.TrustedMWAC.MWACGroupAssistant;


/**
 * MWAC message : presentation of an agent to its neighboors
 * @author Jean-Paul Jamont
 */
public class MWACMessage_PossibleOrganizationalIncoherence extends MWACMessage
{

	private int[] suspectedGroups;
		
	public MWACMessage_PossibleOrganizationalIncoherence(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACMessage.msgPOSSIBLE_ORGANIZATIONAL_INCOHERENCE_NOTIFICATION);
		int nbInt=data.length/4;
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.suspectedGroups=new int[nbInt];
		for(int i=0;i<nbInt;i++) this.suspectedGroups[i]=(buf.getInt());
	}
	
	public MWACMessage_PossibleOrganizationalIncoherence(int sender,int[] suspectedGroups)
	{
		super(sender,MWACMessage.BROADCAST,MWACMessage.msgPOSSIBLE_ORGANIZATIONAL_INCOHERENCE_NOTIFICATION);
		this.suspectedGroups=suspectedGroups;
	}
		
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf=ByteBuffer.allocate(4*this.suspectedGroups.length);
		for(int i=0;i<this.suspectedGroups.length;i++) buf.putInt(this.suspectedGroups[i]);
		return super.toByteSequence(buf.array());
	}
	
	
	public int[] getSuspectedGroups()
	{
		return this.suspectedGroups;
	}
	
	public int[] getClonedSuspectedGroupsArray()
	{
		return MWACGroupAssistant.cloneGroupArray(this.suspectedGroups);
	}
	

	public String toString()
	{		
		return "Possible incoherence detection notification message instancied by "+this.getSender()+" : suspected groups="+MWACGroupAssistant.groupsToString(this.suspectedGroups)+"]";
	}
}
