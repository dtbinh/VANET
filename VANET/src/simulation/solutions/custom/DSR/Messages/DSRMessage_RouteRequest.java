package simulation.solutions.custom.DSR.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;
import simulation.solutions.custom.MWAC.Messages.MWACMessage;

/**
 * DSR message which allow to a sender to search a path (route request)
 * @author Jean-Paul Jamont
 */
public class DSRMessage_RouteRequest extends DSRMessage{

	/** identifier of the request. Used because a same host can receive many times a same route request (the route request can take several route to arrive to a host). The host must answer only to the first one. The 2-uple (sender,request identifier) allow to identify the route request*/
	private short requestIdentifier;
	/** the route take by the route request */
	private int[] route;
	/** the time to live of the route request */
	private byte TTL;

	/**
	 * Parameterized constructor 
	 * @param sender sender to this route request
	 * @param receiver identifier of the searched host
	 * @param requestIdentifier identifier of the request
	 * @param route the builded route (=route take by the message)
	 */
	public DSRMessage_RouteRequest(int sender,int receiver,short requestIdentifier,int[] route)
	{
		this(sender,receiver,requestIdentifier,(byte)0,route);
	}

	/**
	 * Parameterized constructor (with TTL)
	 * @param sender sender to this route request
	 * @param receiver identifier of the searched host
	 * @param requestIdentifier identifier of the request
	 * @param TTL the TTL of this builded message
	 * @param route the builded route (=route take by the message)
	 */
	public DSRMessage_RouteRequest(int sender,int receiver,short requestIdentifier,byte TTL,int[] route)
	{
		super(sender,receiver,DSRMessage.ROUTE_REQUEST);

		this.requestIdentifier=requestIdentifier;
		this.route=route;
		this.TTL=TTL;
	}

	/**
	 * Parameterized constructor (DSR message builded from a specified data under a byte representation)
	 * @param sender sender to this route request
	 * @param receiver identifier of the searched host
	 * @param data byte representation of the TTL (if present) and the builded route)
	 */
	public DSRMessage_RouteRequest(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,DSRMessage.ROUTE_REQUEST);

		ByteBuffer buf=ByteBuffer.wrap(data);

		// capacity = number of hop in the route
		int capacity;
		if(DSRMessage.TTL_VERSION_OF_DSR) 
			capacity = (buf.capacity()-2-1)/4;
		else
			capacity = (buf.capacity()-2)/4;
		if(capacity<0) capacity=0;

		this.requestIdentifier=buf.getShort();

		if(DSRMessage.TTL_VERSION_OF_DSR) this.TTL=buf.get();

		this.route=new int[capacity];
		for(int i=0;i<capacity;i++) this.route[i]=buf.getInt();
	}

	/**
	 * returns the TTL
	 * @return the TTL
	 */
	public byte getTTL()
	{
		return this.TTL;
	}

	/**
	 * the request identifier of this DSR route request 
	 * @return the request identifier
	 */
	public short getRequestIdentifier()
	{
		return this.requestIdentifier;
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
		// build the DSR route request specialized part of the message
		ByteBuffer res;
		if(DSRMessage.TTL_VERSION_OF_DSR)
			res=ByteBuffer.allocate(2+1+4*this.route.length);
		else
			res=ByteBuffer.allocate(2+4*this.route.length);
		res.putShort(this.requestIdentifier);
		if(DSRMessage.TTL_VERSION_OF_DSR) res.put(this.TTL);
		for(int i=0;i<route.length;i++) res.putInt(route[i]);

		// return the generic part of all DSR message added to the previous specific part
		return super.toByteSequence(res.array());
	}

	/**
	 * returns a string representation of this message
	 * @return the string representation
	 */
	public String toString()
	{
		if(DSRMessage.TTL_VERSION_OF_DSR)
			return "Route request #"+this.requestIdentifier+" instancied by "+this.getSender()+" to "+DSRFrame.receiverIdToString(this.getReceiver())+"TTL="+this.TTL+"  Route is "+DSRRouteAssistant.toString(this.route);
		else
			return "Route request #"+this.requestIdentifier+" instancied by "+this.getSender()+" to "+DSRFrame.receiverIdToString(this.getReceiver())+" Route is "+DSRRouteAssistant.toString(this.route);
	}

}
