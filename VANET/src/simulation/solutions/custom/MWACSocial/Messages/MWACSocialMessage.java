package simulation.solutions.custom.MWACSocial.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Message;

public class MWACSocialMessage extends Message{

	public final static byte BROADCAST = -1;

	public static boolean USE_TTL=true;


	/** Message sended at the agent birth */
	public final static byte msgINTRODUCTION = 1;
	/** An agent use this message to present him to neighbor... No response required */
	public final static byte msgPRESENTATION = 2;
	/** A representative agent manage its role conflict by sending its score*/
	public final static byte msgCONFLICT_RESOLUTION = 3;
	/** Like presentation but require a response from surrounding neighbors */
	public final static byte msgWHO_ARE_MY_NEIGHBOORS = 4;
	/** Data message */
	public final static byte msgDATA = 5;
	/** Route request */
	public final static byte msgROUTE_REQUEST = 6;
	/** Route reply */
	public final static byte msgROUTE_REPLY = 7;
	/** Routed data */
	public final static byte msgROUTED_DATA = 8;	
	/** Services request*/
	public final static byte msgSERVICES_REQUEST = 9;
	/** Services reply*/
	public final static byte msgSERVICES_REPLY = 10;
	/** neighboorhood services request */
	public final static byte msgNEIGHBOORHOOD_SERVICES_REQUEST = 11;
	/** neighboorhood services reply */
	public final static byte msgNEIGHBOORHOOD_SERVICES_REPLY = 12;
	

	private int receiver;
	private int sender;
	private byte typeMsg;


	public MWACSocialMessage(){}

	public MWACSocialMessage(int sender,int receiver,byte typeMsg)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.typeMsg=typeMsg;
	}

	/** returns the volume of the message
	 * @return the volume of the message (bytes)
	 */	
	public static MWACSocialMessage createMessage(byte[] msg)
	{
		//System.out.println("Recomposition de "+ASTRO_Message.debugByteArray(msg));

		ByteBuffer buf=ByteBuffer.wrap(msg);
		int sender=buf.getInt();
		int receiver=buf.getInt();
		byte typeMsg=buf.get();
		byte[] data = new byte[msg.length-9];
		for(int i=0;i<msg.length-9;i++) data[i]=buf.get();

		//System.out.println("sender="+sender+"  receiver="+receiver+"  data="+ASTRO_Message.debugByteArray(data));


		switch(typeMsg)
		{
		case MWACSocialMessage.msgINTRODUCTION: return new MWACSocialMessage_Introduction(sender,receiver); 
		case MWACSocialMessage.msgPRESENTATION: return new MWACSocialMessage_Presentation(sender,receiver,data); 
		case MWACSocialMessage.msgCONFLICT_RESOLUTION: return new MWACSocialMessage_ConflictResolution(sender,receiver,data);
		case MWACSocialMessage.msgWHO_ARE_MY_NEIGHBOORS: return new MWACSocialMessage_WhoAreMyNeighboors(sender,receiver,data);
		case MWACSocialMessage.msgDATA: return new MWACSocialMessage_Data(sender,receiver,data);
		case MWACSocialMessage.msgROUTE_REQUEST: return new MWACSocialMessage_RouteRequest(sender,receiver,data);
		case MWACSocialMessage.msgROUTE_REPLY: return new MWACSocialMessage_RouteReply(sender,receiver,data);
		case MWACSocialMessage.msgROUTED_DATA: return new MWACSocialMessage_RoutedData(sender,receiver,data);
		case MWACSocialMessage.msgSERVICES_REQUEST:return new MWACSocialMessage_ServicesRequest(sender,receiver,data);
		case MWACSocialMessage.msgSERVICES_REPLY:return new MWACSocialMessage_ServicesReply(sender,receiver,data);
		case MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REQUEST:return new MWACSocialMessage_NeighboorhoodServicesRequest(sender,receiver);
		case MWACSocialMessage.msgNEIGHBOORHOOD_SERVICES_REPLY:return new MWACSocialMessage_NeighboorhoodServicesReply(sender,receiver,data);
		
		}
		System.out.println("Unknown MWAC message");
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
		return "ASTRO message of "+this.sender;
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
		MWACSocialMessage.bytesCopy(res,ori);
		return res;
	}


}
