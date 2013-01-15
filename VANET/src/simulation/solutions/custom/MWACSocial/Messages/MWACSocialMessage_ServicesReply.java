package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;
import java.util.ListIterator;
import java.util.Vector;

import simulation.solutions.custom.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.MWACSocial.MWACSocialRouteAssistant;



public class MWACSocialMessage_ServicesReply extends MWACSocialMessage
{
	private short idRequest;
	private int[] services;
	private int[] route;

	
	public MWACSocialMessage_ServicesReply(MWACSocialMessage_ServicesReply request)
	{
		this(request.getSender(),request.getReceiver(),request.getIdRequest(),MWACRouteAssistant.cloneRoute(request.getServices()),MWACRouteAssistant.cloneRoute(request.getRoute()));
	}
	
	public MWACSocialMessage_ServicesReply(int sender,int receiver, short idRequest, int[] services,int[] route)
	{
		super(sender,receiver,MWACSocialMessage.msgSERVICES_REPLY);
		this.idRequest=idRequest;
		this.route=route;
		this.services=services;
	}

	public MWACSocialMessage_ServicesReply(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACSocialMessage.msgSERVICES_REPLY);
		ByteBuffer buf=ByteBuffer.wrap(data);

		this.idRequest=buf.getShort();
		byte nbServices = buf.get();
		this.services=new int[nbServices];
		for(int i=0;i<nbServices;i++) this.services[i]=buf.getInt();

		int capacity = (buf.capacity()-2-1-nbServices*4)/4;
		if(capacity<0) capacity=0;
		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();
	}

	public int[] getRoute()
	{
		return this.route;
	}
	public int[] getServices()
	{
		return this.services;
	}

	public short getIdRequest()
	{
		return this.idRequest;
	}

	public  byte[] toByteSequence() 
	{
		ByteBuffer res;
		res=ByteBuffer.allocate(2+1+4*this.services.length+4*this.route.length);
		res.putShort(this.idRequest);
		res.put((byte)this.services.length);
		for(int i=0;i<this.services.length;i++) res.putInt(this.services[i]);
		for(int i=0;i<this.route.length;i++) res.putInt(this.route[i]);
		
		return super.toByteSequence(res.array());
	}


	public String toString()
	{
			return "Services reply #"+this.idRequest+" instancied by "+this.getSender()+" to "+MWACSocialFrame.receiverIdToString(this.getReceiver())+"   Route is "+MWACSocialRouteAssistant.integerArrayToString(this.route)+"   Searched services are "+MWACSocialRouteAssistant.integerArrayToString(this.services);
	}
}
