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
 * Classe d�finissant un objet Frame.
 * 
 * Note: Pour plus d'information -> Checkez la classe machineFrame.java 
 * @author Reykjanes
 *
 */
@SuppressWarnings("serial")
public class AgentsVANETFrame extends Frame 
{
	//TODO: Verifier la structure de donn�e suivante : 
	//	- Une frame contient les ID brutes de l'envoyeur 
	//	- Dans un message on stock la cible-type du message (ex: voiture)
	
	
	/** 
	 * Constructeur d'une frame
	 * @param sender ID de l'expediteur
	 * @param receiver ID du receveur, dont une des valeur est BROADCAST
	 * @param data le tableau de byte construit grace � la classe AgentVANETMessage, 
	 * 				c'est un transcription des informations contenues dans l'objet message en tableau de byte
	 */
	public AgentsVANETFrame(int sender, int receiver,byte[] data)
	{
		super(sender,receiver,data);
	}
	
	/**
	 * M�thode extrayant les donn�es de l'attribut message dans un ordre pr�cis puisque celui-ci a �t� format�.
	 * @return Message un objet message
	 */
	public Message getMessage() //throws InvalidFrameException ?
	{
		ByteBuffer buffer = ByteBuffer.wrap(this.getData());
		int typeReceveur = buffer.getInt();
		int typeEnvoyeur = buffer.getInt();
		IntegerPosition positionAgent = new IntegerPosition(buffer.getInt(),buffer.getInt());
		int typeMessage = buffer.getInt();		

			return new AgentsVANETMessage(typeReceveur,typeEnvoyeur, positionAgent,typeMessage);		
	}
	
	
	 //TODO impl�menter m�thode permettant de filtrer le message en fonction e l'id receiver et du type receveur;
}


