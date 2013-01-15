package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;
import java.util.ListIterator;
import java.util.Vector;

import simulation.solutions.custom.MWACSocial.MWACSocialRouteAssistant;



public class MWACSocialMessage_RouteRequest extends MWACSocialMessage
{
	private int[] route;
	private short idRequest;
	private byte TTL;

	public MWACSocialMessage_RouteRequest(int sender,int receiver, short idRequest, int[] route)
	{
		this(sender,receiver,idRequest,(byte)0,route);
	}
	public MWACSocialMessage_RouteRequest(int sender,int receiver, short idRequest, byte TTL, int[] route)
	{
		super(sender,receiver,MWACSocialMessage.msgROUTE_REQUEST);
		this.idRequest=idRequest;
		this.route=route;
		this.TTL=TTL;
	}

	public MWACSocialMessage_RouteRequest(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACSocialMessage.msgROUTE_REQUEST);
		ByteBuffer buf=ByteBuffer.wrap(data);

		int capacity;
		if (MWACSocialMessage.USE_TTL) 
			capacity = (buf.capacity()-2-1)/4;
		else
			capacity = (buf.capacity()-2)/4;
		if(capacity<0) capacity=0;
		this.idRequest=buf.getShort();

		if(MWACSocialMessage.USE_TTL) this.TTL=buf.get();

		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();
	}

	public MWACSocialMessage_RouteRequest(int sender,int receiver,short idRequest,byte TTL)
	{
		this(sender,receiver,idRequest,TTL,new int[0]);
	}

	public MWACSocialMessage_RouteRequest(int sender,int receiver,short idRequest)
	{
		this(sender,receiver,idRequest,(byte)0);
	}

	public short getIdRequest()
	{
		return this.idRequest;
	}

	public int[] getRoute()
	{
		return this.route;
	}

	public void setRoute(int[] route)
	{
		this.route=route;
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
		if(MWACSocialMessage.USE_TTL) 
			res=ByteBuffer.allocate(2+1+4*this.route.length);
		else
			res=ByteBuffer.allocate(2+4*this.route.length);
		res.putShort(this.idRequest);
		if(MWACSocialMessage.USE_TTL) res.put(this.TTL);
		for(int i=0;i<route.length;i++) res.putInt(route[i]);
		return super.toByteSequence(res.array());
	}


	public String toString()
	{
		if(MWACSocialMessage.USE_TTL)
			return "Route request #"+this.idRequest+" instancied by "+this.getSender()+" to "+MWACSocialFrame.receiverIdToString(this.getReceiver())+"TTL="+this.TTL+"   Route is "+MWACSocialRouteAssistant.integerArrayToString(this.route);
		else
			return "Route request #"+this.idRequest+" instancied by "+this.getSender()+" to "+MWACSocialFrame.receiverIdToString(this.getReceiver())+" Route is "+MWACSocialRouteAssistant.integerArrayToString(this.route);
	}
}
