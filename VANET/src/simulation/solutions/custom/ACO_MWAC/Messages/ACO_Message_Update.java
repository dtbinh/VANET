package simulation.solutions.custom.ACO_MWAC.Messages;

import java.nio.ByteBuffer;


/**
 * le message de mise à jour de l'energie residuelles
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */

@SuppressWarnings("serial")
public class ACO_Message_Update extends ACO_Message{
	
	//le niveau d'energie
	private float EnergyLevel;

	/**
	 * constructeur 
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param data suite de bytes
	 */
	public ACO_Message_Update (int sender, int receiver, byte [] data){
		
		super(sender, receiver, ACO_Message.msgUPDATE);
		
		ByteBuffer buf=ByteBuffer.wrap(data);
		
		this.EnergyLevel = buf.getFloat();
	}
	
	/**
	 * constructeur 
	 * @param sender le destinateur 
	 * @param receiver le destinataire
	 * @param EnergyLevel le niveau d'energie 
	 */
	public ACO_Message_Update (int sender, int receiver, float EnergyLevel){
		
		super(sender,receiver, ACO_Message.msgUPDATE);
		
		this.EnergyLevel = EnergyLevel;
	}
	
	
	public float get_Energy_Level(){
		return this.EnergyLevel;
	}

	/**
	 * retourne la representation de ce message en bytes
	 * @return la representation en bytes
	 **/
	public  byte[] toByteSequence() 
	{
		ByteBuffer buf = ByteBuffer.allocate(4);
		
		buf.putFloat(this.EnergyLevel);
		
		return super.toByteSequence(buf.array());
	}

	/**
	 * retourne la representation de ce message en String    
	 * @return la representation en String 
	 **/
	public String toString()
	{
		return "ACO_Update_Message instancied by "+this.getSender()+" to "+ACO_Frame.receiverIdToString(this.getReceiver())+", Energy Level is "+this.get_Energy_Level();
	}

}
