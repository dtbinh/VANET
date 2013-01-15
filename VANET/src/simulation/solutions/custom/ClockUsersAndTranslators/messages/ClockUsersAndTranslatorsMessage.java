package simulation.solutions.custom.ClockUsersAndTranslators.messages;

import java.nio.ByteBuffer;

import simulation.messages.Message;
import simulation.solutions.custom.DSR.Route.DSRRouteAssistant;

/**
 * DSR message to generalize DSR messages (ROUTE REQUEST, ROUTE REPLY or DATA).
 * Remember that DSRMessage are embedded in DSRFrame
 * @author Jean-Paul Jamont
 */

public class ClockUsersAndTranslatorsMessage extends Message{

	/** the message must be BROADCASTED (all surrounding sensors are receiver of the message) */
	public final static byte BROADCAST = -1;

	/** identifier of the receiver */
	private int receiver;
	/** identifier of the sender */
	private int sender;

	private String message ;

	/**
	 * parametrized constructor
	 * @param sender identifier of the sender of this DSR message
	 * @param receiver identifier ot the receiver of this DSR message
	 * @param typeMsg identifier of the type of message
	 */
	public ClockUsersAndTranslatorsMessage(int sender,int receiver,String message)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.message=message;
	}


	/** create a DSRMessage from a byte array 
	 * @param msg the byte representation of the message
	 * @return the DSR message
	 */
	public static ClockUsersAndTranslatorsMessage createMessage(byte[] msg)
	{
		// the byte array is wrapped by a ByteBuffer
		ByteBuffer buf=ByteBuffer.wrap(msg);
		// get identifier of the sender (bytes [0,3])
		int sender=buf.getInt();
		// get identifier of the receiver (bytes [4,7])
		int receiver=buf.getInt();
		
		String  data="";
		char c;
		while(buf.hasRemaining())
		{
			c=(char)buf.get();
			data+=c;
		}

		
		return new ClockUsersAndTranslatorsMessage(sender,receiver,data);
		
	}

	/**
	 * allows to know the identifier of the receiver of this message
	 * @return identifier of the message receiver
	 */
	public int getReceiver() {
		return this.receiver;
	}

	
	public String getStringMessage()
	{
		return this.message;
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
		return this.toByteSequence(this.message.getBytes());
	}
	/** returns the byte representation of this message.
	 * @param data data bytes (added after sender, receiver and type)
	 * @return the byte representation 
	 * @override the Message method
	 */	
	public byte[] toByteSequence(byte[] data) {
		//System.out.println("On me demande de créer un msg de type "+this.typeMsg+" à partir de "+ASTRO_Message.debugByteArray(data));
		if(data==null)
			return ByteBuffer.allocate(8).putInt(this.sender).putInt(this.receiver).array();
		else
			//System.out.println("JE CREER "+ASTRO_Message.debugByteArray(ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array()));
			return ByteBuffer.allocate(8+data.length).putInt(this.sender).putInt(this.receiver).put(data).array();
	}

	/**
	 * returns a string representation of this message
	 * @return the string representation
	 */
	public String toString()
	{
		return "Message de "+this.sender+" a "+this.receiver+" donnée="+this.message;
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
