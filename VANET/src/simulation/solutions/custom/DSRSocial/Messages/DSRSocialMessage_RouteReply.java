package simulation.solutions.custom.DSRSocial.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;

public class DSRSocialMessage_RouteReply extends DSRSocialMessage{


	private int[] route; 
	private short requestIdentifier;

	public DSRSocialMessage_RouteReply(DSRSocialMessage_RouteRequest msg)
	{
		super(msg.getReceiver(),msg.getSender(),DSRSocialMessage.ROUTE_REPLY);
		this.route=msg.getRoute();
	}

	public DSRSocialMessage_RouteReply(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,DSRSocialMessage.ROUTE_REPLY);

		ByteBuffer buf=ByteBuffer.wrap(data);

		int capacity = (buf.capacity()-2)/4;
		if(capacity<0) capacity=0;
		this.requestIdentifier=buf.getShort();

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


	public  byte[] toByteSequence() 
	{
		ByteBuffer res=ByteBuffer.allocate(2+4*this.route.length);
		res.putShort(this.requestIdentifier);
		for(int i=0;i<route.length;i++) res.putInt(route[i]);
		return super.toByteSequence(res.array());
	}


	public String toString()
	{
		return "Route reply #"+this.requestIdentifier+" instancied by "+this.getSender()+" to "+DSRSocialFrame.receiverIdToString(this.getReceiver())+" Route is "+DSRRouteAssistant.toString(this.route);
	}



}
