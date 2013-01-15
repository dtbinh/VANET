package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.MWACSocial.MWACSocialAgent;
import simulation.solutions.custom.MWACSocial.MWACSocialGroupAssistant;


public class MWACSocialMessage_Presentation extends MWACSocialMessage
{
	protected byte role;
	private int[] group;
		
	public MWACSocialMessage_Presentation(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACSocialMessage.msgPRESENTATION);
		int nbInt=(data.length-1)/4;
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.role=buf.get();
		this.group=new int[nbInt];
		for(int i=0;i<nbInt;i++) this.group[i]=(buf.getInt());
	}
	
	public MWACSocialMessage_Presentation(int sender,byte role,int[] group)
	{
		super(sender,MWACSocialMessage.BROADCAST,MWACSocialMessage.msgPRESENTATION);
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
		return MWACSocialGroupAssistant.cloneGroupArray(this.group);
	}
	

	public String toString()
	{		
		return "Presentation message instancied by "+this.getSender()+" : [role="+MWACSocialAgent.roleToString(this.role)+",group="+MWACSocialGroupAssistant.groupsToString(this.group)+"]";
	}
}
