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
	
	private IntegerPosition positionAgent;	
	public int typeEnvoyeur;
	private int typeReceveur;
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
	public int typeMessage;
	
	/**
	 * Constantes pouvant �tre stock�es dans l'attribut typeMessage.
	 */
	public static final int VOIE_LIBRE=0;
	public static final int ECHANGE_DE_POSITION=1;
	
	/**
	 * Constructeur de message 
	 * @param typeReceveur
	 * @param typeEnvoyeur
	 * @param pos
	 * @param typeMessage
	 */
	
	public AgentsVANETMessage(int typeReceveur, int typeEnvoyeur,IntegerPosition pos, int typeMessage)
	{
		this.typeEnvoyeur=typeEnvoyeur;
		this.setTypeReceveur(typeReceveur);
		this.typeMessage=typeMessage;
		this.positionAgent=pos;
	}
	
	/**
	 * Liste des diff�rents accesseurs en lecture des attributs d'un objet message
	 * @return
	 */
	
	public int getTypeReceveur() 
	{
		return typeReceveur;
	}	
	
	public int getTypeEnvoyeur()
	{
		return this.typeEnvoyeur;
	}
	
	public int getTypeMessage()
	{
		return this.typeMessage;
	}	
	
	public IntegerPosition getPositionAgent() 
	{
		return positionAgent;
	}
	
	/**
	 * Liste des accesseurs en �criture des attributs de l'objet message  
	 * @return
	 */	
	
	public void setTypeEnvoyeur(int nouvTypeEnvoyeur)
	{
		this.typeEnvoyeur=nouvTypeEnvoyeur;
	}
	
	public void setTypeReceveur(int typeReceveur) 
	{
		this.typeReceveur = typeReceveur;
	}
	
	public void setPositionAgent(IntegerPosition positionAgent) 
	{
		this.positionAgent = positionAgent;
	}	
	
	public void setTypeMessage(int nouvTypeMessage)
	{
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
		//On pr�pare le messages, i�i la taille 500 sera � r�duire 
		//TODO : Trouver la fonction permettant d'avoir une taille adapt�e 
		return (ByteBuffer.allocate(500).putInt(typeReceveur).putInt(typeEnvoyeur).putInt(positionAgent.x).putInt(positionAgent.y).putInt(typeMessage).array());
	
	}
	
	/**
	 * M�thode inverse de toByteSequence : d�cortique un tableau de byte et en fait un Message
	 * L'ordre est important
	 * @param data
	 * @return le message extrait
	 */
	
	 public AgentsVANETMessage arrayToMessage(byte[] data)
	 {
		 AgentsVANETMessage messageExtrait = new AgentsVANETMessage(data[1],data[2], new IntegerPosition(data[3],data[4]),data[5] );
		 return messageExtrait;
	 }

	/* (non-Javadoc)
	 * @see simulation.messages.Message#getReceiver()
	 */
	@Override
	public int getReceiver() {
		// TODO Auto-generated method stub
		//FIXME c'est cens� �tre quoi, l� ? idem pour sender
		return 0;
	}

	/* (non-Javadoc)
	 * @see simulation.messages.Message#getSender()
	 */
	@Override
	public int getSender() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
