package simulation.solutions.custom.ACO_MWAC.Messages;

import java.nio.ByteBuffer;

/**
 * message d'initialisation des tables de routage (pheromone, Sequence Number et niveaux d'energie)
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

@SuppressWarnings("serial")
public class ACO_Message_Initialization extends ACO_Message{
		
	//le niveau d'energie
	private float EnergyLevel;
	
	//le nombre de sauts reliant la station de base au reste du reseau
	private int HopCount;
	
	/**
	 * constructeur 
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param data suite de bytes
	 */
	public ACO_Message_Initialization (int sender, int receiver, byte [] data){
		
		super(sender,receiver,ACO_Message.msgINITIALIZATION);
		
		ByteBuffer buf=ByteBuffer.wrap(data);
		
		this.EnergyLevel = buf.getFloat();
		this.HopCount = buf.getInt();
	}
	
	
	/**
	 * constructeur 
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param energy_level le niveau d'energie
	 * @param hop le nombre de sauts
	 */
	public ACO_Message_Initialization (int sender, int receiver, float EnergyLevel, int HopCount){
		
		super(sender, receiver, ACO_Message.msgINITIALIZATION);

		this.EnergyLevel = EnergyLevel;
		this.HopCount = HopCount;
	}
	
	
	public float get_EnergyLevel(){
		return this.EnergyLevel;
	}
	public int get_HopCount(){
		return this.HopCount;
	}

	
	/**
	 * retourne la representation de ce message en bytes  
	 * @return representation en bytes
	 **/
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf = ByteBuffer.allocate(8); 
		
		buf.putFloat(this.EnergyLevel);
		buf.putInt(this.HopCount);
		
		return super.toByteSequence(buf.array());
	}

	
	/**
	 * retourne la representation en String de ce message   
	 * @return la representation en String 
	 **/
	public String toString()
	{
		return "ACO_Initialization_Message instancied by "+getSender()+" to "+ACO_Frame.receiverIdToString(getReceiver())+ ", Energy is "+get_EnergyLevel()+", Hop Count is "+get_HopCount();
	}

}
