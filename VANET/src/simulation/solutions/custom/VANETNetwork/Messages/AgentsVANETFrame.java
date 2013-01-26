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
	//TODO: Verifier la structure de donnée suivante : 
	//	- Une frame contient les ID brutes de l'envoyeur 
	//	- Dans un message on stock la cible-type du message (ex: voiture)
	
	
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
	 * @return Message un objet message
	 */
	public Message getMessage() //throws InvalidFrameException ?
	{
		ByteBuffer buffer = ByteBuffer.wrap(this.getData());
		byte type= buffer.get();

		if (type == AgentsVANETMessage.DIRE_QUI_PEUT_PASSER) 
			return new AgentsVANETMessage(buffer.getInt(), buffer.getInt(), type, buffer.getInt());

		System.out.println("Error in the encapsulation of the frame data");
		return null;		
	}
}


