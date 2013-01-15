package simulation.solutions.custom.DSR.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;

/**
 * DSR message which allow to a receiver to choose the path
 * @author Jean-Paul Jamont
 */
public class DSRMessage_RouteReply extends DSRMessage{

	/** the chosen route by the searched host */
	private int[] route;
	/** identifier of the answered route request */
	private short requestIdentifier;

	/**
	 * Parameterized constructor (DSR message builded from the answered route request)
	 * @param msg
	 */
	public DSRMessage_RouteReply(DSRMessage_RouteRequest msg)
	{
		super(msg.getReceiver(),msg.getSender(),DSRMessage.ROUTE_REPLY);
		this.route=msg.getRoute();
	}


	/**
	 * Parameterized constructor (DSR message builded from a specified data under a byte representation)
	 * @param sender identifier of the sender of this route reply
	 * @param receiver identifier of the receiver of this route reply (which is the sender of the answered route request)
	 * @param data byte representation of the builded route)
	 */
	public DSRMessage_RouteReply(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,DSRMessage.ROUTE_REPLY);

		ByteBuffer buf=ByteBuffer.wrap(data);

		int capacity = (buf.capacity()-2)/4;
		if(capacity<0) capacity=0;
		this.requestIdentifier=buf.getShort();

		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();
	}


	/**
	 * the builded route (i.e. the route taken by this DSR route request)
	 * @return the route
	 */
	public int[] getRoute()
	{
		return this.route;
	}

	/**
	 * set the route of this DSR route request
	 * @param the new route
	 */
	public void setRoute(int[] route)
	{
		this.route=route;
	}



	/**
	 * allows to return the byte presentation of a DSR route request message
	 * return the byte representation
	 */
	public  byte[] toByteSequence() 
	{
		ByteBuffer res=ByteBuffer.allocate(2+4*this.route.length);
		res.putShort(this.requestIdentifier);
		for(int i=0;i<route.length;i++) res.putInt(route[i]);
		return super.toByteSequence(res.array());
	}

	/**
	 * returns a string representation of this message
	 * @return the string representation
	 */
	public String toString()
	{
		return "Route reply #"+this.requestIdentifier+" instancied by "+this.getSender()+" to "+DSRFrame.receiverIdToString(this.getReceiver())+" Route is "+DSRRouteAssistant.toString(this.route);
	}

}
