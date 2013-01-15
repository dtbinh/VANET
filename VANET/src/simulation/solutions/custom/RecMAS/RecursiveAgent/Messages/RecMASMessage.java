package simulation.solutions.custom.RecMAS.RecursiveAgent.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Message;
import simulation.solutions.custom.TrustedMWAC.Messages.MWACMessage;



/**
 * MWAC message : MWAC specialized message
 * @author Jean-Paul Jamont
 */

public class RecMASMessage extends Message{

	public final static byte BROADCAST = MWACMessage.BROADCAST;
	

	public final static String header = "RM";

	public final static byte msgCOMPOSE_REQUEST = 0x01;
	public final static byte msgCOMPOSE_ACCEPT = 0x02;
	public final static byte msgCOMPOSE_JOIN = 0x03;
	public final static byte msgCOMPOSE_UPDATE =0x04;
	
	public final static byte msgDECOMPOSE_REQUEST = 0x10;
	public final static byte msgDECOMPOSE_INFORM = 0x11;
	
	public final static byte msgAPPLICATIVE_MESSAGE = 0x20;
	public final static byte msgINTERN_SYSTEM_MESSAGE_TRANSPORT = 0x21;
	public final static byte msgEXTERN_SYSTEM_MESSAGE_TRANSPORT = 0x22;

	
	
	private int sender;
	private int layer;
	private byte typeMsg;


	public RecMASMessage(){}

	public RecMASMessage(int sender,int layer,byte typeMsg)
	{
		this.sender=sender;
		this.layer=layer;
		this.typeMsg=typeMsg;
	}

	/** returns the volume of the message
	 * @return the volume of the message (bytes)
	 */	
	public static RecMASMessage createMessage(byte[] msg)
	{
		//System.out.println("Recomposition de "+ASTRO_Message.debugByteArray(msg));

		
		ByteBuffer buf=ByteBuffer.wrap(msg);
		
		if(buf.get()!=(byte)RecMASMessage.header.charAt(0)) return null;
		if(buf.get()!=(byte)RecMASMessage.header.charAt(1)) return null;
		
		int sender=buf.getInt();
		int layer = (int) buf.get();
		byte typeMsg=buf.get();
		byte[] data = new byte[msg.length-8];
		for(int i=0;i<msg.length-8;i++) data[i]=buf.get();

		//System.out.println("sender="+sender+"  receiver="+receiver+"  data="+ASTRO_Message.debugByteArray(data));


		switch(typeMsg)
		{
		case RecMASMessage.msgDECOMPOSE_INFORM: return new RecMASMessage_DecomposeInform(sender,layer); 
		case RecMASMessage.msgDECOMPOSE_REQUEST: return new RecMASMessage_DecomposeRequest(sender,layer);
		
		case RecMASMessage.msgCOMPOSE_REQUEST: return new RecMASMessage_ComposeRequest(sender,layer); 
		case RecMASMessage.msgCOMPOSE_ACCEPT: return new RecMASMessage_ComposeAccept(sender,layer,data); 
		case RecMASMessage.msgCOMPOSE_JOIN: return new RecMASMessage_ComposeJoin(sender,layer);
		case RecMASMessage.msgCOMPOSE_UPDATE: return new RecMASMessage_ComposeUpdate(sender,layer,data);
		
		case RecMASMessage.msgINTERN_SYSTEM_MESSAGE_TRANSPORT: return new RecMASMessage_msgInternSystemMessageTransport(sender,layer,data);
		case RecMASMessage.msgEXTERN_SYSTEM_MESSAGE_TRANSPORT: return new RecMASMessage_msgExternSystemMessageTransport(sender,layer,data);
		
		case RecMASMessage.msgAPPLICATIVE_MESSAGE: return new RecMASMessage_msgApplicativeMessage(sender,layer,data);
		default:  System.out.println("Unknown RecMAS message");
		}
		
		
		return null;
	}

	public int getType()
	{
		return this.typeMsg;
	}

	

	public int getSender() {
		return this.sender;
	}
	
	public int getLayer()
	{
		return this.layer;
	}
	
	public void setType(byte type)
	{
		this.typeMsg=type;
	}

	@Override
	public byte[] toByteSequence() {
		return this.toByteSequence(null);
	}
	public byte[] toByteSequence(byte[] data) {
		//System.out.println("On me demande de créer un msg de type "+this.typeMsg+" à partir de "+ASTRO_Message.debugByteArray(data));
		if(data==null)
		
		return ByteBuffer.allocate(2+4+1+1).put((byte)RecMASMessage.header.charAt(0)).put((byte)RecMASMessage.header.charAt(1)).putInt(this.sender).put((byte) this.layer).put(this.typeMsg).array();
		
		else
		{
			//System.out.println("JE CREER "+ASTRO_Message.debugByteArray(ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array()));
			return ByteBuffer.allocate(4+2+1+1+data.length).put((byte)RecMASMessage.header.charAt(0)).put((byte)RecMASMessage.header.charAt(1)).putInt(this.sender).put((byte) this.layer).put(this.typeMsg).put(data).array();
		}
	}

	public String toString()
	{
		return "RecMAS message of "+this.sender;
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
		RecMASMessage.bytesCopy(res,ori);
		return res;
	}

	@Override
	public int getReceiver() {
		// TODO Auto-generated method stub
		return RecMASMessage.BROADCAST;
	}
	
}

