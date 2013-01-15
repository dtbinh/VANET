package simulation.solutions.custom.DSR.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Message;
import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;

/**
 * DSR message to generalize DSR messages (ROUTE REQUEST, ROUTE REPLY or DATA).
 * Remember that DSRMessage are embedded in DSRFrame
 * @author Jean-Paul Jamont
 */

public class DSRMessage extends Message{

	/** the message must be BROADCASTED (all surrounding sensors are receiver of the message) */
	public final static byte BROADCAST = -1;

	/** the message is a route request */
	public final static byte ROUTE_REQUEST = 1;
	/** the message is a route reply */
	public final static byte ROUTE_REPLY = 2;
	/** the message is a sended data */
	public final static byte DATA = 3;

	/** this message embed a TTL (time to live = max number of hop)*/
	public static boolean TTL_VERSION_OF_DSR=true;

	/** identifier of the receiver */
	private int receiver;
	/** identifier of the sender */
	private int sender;
	/** type of DSR message (see ROUTE_REQUEST, ROUTE_REPLY, DATA)*/
	private byte typeMsg;


	/**
	 * parametrized constructor
	 * @param sender identifier of the sender of this DSR message
	 * @param receiver identifier ot the receiver of this DSR message
	 * @param typeMsg identifier of the type of message
	 */
	public DSRMessage(int sender,int receiver,byte typeMsg)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.typeMsg=typeMsg;
	}


	/** create a DSRMessage from a byte array 
	 * @param msg the byte representation of the message
	 * @return the DSR message
	 */
	public static DSRMessage createMessage(byte[] msg)
	{
		// the byte array is wrapped by a ByteBuffer
		ByteBuffer buf=ByteBuffer.wrap(msg);
		// get identifier of the sender (bytes [0,3])
		int sender=buf.getInt();
		// get identifier of the receiver (bytes [4,7])
		int receiver=buf.getInt();
		// get the type of message (byte [8])
		byte typeMsg=buf.get();

		// get the data (bytes [9,..]
		byte[] data= new byte[msg.length-9];
		for(int i=0;i<msg.length-9;i++) data[i]=buf.get();

		// According the type of DSR message, the DSR message is builded
		switch(typeMsg)
		{
		case DSRMessage.ROUTE_REQUEST: return new DSRMessage_RouteRequest(sender,receiver,data); 
		case DSRMessage.ROUTE_REPLY: return new DSRMessage_RouteReply(sender,receiver,data); 
		case DSRMessage.DATA: return new DSRMessage_Data(sender,receiver,data); 
		}
		System.out.println("Unknown DSR message "+typeMsg);
		return null;
	}

	/**
	 * allows to know the type of DSR message
	 * @return the type of DSR message (ROUTE_REQUEST, ROUTE_REPLY, DATA)
	 */
	public int getType()
	{
		return this.typeMsg;
	}

	/**
	 * allows to know the identifier of the receiver of this message
	 * @return identifier of the message receiver
	 */
	public int getReceiver() {
		return this.receiver;
	}


	/**
	 * allows to know the identifier of the sender of this message
	 * @return identifier of the message sender
	 */
	public int getSender() {
		return this.sender;
	}

	/** returns the byte representation of this message.
	 * Used when there is no data bytes.
	 * @return the byte representation 
	 * @override the Message method
	 */	
	public byte[] toByteSequence() {
		return this.toByteSequence(null);
	}
	/** returns the byte representation of this message.
	 * @param data data bytes (added after sender, receiver and type)
	 * @return the byte representation 
	 * @override the Message method
	 */	
	public byte[] toByteSequence(byte[] data) {
		//System.out.println("On me demande de créer un msg de type "+this.typeMsg+" à partir de "+ASTRO_Message.debugByteArray(data));
		if(data==null)
			return ByteBuffer.allocate(8+1).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).array();
		else
			//System.out.println("JE CREER "+ASTRO_Message.debugByteArray(ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array()));
			return ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array();
	}

	/**
	 * returns a string representation of this message
	 * @return the string representation
	 */
	public String toString()
	{
		return "DSR message of "+this.sender;
	}

	/**
	 * returns a string representation of a byte array
	 * @param data the byte array
	 * @return the string representation
	 */
	public  static String debugByteArray(byte[] data)
	{
		String result = "";
		if (data!=null)
			for (int i=0; i < data.length; i++) result += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 )+" ";
		else
			result= "null";
		return("["+result+"]");
	}



}
