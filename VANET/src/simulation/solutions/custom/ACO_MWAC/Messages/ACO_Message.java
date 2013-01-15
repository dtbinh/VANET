package simulation.solutions.custom.ACO_MWAC.Messages;

import java.nio.ByteBuffer;
import simulation.messages.Message;

/**
 * pour generaliser les quatres types de messages ACO (Fourmi_forward, Fourmi_backward, initialisation, mise à jour) 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

public class ACO_Message extends Message{
	
	
	public final static byte BROADCAST = -1;

	// message d'initialisation de la pheromone  
	public final static byte msgINITIALIZATION = 11;

	// formis forward
	public final static byte msgFORWARD_ANT = 12;
	
	// fourmis backward
	public final static byte msgBACKWARD_ANT = 13;
	
	// message de mise à jour niveau d'energie
	public final static byte msgUPDATE = 14;

	private int receiver;
	private int sender;
	private byte typeMsg;
	
	/**
	 * constructeur pour l'initialisation du message 
	 * @param sender le destinateur du message 
	 * @param receiver le destinataire du message 
	 * @param typeMsg le type du message 
	 */
	public ACO_Message (int sender, int receiver, byte typeMsg)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.typeMsg = typeMsg;
	}

	/**
	 * reconstruit le message à partir d'une une suite de bytes
	 * @param message en suite de Bytes
	 * @return le type de message bien specifié  
	 */
	public synchronized static ACO_Message createMessage(byte[] msg)
	{
		// the byte array is wrapped by a ByteBuffer
		ByteBuffer buf=ByteBuffer.wrap(msg);

		// get identifier of the sender (bytes [0,3])
		int sender=buf.getInt();

		// get identifier of the receiver (bytes [4,7])
		int receiver=buf.getInt();

		// get the type of the Ant (byte [8])
		byte typeMsg=buf.get();

		// get the data (bytes [9,..]
		byte[] data= new byte[msg.length-9];
		for(int i=0;i<msg.length-9;i++) data[i]=buf.get();

		// According to the type of the Ant, the Ant is builded
		switch(typeMsg)
		{
			case ACO_Message.msgINITIALIZATION:		return new ACO_Message_Initialization(sender,receiver,data);
			case ACO_Message.msgFORWARD_ANT: 		return new ACO_Message_Forward_Ant(sender,receiver,data);
			case ACO_Message.msgBACKWARD_ANT: 		return new ACO_Message_Backward_Ant(sender,receiver,data);
			case ACO_Message.msgUPDATE: 			return new ACO_Message_Update(sender,receiver,data); 
		}
		System.out.print("Unknow ACO MESSAGE, IT IS AN MWAC MESSAGE	:"+typeMsg);
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

	public byte[] toByteSequence() {
		return this.toByteSequence(null);
	}
	
	public byte[] toByteSequence(byte[] data) {
		
		if(data==null)
			return ByteBuffer.allocate(8+1).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).array();
		else
		{
			return ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array();
		}
	}

	public String toString()
	{
		return "ACO message of "+this.sender;
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
		ACO_Message.bytesCopy(res,ori);
		return res;
	}
	
}

