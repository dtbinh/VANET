package simulation.solutions.custom.DSDV.Messages;

/**
 * le message DSDV qui permet d'acquitter une données reçue 
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

@SuppressWarnings("serial")
public class DSDV_Message_Acknowledgement extends DSDV_Message{

	/**
	 * constructeur pour l'initialisation du message d'acquittement
	 * @param sender le destinateur de l'acquittement
	 * @param receiver le destinataire de l'acquittement
	 */
	public DSDV_Message_Acknowledgement(int sender,int receiver)
	{
		super(sender,receiver,DSDV_Message.msgACKNOWLEDGEMENT);
	}
	
	/**
	 * constructeur pour l'initialisation du message d'acquittement à partir d'un message de données  
	 * @param msg le message de données
	 */
	public DSDV_Message_Acknowledgement(DSDV_Message_Data msg)
	{
		super(msg.getReceiver(),msg.getSender(), DSDV_Message.msgACKNOWLEDGEMENT);
	}
	
	/**
	 * retourne la representation du message en une suite de bytes  
	 * @return representation en bytes
	 **/
	public  byte[] toByteSequence() 
	{
		return super.toByteSequence();
	}
	
	/**
	 * retourne la representation du message en String  
	 * @return representation en String 
	 **/
	public String toString()
	{
		return "Acknowledgment instancied by "+this.getSender()+" to "+this.getReceiver();
	}

}
