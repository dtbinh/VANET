package simulation.solutions.custom.DSDV.Messages;

import simulation.solutions.custom.DSDV.Entry_routing_table;

/**
 * le message DSDV permettant de transporter une entrée de la table de routage
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
@SuppressWarnings("serial")
public class DSDV_Message_Update extends DSDV_Message{

	//l'entrée de la table de routage	
	Entry_routing_table Entry;	

	/**
	 * constructeur pour l'initialisation du message de mise à jour
	 * @param sender destinateur 
	 * @param receiver destinataire 
	 * @param Entry entrée de la table de routage
	 */
	public DSDV_Message_Update(int sender,int receiver,Entry_routing_table Entry)
	{
		super(sender,receiver,DSDV_Message.msgUPDATE);
		this.Entry=new Entry_routing_table(Entry.toByteSequence());
	}
	
	/**
	 * constructeur pour l'initialisation du message de mise à jour
	 * @param sender destinateur
	 * @param receiver destinataire
	 * @param Entry entrée de la table de routage (en bytes)
	 */
	public DSDV_Message_Update(int sender,int receiver,byte[] Entry)
	{
		super(sender,receiver,DSDV_Message.msgUPDATE);
		this.Entry=new Entry_routing_table (Entry);
		
	}
	
	/**
	 * retourne l'entrée de la table de routage transportée par le message de mise à jour 
	 *@return l'entrée
	 */
	public Entry_routing_table get_Entry()
	{
		return this.Entry;
	}

	/**
	 * retourne la representation du message en une suite de bytes  
	 * @return representation en bytes
	 **/
	public  byte[] toByteSequence() 
	{	
		return super.toByteSequence(this.Entry.toByteSequence());
	}
	
	/**
	 * retourne la representation du message en String  
	 * @return representation en String 
	 **/
	public String toString()
	{
		return "Update Message ["+this.Entry.toString()+"]"+" instancied by "+this.getSender()+" to "+DSDV_Frame.receiverIdToString(this.getReceiver());
	}


}
