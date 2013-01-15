package simulation.solutions.custom.DSDV.Messages;

import java.nio.ByteBuffer;
import simulation.messages.Message;

/**
 * le message DSDV pour generaliser ses quatres types(Introduction, Update, Data, Acknowledgment) 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

@SuppressWarnings("serial")
public class DSDV_Message extends Message{

	// le message doit etre diffusé pour tous les voisins du destinateur
	public final static byte BROADCAST = -1;

	//le message est de type introduction
	public final static byte msgINTRODUCTION =1;
	
	// le message est de type mise à jour  
	public final static byte msgUPDATE = 2;

	// le message est de type données
	public final static byte msgDATA = 3;

	// le message est de type acquittement
	public final static byte msgACKNOWLEDGEMENT = 4;


	// l'identité du destinateur
	private int sender;
	
	// l'identité du destinataire
	private int receiver;

	// le type du message DSDV (UPDATE, DATA or ACKNOWLEDGEMENT)
	private byte typeMsg;

	/**
	 * constructeur pour l'initialisation du message DSDV
	 * @param sender le destinateur du message DSDV
	 * @param receiver le destinataire du message DSDV
	 * @param typeMsg le type du message DSDV
	 */
	public DSDV_Message(int sender,int receiver,byte typeMsg)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.typeMsg=typeMsg;
	}

	/**
	 * reconstruit le message DSDV à partir d'une une suite de bytes
	 * @param msg suite de Bytes
	 * @return le message DSDV specifié (update, data, acknowledgment)
	 */
	public static DSDV_Message createMessage(byte[] msg)
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

		// According to the type of DSDV message, the DSDV message is builded
		switch(typeMsg)
		{
			case DSDV_Message.msgINTRODUCTION : 	return new DSDV_Message_Introduction(sender, receiver, data);
			case DSDV_Message.msgUPDATE: 			return new DSDV_Message_Update(sender,receiver,data); 
			case DSDV_Message.msgDATA: 				return new DSDV_Message_Data(sender,receiver,data); 
			case DSDV_Message.msgACKNOWLEDGEMENT: 	return new DSDV_Message_Acknowledgement(sender,receiver);
		}
		System.out.println("Unknown DSDV message "+typeMsg);
		return null;
	}
	
	/**
	 * retourne le type du message DSDV
	 * @return type du message (1,2,3)
	 */
	public int getType(){
		return this.typeMsg;
	}
	
	/**
	 * retourne le destinataire du message DSDV
	 * @return destinataire
	 */
	public int getReceiver() {
		return this.receiver;
	}

	/**
	 * retourne le destinateur du message DSDV
	 * @return destinateur
	 */
	public int getSender() {
		return this.sender;
	}

	/**
	 * pour retourner la representation du message DSDV en une suite de bytes  
	 * appelér la methode reecrite
	 **/
	public byte[] toByteSequence() {
		return this.toByteSequence(null);
	}
	
	/**
	 * retourne la representation du message en une suite de bytes  
	 * @return representation en bytes
	 **/
	public byte[] toByteSequence(byte[] data) {
		if(data==null)
			return ByteBuffer.allocate(8+1).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).array();
		else
			return ByteBuffer.allocate(8+1+data.length).putInt(this.sender).putInt(this.receiver).put(this.typeMsg).put(data).array();
	}
	
	/**
	 * retourne la representation du message en String  
	 * @return representation en String 
	 **/
	public String toString()
	{
		return "DSDV message of "+this.sender;
	}

	/**
	 * conversion du message du type Bytes to String  
	 * @return representation en String 
	 **/
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

