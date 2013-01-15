package simulation.solutions.custom.DSRSocial.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;
import simulation.solutions.custom.MWAC.Messages.MWACMessage;

public class DSRSocialMessage_RouteRequest extends DSRSocialMessage{

	private short requestIdentifier;
	private int[] route;
	private byte TTL;

	public DSRSocialMessage_RouteRequest(int sender,int receiver,short requestIdentifier,int[] route)
	{
		this(sender,receiver,requestIdentifier,(byte)0,route);
	}
	public DSRSocialMessage_RouteRequest(int sender,int receiver,short requestIdentifier,byte TTL,int[] route)
	{
		super(sender,receiver,DSRSocialMessage.ROUTE_REQUEST);

		this.requestIdentifier=requestIdentifier;
		this.route=route;
		this.TTL=TTL;
	}

	public DSRSocialMessage_RouteRequest(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,DSRSocialMessage.ROUTE_REQUEST);

		ByteBuffer buf=ByteBuffer.wrap(data);

		int capacity;
		if(DSRSocialMessage.TTL_VERSION_OF_DSR) 
			capacity = (buf.capacity()-2-1)/4;
		else
			capacity = (buf.capacity()-2)/4;
		if(capacity<0) capacity=0;

		this.requestIdentifier=buf.getShort();

		if(DSRSocialMessage.TTL_VERSION_OF_DSR) this.TTL=buf.get();

		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();

	}

	public byte getTTL()
	{
		return this.TTL;
	}

	public short getRequestIdentifier()
	{
		return this.requestIdentifier;
	}

	public int[] getRoute()
	{
		return this.route;
	}

	public void setRoute(int[] route)
	{
		this.route=route;
	}


	public  byte[] toByteSequence() 
	{
		ByteBuffer res;
		if(DSRSocialMessage.TTL_VERSION_OF_DSR)
			res=ByteBuffer.allocate(2+1+4*this.route.length);
		else
			res=ByteBuffer.allocate(2+4*this.route.length);
		res.putShort(this.requestIdentifier);
		if(DSRSocialMessage.TTL_VERSION_OF_DSR) res.put(this.TTL);
		for(int i=0;i<route.length;i++) res.putInt(route[i]);
		return super.toByteSequence(res.array());
	}


	public String toString()
	{
		if(DSRSocialMessage.TTL_VERSION_OF_DSR)
			return "Route request #"+this.requestIdentifier+" instancied by "+this.getSender()+" to "+DSRSocialFrame.receiverIdToString(this.getReceiver())+"TTL="+this.TTL+"  Route is "+DSRRouteAssistant.toString(this.route);
		else
			return "Route request #"+this.requestIdentifier+" instancied by "+this.getSender()+" to "+DSRSocialFrame.receiverIdToString(this.getReceiver())+" Route is "+DSRRouteAssistant.toString(this.route);
	}

}
