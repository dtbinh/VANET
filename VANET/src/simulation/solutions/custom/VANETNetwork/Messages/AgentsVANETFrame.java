/**
 * Classe en construction
 */
package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;

/**
 * Classe définissant un objet Frame.
 * 
 * Note: Pour plus d'information -> Checkez la classe machineFrame.java 
 * @author Reykjanes
 *
 */
@SuppressWarnings("serial")
public class AgentsVANETFrame extends Frame 
{
	/** 
	 * Constructeur d'une frame
	 * @param sender ID de l'expediteur
	 * @param receiver ID du receveur, dont une des valeurs possibles est BROADCAST
	 * @param data le tableau de byte construit grace à la classe AgentVANETMessage, 
	 * c'est un transcription des informations contenues dans l'objet message en tableau de byte
	 */
	public AgentsVANETFrame(int sender, int receiver,byte[] data)
	{
		super(sender,receiver,data);
	}
	
	public AgentsVANETFrame(int sender, int receiver,Message msg){
		super(sender,receiver,msg);
	}
	
	/**
	 * Méthode extrayant les données de l'attribut message dans un ordre précis puisque celui-ci a été formaté.
	 * @return l'objet Message construit à partir des données contenues dans cette Frame
	 */
	public Message getMessage() //throws InvalidFrameException ?
	{
		AgentsVANETMessage nouvMsg = null;
		ByteBuffer buffer = ByteBuffer.wrap(this.getData());
		byte type= buffer.get();

		if (type == AgentsVANETMessage.DIRE_QUI_PEUT_PASSER){
			nouvMsg = new AgentsVANETMessage(buffer.getInt(), buffer.getInt(), type);
		 	nouvMsg.setVoieLibre(buffer.getInt());
		}
		
		else if (type == AgentsVANETMessage.DIFFUSION_TRAJET){
			nouvMsg = new AgentsVANETMessage(buffer.getInt(), buffer.getInt(), type);
			nouvMsg.setTTLMessage(buffer.getInt());
			
			int nbEtapesARajouterAuMessage = buffer.getInt();
			while (nbEtapesARajouterAuMessage > 0) {
				nouvMsg.parcoursMessage.add(buffer.getInt()); 
				nbEtapesARajouterAuMessage--;
			}
		}		
		else if (type == AgentsVANETMessage.INDIQUER_DIRECTION) {
			nouvMsg = new AgentsVANETMessage(buffer.getInt(), buffer.getInt(), type);
		}
		else
			System.out.println("\nError in the encapsulation of the frame data\n");
		
		return nouvMsg;
	}
	
	/*
	 * FIXME Il semblerait qu'Old Performances Analysis ne fonctionne pas quand on redéfinit toString, pourquoi ?
	 * public String toString() {
		return "Frame from "+this.getSender()+" to "+this.getReceiver();
	}*/
}


