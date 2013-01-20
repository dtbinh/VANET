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
	
	//==========Informations nécessaire à l'utilisation d'un message=============//
	
	
	private IntegerPosition positionAgent;	
	public int typeEnvoyeur;
	private int typeReceveur;
	
	//On référence tous lea identifaint des agents	
	public static final int VOITURE=0;
	public static final int FEU_DE_SIGNALISATION=1;
	public static final int CROISEMENT=2;
			//TODO : verifier si ce champs est utile (un croisement,  première vue n'envoie pas de message tout seul, c'est le feu de signalisation par
			//l'intermediaire du croisement qui fait le boulot
	
			//TODO : Si on en a finit avec l'ajout de nouveaux champs, il faut essayer dès que possible mettre des type byte au lieu d'int pour optimiser; 
	/**
	 * Un champs d'une frame sera le type de message
	 */
	public int typeMessage;
	
	// On référence tout les types de message (liste provisoire) içi voie libre sert a identifer le message lorsqu'une
	public static final int VOIE_LIBRE=0;
	public static final int ECHANGE_DE_POSITION=1;
	
	//===Constructeur de messages ==//
	
	public AgentsVANETMessage(int typeEnvoyeur, int typeReceveur,IntegerPosition pos, int typeMessage)
	{
		this.typeEnvoyeur=typeEnvoyeur;
		this.setTypeReceveur(typeReceveur);
		this.typeMessage=typeMessage;
		this.positionAgent=pos;
	}
	
	//====================METHODE DE MANIPULATION DES MESSAGES===============//
	
	public int getTypeReceveur() 
	{
		return typeReceveur;
	}

	public void setTypeReceveur(int typeReceveur) 
	{
		this.typeReceveur = typeReceveur;
	}
	
	public int getTypeEnvoyeur()
	{
		return this.typeEnvoyeur;
	}
	
	public void setTypeEnvoyeur(int nouvTypeEnvoyeur)
	{
		this.typeEnvoyeur=nouvTypeEnvoyeur;
	}
	
	public IntegerPosition getPositionAgent() 
	{
		return positionAgent;
	}
	
	public void setPositionAgent(IntegerPosition positionAgent) 
	{
		this.positionAgent = positionAgent;
	}
	
	public int getTypeMessage()
	{
		return this.typeMessage;
	}
	
	public void setTypeMessage(int nouvTypeMessage)
	{
		this.typeMessage=nouvTypeMessage;
	}
	
	
	
	//==============METHODE DE NORMALISATION DU MESSAGE EN VUE D'UNE INTEGRATION DANS LA FRAME=========//
	
	/**
	 * Méthode permettant de transcrire les information de l'objet FeuDeSignalisation 
	 * dans un tableau de Byte
	 */
	
	public byte[] toByteSequence()
	{
		//On prépare le messages, içi la taille 500 sera à réduire 
		//TODO : Trouver al fonction permettant d'avoir une taille adaptée 
		return (ByteBuffer.allocate(500).putInt(positionAgent.x).putInt(positionAgent.y).putInt(typeEnvoyeur).putInt(typeReceveur).putInt(typeMessage).array());
	
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
