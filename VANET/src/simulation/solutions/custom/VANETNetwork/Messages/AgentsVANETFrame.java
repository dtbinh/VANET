/**
 * Classe en construction
 */
package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;

import simulation.messages.Frame;
import simulation.messages.Message;
import simulation.solutions.custom.VANETNetwork.Exceptions.InvalidFrameException;
import simulation.utils.IntegerPosition;

/**
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
	 * @param receiver ID du receveur, dont une des valeur est BROADCAST
	 * @param data le tableau de byte construit grace à la classe AgentVANETMessage, 
	 * 				c'est un transcription des informations contenues dans l'objet message en tableau de byte
	 */
	public AgentsVANETFrame(int sender, int receiver,byte[] data)
	{
		super(sender,receiver,data);
	}
	
	@Override
	public Message getMessage() //throws InvalidFrameException ?
	{
		ByteBuffer buffer = ByteBuffer.wrap(this.getData());
		byte type= buffer.get();
		
		//En construction : Le return doit retourner un message contenant les informations envoyée dans la frame 
		try {if(type == AgentsVANETMessage.VOIE_LIBRE)
			return new AgentsVANETMessage(this.d);}
		catch (Exception InvalidFrameException){;}		
	
	}
}


