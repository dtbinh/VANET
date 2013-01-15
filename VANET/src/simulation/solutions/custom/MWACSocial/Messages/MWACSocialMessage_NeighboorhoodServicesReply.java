package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;
import java.util.Vector;

import simulation.solutions.custom.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.MWACSocial.MWACSocialRouteAssistant;


public class MWACSocialMessage_NeighboorhoodServicesReply extends MWACSocialMessage
{
	
	private int[] services;

	public MWACSocialMessage_NeighboorhoodServicesReply(int sender,int receiver,Vector<Integer> services)
	{
		super(sender,receiver,MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REPLY);
		this.services=new int[services.size()];
		for(int i=0;i<services.size();i++) this.services[i]=services.get(i);
	}

	public MWACSocialMessage_NeighboorhoodServicesReply(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REPLY);
		ByteBuffer buf=ByteBuffer.wrap(data);
		int numberOfServices = data.length/4;
		this.services=new int[numberOfServices];
		for(int i=0;i<numberOfServices;i++) this.services[i]=buf.getInt();
	}
	
	public int[] getServices()
	{
		return this.services;
	}
	
	public  byte[] toByteSequence() 
	{
		ByteBuffer res=ByteBuffer.allocate(4*this.services.length);
		for(int i=0;i<this.services.length;i++) res.putInt(this.services[i]);
		res.put(MWACRouteAssistant.routeToByteBuffer(this.services));
		return super.toByteSequence(res.array());
	}

	public String toString()
	{
		return "Services neighboorhood reply instancied by "+this.getSender()+" to its representative. Services is "+MWACSocialRouteAssistant.integerArrayToString(this.services);
	}
}
