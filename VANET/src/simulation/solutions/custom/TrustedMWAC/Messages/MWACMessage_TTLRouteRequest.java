package simulation.solutions.custom.TrustedMWAC.Messages;

import java.nio.ByteBuffer;
import simulation.solutions.custom.TrustedMWAC.MWACRouteAssistant;

/**
 * MWAC message : route request (sended by a representative agent)
 * @author Jean-Paul Jamont
 */

public class MWACMessage_TTLRouteRequest extends MWACMessage_RouteRequest
{
	private byte TTL;

	public MWACMessage_TTLRouteRequest(int sender,int receiver, short idRequest, byte TTL)
	{
		this(sender,receiver, idRequest, TTL,new int[0]);
	}
	public MWACMessage_TTLRouteRequest(int sender,int receiver, short idRequest, byte TTL, int[] route)
	{
		super(sender,receiver,MWACMessage.msgTTL_ROUTE_REQUEST);
		this.idRequest=idRequest;
		this.route=route;
		this.TTL=TTL;
	}

	public MWACMessage_TTLRouteRequest(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,MWACMessage.msgTTL_ROUTE_REQUEST);
		ByteBuffer buf=ByteBuffer.wrap(data);

		int capacity = (buf.capacity()-2-1)/4;
		if(capacity<0) capacity=0;
		this.idRequest=buf.getShort();

		this.TTL=buf.get();

		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();
	}

	public MWACMessage_TTLRouteRequest(int sender,int receiver,byte TTL,short idRequest)
	{
		this(sender,receiver,idRequest,TTL,new int[0]);
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
		res=ByteBuffer.allocate(2+1+4*this.route.length);
		res.putShort(this.idRequest);
		res.put(this.TTL);
		for(int i=0;i<route.length;i++) res.putInt(route[i]);
		return super.toByteSequence(res.array());
	}


	public String toString()
	{
		return "Route request #"+this.idRequest+" instancied by "+this.getSender()+" to "+MWACFrame.receiverIdToString(this.getReceiver())+"TTL="+this.TTL+"   Route is "+MWACRouteAssistant.routeToString(this.route);
	}
}
