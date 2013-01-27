package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;
import simulation.messages.Message;
import simulation.utils.IntegerPosition;


/**
 * @author Reykjanes
 *
 */
@SuppressWarnings("serial")
public class AgentsVANETMessage extends Message{
	
	//TODO verifier si je dis pas de conner*es
	/**
	 * Principaux attributs d'un message.
	 * 
	 * Un message est un objet stockant des informations, lors de l'envoi ces informations seront stock�es dans une int[] normalis�	
	 */
	
	/**
	 * Pour certains messages, ni la position de l'envoyeur ni celle du receveur n'importent.
	 * FIXME remanier le constructeur ou en cr�er d'autres lorsqu'on s'en servira
	 */
	private IntegerPosition positionAgent;	

	private int senderID;
	private int receiverID;
	/**
	 * Utilis� dans les messages de feux rouges.
	 * Contient l'id de la voie (du Croisement qu'on trouve au bout de cette voie) qui est au vert. Une et une seule voie par Croisement peut �tre au vert.
	 */
	private int voieLibre;
	/**
	 * Constantes codant le type de l'agent
	 */
	//On r�f�rence tous les identifiants des agents	
	public static final int VOITURE=0;
	public static final int FEU_DE_SIGNALISATION=1;
	public static final int CROISEMENT=2;
			//TODO : verifier si ce champ est utile (un croisement, � premi�re vue n'envoie pas de message tout seul, c'est le feu de signalisation par
			//l'intermediaire du croisement qui fait le boulot
	
			//TODO : Si on en a fini avec l'ajout de nouveaux champs, il faut essayer d�s que possible de mettre des type byte au lieu d'int pour optimiser; 
	/**
	 * Attribut codant le type de message, celui-ci stocke les constantes ci-dessous
	 */
	private byte typeMessage;
	
	/**
	 * Constantes pouvant �tre stock�es dans l'attribut typeMessage.
	 * Indiquent le "genre de message"
	 */
	public static final byte VOIE_LIBRE=0;//FIXME peut-etre � enlever si c'est inclus dans DIRE_QUI_PEUT_PASSER
	public static final byte ECHANGE_DE_POSITION=1;//FIXME trouver mieux que ce nom tout pourri
	public static final byte DIRE_QUI_PEUT_PASSER=2;
	
	/**
	 * Constructeur de message.
	 * Il est possible d'avoir plusieurs constructeurs en fonction des besoins, ou sinon on peut passer tous les param�tres, 
	 * en mettant 0 ou null pour ceux qui ne servent pas dans les appels qui ne les utilisent pas
	 */
	
	public AgentsVANETMessage(int sender, int receiver, byte type, int idVoieAuVert)
	{
		this.senderID=sender;
		this.receiverID=receiver;
		this.typeMessage=type;
		this.voieLibre = idVoieAuVert;//voieLibre est un int, on retransformera en Croisement plus tard... ou pas (au choix, en fonction des besoins)
	}
	
	/**
	 * Liste des diff�rents accesseurs en lecture des attributs d'un objet message
	 * @return
	 */
	
	public byte getTypeMessage() {
		return this.typeMessage;
	}	
	
	public IntegerPosition getPositionAgent() {
		return positionAgent;
	}
	
	/**
	 * Liste des accesseurs en �criture des attributs de l'objet message  
	 * @return
	 */	
	
	public void setPositionAgent(IntegerPosition positionAgent) {
		this.positionAgent = positionAgent;
	}	
	
	/// FIXME Est-ce vraiment utile ?
	public void setTypeMessage(byte nouvTypeMessage) {
		this.typeMessage=nouvTypeMessage;
	}
	

	/**
	 * M�thode permettant de transcrire les informations de l'objet FeuDeSignalisation 
	 * dans un tableau de Byte
	 * 
	 * Fonction extr�mement importante, elle code en tableau de byte les attributs de l'objet message en vue d'une exp�dition.
	 * 
	 * NOTE: L'ordre est important et va induire un d�codage lors de l'extraction des informations dans cet ordre pr�cis
	 * @return le message sous forme de byte[]
	 */
	public byte[] toByteSequence()
	{
		//TODO : Trouver la fonction permettant d'avoir une taille adapt�e 
		if(this.typeMessage==DIRE_QUI_PEUT_PASSER)
			return ByteBuffer.allocate(50).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.voieLibre).array();
		//else if == ...
		else
		{
			System.out.println("PROBLEME : Un message ne poss�de pas un typeMessage coh�rent");
			return null;
		}
	}
	
	/**
	 * M�thode inverse de toByteSequence : d�cortique un tableau de byte et en fait un Message
	 * L'ordre est important
	 * @param data
	 * @return le message extrait
	 */

	@Override
	public int getReceiver() {
		return this.receiverID;
	}
	
	@Override
	public int getSender() {
		return this.senderID;
	}
	
	public int getVoieLibre(){
		return this.voieLibre;
	}

}
