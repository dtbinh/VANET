package simulation.solutions.custom.VANETNetwork.Messages;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import simulation.messages.Message;
import simulation.solutions.custom.VANETNetwork.Croisement;
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
	 * 
	 * Un message est un objet stockant des informations, lors de l'envoi ces informations seront stockées dans une int[] normalisé	
	 */
	
	/**
	 * Pour certains messages, ni la position de l'envoyeur ni celle du receveur n'importent.
	 * FIXME (FIXED) remanier le constructeur ou en créer d'autres lorsqu'on s'en servira : Il suffit de ne pas inclure le traitement des position dans le receiverame(); 
	 */
	private IntegerPosition positionAgent;	
	
	private int senderID;
	private int receiverID;
	private int capaciteMessage;
	private int TTLMessage;
	
	//Flemme de faire les accesseurs d'où -> public TODO: Quand j'ai le temps optimiser ce foutu code
	public List<Croisement>parcoursMessage= new LinkedList<Croisement>();
	
	/**
	 * Utilisé dans les messages de feux rouges.
	 * Contient l'id de la voie (du Croisement qu'on trouve au bout de cette voie) qui est au vert. Une et une seule voie par Croisement peut être au vert.
	 */
	private int voieLibre;
	/**
	 * Constantes codant le type de l'agent
	 */
	
	//On référence tous les identifiants des agents	
	public static final int VOITURE=0;
	public static final int FEU_DE_SIGNALISATION=1;
	public static final int CROISEMENT=2;
			//TODO : verifier si ce champ est utile (un croisement, à première vue n'envoie pas de message tout seul, c'est le feu de signalisation par
			//l'intermediaire du croisement qui fait le boulot
	
	 
	/**
	 * Attribut codant le type de message, celui-ci stocke les constantes ci-dessous
	 */
	private byte typeMessage;
	
	/**
	 * Constantes pouvant être stockées dans l'attribut typeMessage.
	 * Indiquent le "genre de message"
	 */
	public static final byte VOIE_LIBRE=0;//FIXME peut-etre à enlever si c'est inclus dans DIRE_QUI_PEUT_PASSER
	public static final byte ECHANGE_DE_POSITION=1;//FIXME FIXED trouver mieux que ce nom tout pourri : Non je le trouve cool !
	public static final byte DIRE_QUI_PEUT_PASSER=2;
	public static final byte DIFFUSION_TRAJET=3;
	
	//Ces attributs correspondent à la capaciteMessage, et le TTL pour éviter une congestion. 
	public static final byte TTL_OPTIMAL=5;
	public static final byte CAPACITE_MESSAGE=5;
	
	/**
	 * Constructeur de message.
	 * Il est possible d'avoir plusieurs constructeurs en fonction des besoins, ou sinon on peut passer tous les paramètres, 
	 * en mettant 0 ou null pour ceux qui ne servent pas dans les appels qui ne les utilisent pas
	 */
	//TODO: FIXED corriger ce constructeur et le faire générique !! On fera des "initVoieLibre" pour typer e remplir le message
	public AgentsVANETMessage(int sender, int receiver, byte type)
	{
		this.senderID=sender;
		this.receiverID=receiver;
		this.typeMessage=type;		
	}
	
	public void initTrajetMessage(AgentsVANETMessage msg, Croisement derCrois,Croisement dest){
		
		msg.setCapaciteMessage(AgentsVANETMessage.CAPACITE_MESSAGE);
		msg.setTTLMessage(TTL_OPTIMAL);
	
		
		for (int i=0; i < CAPACITE_MESSAGE; i++){
			parcoursMessage[i]=-1;}
		
		parcoursMessage[0]=dest.getUserId();
		parcoursMessage[1]=derCrois.getUserId();
		
		msg.setCapaciteMessage(CAPACITE_MESSAGE-2);		
	}
		
	/**
	 * Liste des différents accesseurs en lecture des attributs d'un objet message
	 * @return
	 */
	
	public byte getTypeMessage() {
		return this.typeMessage;
	}	
	
	public IntegerPosition getPositionAgent() {
		return positionAgent;
	}
	
	public int getCapaciteMessage(){
		return this.capaciteMessage;
	}
	 
	public int getTTLMessage(){
		return this.TTLMessage;
	}
	
	/**
	 * Liste des accesseurs en écriture des attributs de l'objet message  
	 * @return
	 */	
	
	public void setPositionAgent(IntegerPosition positionAgent) {
		this.positionAgent = positionAgent;
	}	
	
	// FIXME (FIXED) Est-ce vraiment utile ? OUI !
	public void setTypeMessage(byte nouvTypeMessage) {
		this.typeMessage=nouvTypeMessage;
	}
	
	public void setCapaciteMessage(int nouvCapa){
		this.capaciteMessage=nouvCapa;
	}
	
	public void setVoieLibre(int nouvID){
		this.voieLibre=nouvID;
	}
	
	public void setTTLMessage(int nouvTTL)
	{
		this.TTLMessage=nouvTTL;
	}
	/**
	 * Méthode permettant de transcrire les informations de l'objet FeuDeSignalisation 
	 * dans un tableau de Byte
	 * 
	 * Fonction extrêmement importante, elle code en tableau de byte les attributs de l'objet message en vue d'une expédition.
	 * 
	 * NOTE: L'ordre est important et va induire un décodage lors de l'extraction des informations dans cet ordre précis
	 * @return le message sous forme de byte[]
	 */
	public byte[] toByteSequence()
	{
		//TODO : Trouver la fonction permettant d'avoir une taille adaptée 
		if(this.typeMessage==DIRE_QUI_PEUT_PASSER)
			return ByteBuffer.allocate(50).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.voieLibre).array();

		else if (this.typeMessage==DIFFUSION_TRAJET){
			ByteBuffer res = ByteBuffer.allocate(100).put(this.typeMessage).putInt(this.senderID).putInt(receiverID).putInt(this.capaciteMessage).putInt(this.TTLMessage);
			for (int i=0; i <TTL_OPTIMAL-this.capaciteMessage;i++)
				res.putInt(this.parcoursMessage[i]);
			
			return res.array();
		}
		//else if == ...
		else
		{
			System.out.println("PROBLEME : Un message ne possède pas un typeMessage cohérent");
			return null;
		}
	}
	
	/**
	 * Méthode inverse de toByteSequence : décortique un tableau de byte et en fait un Message
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
