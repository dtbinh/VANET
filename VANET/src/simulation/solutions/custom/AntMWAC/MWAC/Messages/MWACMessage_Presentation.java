package simulation.solutions.custom.AntMWAC.MWAC.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.AntMWAC.MWAC.AntMWACAgent;
import simulation.solutions.custom.AntMWAC.MWAC.MWACGroupAssistant;


/**
 * MWAC message : presentation of an agent to its neighboors
 * @author Jean-Paul Jamont
 */
public class MWACMessage_Presentation extends MWACMessage
{
	protected byte role;
	private int[] group;
		
	public MWACMessage_Presentation(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACMessage.msgPRESENTATION);
		int nbInt=(data.length-1)/4;
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.role=buf.get();
		this.group=new int[nbInt];
		for(int i=0;i<nbInt;i++) this.group[i]=(buf.getInt());
	}
	
	public MWACMessage_Presentation(int sender,byte role,int[] group)
	{
		super(sender,MWACMessage.BROADCAST,MWACMessage.msgPRESENTATION);
		this.role=role;
		this.group=group;
	}
	
	public void addGroup(int id)
	{
		int i;
		int[] newGroup = new int[1+group.length];
		for(i=0;i<this.group.length;i++) newGroup[i]=this.group[i];
		newGroup[i]=id;
		this.group=newGroup;
	}
	
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf=ByteBuffer.allocate(1+4*this.group.length).put(role);
		for(int i=0;i<this.group.length;i++) buf.putInt(this.group[i]);
		return super.toByteSequence(buf.array());
	}
	
	public byte getRole()
	{
		return this.role;
	}
	
	public int[] getGroups()
	{
		return this.group;
	}
	
	public int[] getClonedGroupArray()
	{
		return MWACGroupAssistant.cloneGroupArray(this.group);
	}
	

	public String toString()
	{		
		return "Presentation message instancied by "+this.getSender()+" : [role="+AntMWACAgent.roleToString(this.role)+",group="+MWACGroupAssistant.groupsToString(this.group)+"]";
	}
}
