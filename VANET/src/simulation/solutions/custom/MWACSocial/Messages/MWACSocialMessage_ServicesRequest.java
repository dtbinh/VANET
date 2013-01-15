package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;
import java.util.ListIterator;
import java.util.Vector;

import simulation.solutions.custom.MWACSocial.MWACSocialRouteAssistant;



public class MWACSocialMessage_ServicesRequest extends MWACSocialMessage
{
	private short idRequest;
	private byte TTL;
	private int[] services;
	private int[] route;

	public MWACSocialMessage_ServicesRequest(int sender,int receiver, short idRequest, byte TTL,int[] services,int[] route)
	{
		super(sender,receiver,MWACSocialMessage.msgSERVICES_REQUEST);
		this.idRequest=idRequest;
		this.TTL=TTL;
		this.route=route;
		this.services=services;
	}

	public MWACSocialMessage_ServicesRequest(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACSocialMessage.msgSERVICES_REQUEST);
		ByteBuffer buf=ByteBuffer.wrap(data);

		this.idRequest=buf.getShort();
		this.TTL=buf.get();
		byte nbServices = buf.get();
		this.services=new int[nbServices];
		for(int i=0;i<nbServices;i++) this.services[i]=buf.getInt();

		int capacity = (buf.capacity()-2-1-1-nbServices*4)/4;
		if(capacity<0) capacity=0;
		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();
	}

	public int[] getRoute()
	{
		return this.route;
	}

	public void setRoute(int[] route)
	{
		this.route=route;
	}
	
	public int[] getServices()
	{
		return this.services;
	}
	
	public void setServices(int[] services)
	{
		this.services=services;
	}
	
	public short getIdRequest()
	{
		return this.idRequest;
	}


	public void decreaseTTL()
	{
		this.TTL--;
	}
	public byte getTTL()
	{
		return this.TTL;
	}

	public  byte[] toByteSequence() 
	{
		ByteBuffer res;
		res=ByteBuffer.allocate(2+1+1+4*this.services.length+4*this.route.length);
		res.putShort(this.idRequest);
		res.put(this.TTL);
		res.put((byte)this.services.length);
		for(int i=0;i<this.services.length;i++) res.putInt(this.services[i]);
		for(int i=0;i<this.route.length;i++) res.putInt(this.route[i]);
		
		return super.toByteSequence(res.array());
	}


	public String toString()
	{
			return "Services request #"+this.idRequest+" instancied by "+this.getSender()+" to "+MWACSocialFrame.receiverIdToString(this.getReceiver())+"TTL="+this.TTL+"   Route is "+MWACSocialRouteAssistant.integerArrayToString(this.route)+"   Searched services are "+MWACSocialRouteAssistant.integerArrayToString(this.services);
	}
}
