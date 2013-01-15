package simulation.solutions.custom.DSR.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;


/**
 * DSR message which allow to transport a data 
 * @author Jean-Paul Jamont
 */
public class DSRMessage_Data extends DSRMessage{

	/** the message*/
	private String msg;
	/** the route which must be taken by the message */
	int[] route;

	/**
	 * Parameterized constructor
	 * @param sender sender to this message
	 * @param receiver identifier of receiver of this message
	 * @param route route which must be taken by this message
	 * @param message transported message from sender to receiver
	 */
	public DSRMessage_Data(int sender,int receiver,int[] route,String message)
	{
		super(sender,receiver,DSRMessage.DATA);
		this.route=route;
		this.msg=message;
	}

	/**
	 * Parameterized constructor (DSR message builded from a specified data under a byte representation)
	 * @param sender sender to this message
	 * @param receiver identifier of receiver of this message
	 * @param data byte representation of the route and the message
	 */	public DSRMessage_Data(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,DSRMessage.DATA);

		ByteBuffer buf=ByteBuffer.wrap(data);
		short routeSize = buf.getShort();
		this.route=new int[routeSize];
		for(int i=0;i<routeSize;i++) route[i]=buf.getInt();
		this.msg="";
		char c;
		while(buf.hasRemaining())
		{
			c=(char)buf.get();
			msg+=c;
		}
	}

	/**
	 * returns the transported message 
	 * @return the message
	 */
	public String getMessage()
	{
		return msg;
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
		char[] ch=msg.toCharArray();
		ByteBuffer buf=ByteBuffer.allocate(2 /* route size */+4*this.route.length+ch.length);
		buf.putShort((short)this.route.length);
		for(int i=0;i<this.route.length;i++) buf.putInt(this.route[i]);
		for(int i=0;i<ch.length;i++) buf.put((byte)(ch[i]));
		return super.toByteSequence(buf.array());
	}

	/**
	 * returns a string representation of this message
	 * @return the string representation
	 */
	public String toString()
	{
		return "Data message instancied by "+this.getSender()+" to "+DSRFrame.receiverIdToString(this.getReceiver())+" Route is "+DSRRouteAssistant.toString(this.route)+" Data is '"+this.getMessage()+"'";
	}


}
