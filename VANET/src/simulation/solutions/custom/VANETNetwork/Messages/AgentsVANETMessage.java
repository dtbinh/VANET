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
	 * Un message est un objet stockant des informations, lors de l'envoi ces informatiions seront stockées dans une int[] normalisé	
	 */
	
	private IntegerPosition positionAgent;	
	public int typeEnvoyeur;
	private int typeReceveur;
	/**
	 * Constantes codant le type de l'agent
	 */
	//On référence tous lea identifaint des agents	
	public static final int VOITURE=0;
	public static final int FEU_DE_SIGNALISATION=1;
	public static final int CROISEMENT=2;
			//TODO : verifier si ce champs est utile (un croisement,  première vue n'envoie pas de message tout seul, c'est le feu de signalisation par
			//l'intermediaire du croisement qui fait le boulot
	
			//TODO : Si on en a finit avec l'ajout de nouveaux champs, il faut essayer dès que possible mettre des type byte au lieu d'int pour optimiser; 
	/**
	 * Attribut coandt le type de message, celui-ci stocke les constantes ci-dessous
	 */
	public int typeMessage;
	
	/**
	 * Constante pouvant être stockées dans l'attribut typeMessage.
	 */
	public static final int VOIE_LIBRE=0;
	public static final int ECHANGE_DE_POSITION=1;
	
	/**
	 * Costructeur de message 
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
	 * Liste des différents accesseur en lecture des attributs d'un objet message
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
	 * Liste des accesseur en écriture des attributs de l'objet message  
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
	 * Méthode permettant de transcrire les informations de l'objet FeuDeSignalisation 
	 * dans un tableau de Byte
	 * 
	 * Fonctions extrêmement importante, elle code en tableau de byte les attributs de l'objet message en vue d'une expedition.
	 * 
	 * NOTE: L'ordre est important et va induire un décodage lors de l'extraction des informations dans cet ordre précis
	 * @param data 
	 * @return
	 */
	public byte[] toByteSequence()
	{
		//On prépare le messages, içi la taille 500 sera à réduire 
		//TODO : Trouver al fonction permettant d'avoir une taille adaptée 
		return (ByteBuffer.allocate(500).putInt(typeReceveur).putInt(typeEnvoyeur).putInt(positionAgent.x).putInt(positionAgent.y).putInt(typeMessage).array());
	
	}
	
	/**
	 * Méthode permettant de transcrire les information de l'objet FeuDeSignalisation 
	 * dans un tableau de Byte
	 * 
	 * Fonctions extrêmement importante, elle code en tableau de byte les attributs de l'objet message en vue d'une expedition.
	 * 
	 * NOTE: L'ordre est important car la fonction de codage est liée à la fonction de décodage
	 * @param data 
	 * @return
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
