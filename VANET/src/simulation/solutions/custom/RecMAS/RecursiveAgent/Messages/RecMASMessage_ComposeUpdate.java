package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.RecMAS.MWAC.MWACAgent;
import simulation.solutions.custom.TrustedRoutingMWAC.MWACRouteAssistant;

public class RecMASMessage_ComposeUpdate extends RecMASMessage {

	private int[] aggregatedAgents;
	private byte role;


	
	public RecMASMessage_ComposeUpdate(int sender,int layer,byte role,int[] aggregatedAgents)
	{
		super(sender,layer,RecMASMessage.msgCOMPOSE_UPDATE);
		this.aggregatedAgents=aggregatedAgents;
		this.role=role;
	}

	public RecMASMessage_ComposeUpdate(int sender,int layer,byte[] data)
	{
		super(sender,layer,RecMASMessage.msgCOMPOSE_UPDATE);
		ByteBuffer buf=ByteBuffer.wrap(data);
		this.role=buf.get();
		int nbOfAgents =buf.getInt(); 
		this.aggregatedAgents=new int[nbOfAgents];
		for(int i=0;i<nbOfAgents;i++) this.aggregatedAgents[i]=buf.getInt();	
	}

	public byte[] toByteSequence() {

		ByteBuffer buf=ByteBuffer.allocate(1+4+4*this.aggregatedAgents.length).put(this.role).putInt(this.aggregatedAgents.length);
		for(int i=0;i<this.aggregatedAgents.length;i++) buf.putInt(this.aggregatedAgents[i]);
		return super.toByteSequence(buf.array());
	}

	public byte getRole()
	{
		return this.role;
	}
	public int[] getAggregatedAgents()
	{
		return this.aggregatedAgents;
	}


	public String toString()
	{
		return "[COMPOSE UPDATE from (id="+this.getSender()+",level="+this.getLayer()+") : role is "+MWACAgent.roleToString(this.role)+" members are "+MWACRouteAssistant.routeToString(this.aggregatedAgents);//+"     "+RecMASMessage.debugByteArray(this.toByteSequence())+"]";
	}
}
