package simulation.solutions.custom.RecMAS.MWAC.Messages;

import java.nio.ByteBuffer;


import simulation.solutions.custom.RecMAS.MWAC.MWACRouteAssistant;

/**
 * MWAC message : reply from a receiver representative to a route request
 * @author Jean-Paul Jamont
 */
public class MWACMessage_RouteReply extends MWACMessage
{
	/** the route */
	private int[] route;
	/** id of the route request that the route reply answer */
	private short idRequest;


	public MWACMessage_RouteReply(MWACMessage_RouteReply request)
	{
		this(request.getSender(),request.getReceiver(),request.getIdRequest(),MWACRouteAssistant.cloneRoute(request.getRoute()));
	}
	
	public MWACMessage_RouteReply(int sender,int receiver, short idRequest, int[] route)
	{
		super(sender,receiver,MWACMessage.msgROUTE_REPLY);
		this.idRequest=idRequest;
		this.route=route;
	}

	public MWACMessage_RouteReply(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACMessage.msgROUTE_REPLY);
		ByteBuffer buf=ByteBuffer.wrap(data);

		int capacity = (buf.capacity()-2)/4;
		if(capacity<0) capacity=0;
		this.idRequest=buf.getShort();

		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();
	}

	public MWACMessage_RouteReply(int sender,int receiver,short idRequest)
	{
		this(sender,receiver,idRequest,new int[0]);
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
		res.putShort(this.idRequest);
		for(int i=0;i<this.route.length;i++) res.putInt(this.route[i]);
		res.put(MWACRouteAssistant.routeToByteBuffer(this.route));
		return super.toByteSequence(res.array());
	}

	public short getIdRequest()
	{
		return this.idRequest;
	}

	public String toString()
	{
		return "Route reply #"+this.idRequest+" instancied by "+this.getSender()+" to "+MWACFrame.receiverIdToString(this.getReceiver())+" Route is "+MWACRouteAssistant.routeToString(this.route);
	}
}
