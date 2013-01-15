package simulation.solutions.custom.DSDV.Messages;

import java.nio.ByteBuffer;

import simulation.solutions.custom.DSDV.Entry_routing_table;

/**
 * le message DSDV permettant de transporter une entrée de la table de routage
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
@SuppressWarnings("serial")
public class DSDV_Message_Introduction extends DSDV_Message{

	//le Sequence Number
	private int SN;
	
	
	/**
	 * constructeur pour l'initialisation du message de mise à jour
	 * @param sender destinateur
	 * @param receiver destinataire
	 */
	public DSDV_Message_Introduction(int sender,int receiver, int SequenceNumber)
	{
		super(sender,receiver,DSDV_Message.msgINTRODUCTION);
		this.SN = SequenceNumber;
	}
	public DSDV_Message_Introduction(int sender,int receiver, byte [] data)
	{
		super(sender,receiver,DSDV_Message.msgINTRODUCTION);
		ByteBuffer buf = ByteBuffer.wrap(data);
		this.SN = buf.getInt();
	}

	
	public int get_SN(){
		return this.SN;
	}
	/**
	 * retourne la representation du message en une suite de bytes  
	 * @return representation en bytes
	 **/
	public  byte[] toByteSequence() 
	{	
		return super.toByteSequence((ByteBuffer.allocate(4).putInt(SN)).array());
	}
	
	/**
	 * retourne la representation du message en String  
	 * @return representation en String 
	 **/
	public String toString()
	{
		return "Introduction Message "+" instancied by "+this.getSender()+" to "+DSDV_Frame.receiverIdToString(this.getReceiver())+ ", SN is " + this.get_SN();
	}


}
