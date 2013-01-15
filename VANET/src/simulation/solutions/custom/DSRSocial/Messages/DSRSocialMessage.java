package simulation.solutions.custom.DSRSocial.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Message;

public class DSRSocialMessage extends Message{

	public final static byte BROADCAST = -1;

	/** the message is a route request */
	public final static byte ROUTE_REQUEST = 1;
	/** the message is a route reply */
	public final static byte ROUTE_REPLY = 2;
	/** the message is a sended data */
	public final static byte DATA = 3;
	/** the message is a SERVICES REQUEST */
	public final static byte SERVICES_REQUEST = 4;
	/** the message is a SERVICES REPLY */
	public final static byte SERVICES_REPLY = 5;
	

	public static boolean TTL_VERSION_OF_DSR=true;

	private int receiver;
	private int sender;
	private byte typeMsg;



	public DSRSocialMessage(int sender,int receiver,byte typeMsg)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.typeMsg=typeMsg;
	}


	/** returns the volume of the message
	 * @return the volume of the message (bytes)
	 */	
	public static DSRSocialMessage createMessage(byte[] msg)
	{
		//System.out.println("Recomposition de "+ASTRO_Message.debugByteArray(msg));
		ByteBuffer buf=ByteBuffer.wrap(msg);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte typeMsg=buf.get();

		byte[] data= new byte[msg.length-9];

		for(int i=0;i<msg.length-9;i++) data[i]=buf.get();

		switch(typeMsg)
		{
		case DSRSocialMessage.ROUTE_REQUEST: return new DSRSocialMessage_RouteRequest(sender,receiver,data); 
		case DSRSocialMessage.ROUTE_REPLY: return new DSRSocialMessage_RouteReply(sender,receiver,data); 
		case DSRSocialMessage.DATA: return new DSRSocialMessage_Data(sender,receiver,data); 
		case DSRSocialMessage.SERVICES_REPLY: return new DSRSocialMessage_ServicesReply(sender,receiver,data); 
		case DSRSocialMessage.SERVICES_REQUEST: return new DSRSocialMessage_ServicesRequest(sender,receiver,data); 
		}
		System.out.println("Unknown DSR message "+typeMsg+"!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		return null;
	}

	public int getType()
	{
		return this.typeMsg;
	}

	public int getReceiver() {
		return this.receiver;
	}


	public int getSender() {
		return this.sender;
	}

	@Override
	public byte[] toByteSequence() {
		return this.toByteSequence(null);
	}
	public byte[] toByteSequence(byte[] data) {
		//System.out.println("On me demande de créer un msg de type "+this.typeMsg+" à partir de "+ASTRO_Message.debugByteArray(data));
		if(data==null)
			return ByteBuffer.allocate(8+1).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).array();
		else
		{
			//System.out.println("JE CREER "+ASTRO_Message.debugByteArray(ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array()));
			return ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array();
		}
	}

	public String toString()
	{
		return "DSR message of "+this.sender;
	}

	public  static String debugByteArray(byte[] data)
	{
		String result = "";
		if (data!=null)
			for (int i=0; i < data.length; i++) result += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 )+" ";
		else
			result= "null";
		return("["+result+"]");
	}

	public static void bytesCopy(byte[] dest,byte[] src)
	{
		for(int i=0;i<src.length;i++) dest[i]=src[i];
	}
	public static byte[] cloneBytes(byte ori[])
	{
		byte[] res = new byte[ori.length];
		DSRSocialMessage.bytesCopy(res,ori);
		return res;
	}


}
